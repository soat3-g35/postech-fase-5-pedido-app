package br.com.fiap.pos.soat3.pedido.infrastructure.gateways.pedido;

import br.com.fiap.pos.soat3.pedido.application.gateways.PedidoGateway;
import br.com.fiap.pos.soat3.pedido.domain.entity.ItemPedido;
import br.com.fiap.pos.soat3.pedido.domain.entity.Produto;
import br.com.fiap.pos.soat3.pedido.infrastructure.config.exception.EntityNotFoundException;
import br.com.fiap.pos.soat3.pedido.domain.entity.Pedido;
import br.com.fiap.pos.soat3.pedido.domain.entity.StatusPedido;
import br.com.fiap.pos.soat3.pedido.infrastructure.gateways.produto.ProdutoRepositoryGateway;
import br.com.fiap.pos.soat3.pedido.infrastructure.persistence.itempedido.ItemPedidoRepository;
import br.com.fiap.pos.soat3.pedido.infrastructure.persistence.pedido.PedidoEntity;
import br.com.fiap.pos.soat3.pedido.infrastructure.persistence.pedido.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PedidoRepositoryGateway implements PedidoGateway {

    private final Logger log = LoggerFactory.getLogger(PedidoRepositoryGateway.class);

    private final PedidoRepository pedidoRepository;

    private final ProdutoRepositoryGateway produtoRepositoryGateway;

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoEntityMapper pedidoEntityMapper;

    public PedidoRepositoryGateway(PedidoRepository pedidoRepository,
                                   ProdutoRepositoryGateway produtoRepositoryGateway, ItemPedidoRepository itemPedidoRepository,
                                   PedidoEntityMapper pedidoEntityMapper) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepositoryGateway = produtoRepositoryGateway;
        this.itemPedidoRepository = itemPedidoRepository;
        this.pedidoEntityMapper = pedidoEntityMapper;
    }

    @Override
    public Pedido cadastraPedido(Pedido pedido) {

        pedido.setStatus(StatusPedido.GERADO);
        pedido.setTotalPedido(getTotal(pedido.getItensPedido()));
        
        PedidoEntity pedidoEntity = pedidoEntityMapper.toEntity(pedido);
        pedido.setId(pedidoRepository.save(pedidoEntity).getId());
        log.info("SAGA 1: Salva Pedido no DB, pedidoId {}", pedido.getId());
        log.info(String.format("Lanchonete: Pagamento do pedido  %s gerado", pedido.getId()));
        
        return pedido;
    }

    @Override
    public List<Pedido> listaPedidos() {
        try {
            var pedidosEntity = pedidoRepository.findByStatusNot(StatusPedido.FINALIZADO.name());
            return pedidosEntity
                    .stream()
                    .map(pedidoEntityMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EntityNotFoundException("Pedido", "");
        }
    }

    @Override
    public List<Pedido> listaPedidosProducao() {
        try {
            var pedidosEntity = pedidoRepository.findByProducaoStatus();
            return pedidosEntity
                    .stream()
                    .map(pedidoEntityMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EntityNotFoundException("Pedido", "");
        }
    }

    @Override
    public String consultaStatusPedido(Long pedidoId) {
        if (pedidoRepository.existsById(pedidoId)) {
            return pedidoRepository.getReferenceById(pedidoId).getStatus();
        } else {
            throw new EntityNotFoundException("Pedido não existe", pedidoId.toString());
        }
    }

    @Override
    @Transactional
    public Pedido atualizaStatusPedido(Long pedidoId, String status) {
        try {
            if (pedidoRepository.existsById(pedidoId)) {
                PedidoEntity pedidoEntity = pedidoRepository.getReferenceById(pedidoId);
                pedidoEntity.setStatus(status);
                pedidoRepository.save(pedidoEntity);
                return pedidoEntityMapper.toDomain(pedidoEntity);
            } else {
                throw new EntityNotFoundException("Pedido não existe", pedidoId.toString());
            }
        } catch (Exception e) {
            throw new EntityNotFoundException("Erro ao atualizar status pedido", pedidoId.toString());
        }
    }

    private String getTotal(List<ItemPedido> itensPedido) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido itemPedido : itensPedido) {
            Produto produto = produtoRepositoryGateway.buscaProduto(itemPedido.getProduto().getId());
            String valorUnitario = String.valueOf(produto.getValor());
            BigDecimal valor = new BigDecimal(valorUnitario.replaceAll("\\.", "").replace(",", "."));

            BigDecimal multiplicador = new BigDecimal(itemPedido.getQuantidade());

            total = total.add(valor.multiply(multiplicador));
        }
        return String.valueOf(total);
    }
}
