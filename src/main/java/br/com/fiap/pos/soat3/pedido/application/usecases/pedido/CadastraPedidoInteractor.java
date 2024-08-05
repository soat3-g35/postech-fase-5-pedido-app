package br.com.fiap.pos.soat3.pedido.application.usecases.pedido;

import br.com.fiap.pos.soat3.pedido.application.gateways.PedidoGateway;
import br.com.fiap.pos.soat3.pedido.domain.entity.Pedido;
import br.com.fiap.pos.soat3.pedido.infrastructure.integration.messaging.pedidogerado.PedidoGeradoPublisher;
import org.springframework.transaction.annotation.Transactional;

public class CadastraPedidoInteractor {
    private final PedidoGateway pedidoGateway;
    private final PedidoGeradoPublisher pedidoGeradoPublisher;


    public CadastraPedidoInteractor(PedidoGateway pedidoGateway, PedidoGeradoPublisher pedidoGeradoPublisher) {
        this.pedidoGateway = pedidoGateway;
        this.pedidoGeradoPublisher = pedidoGeradoPublisher;
    }

    @Transactional
    public Pedido cadastraPedido(Pedido pedido) {
        pedido = pedidoGateway.cadastraPedido(pedido);
        pedidoGeradoPublisher.publishMessage(pedido);
        return pedido;
    }

}
