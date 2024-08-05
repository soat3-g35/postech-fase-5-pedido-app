package br.com.fiap.pos.soat3.pedido.infrastructure.config;

import br.com.fiap.pos.soat3.pedido.application.gateways.PedidoGateway;
import br.com.fiap.pos.soat3.pedido.application.usecases.pedido.AtualizaStatusPedidoInteractor;
import br.com.fiap.pos.soat3.pedido.application.usecases.pedido.CadastraPedidoInteractor;
import br.com.fiap.pos.soat3.pedido.application.usecases.pedido.ConsultaStatusPedidoInteractor;
import br.com.fiap.pos.soat3.pedido.application.usecases.pedido.ListaPedidosInteractor;
import br.com.fiap.pos.soat3.pedido.application.usecases.pedido.ListaPedidosProducaoInteractor;
import br.com.fiap.pos.soat3.pedido.infrastructure.controllers.pedido.PedidoDTOMapper;
import br.com.fiap.pos.soat3.pedido.infrastructure.gateways.pedido.PedidoEntityMapper;
import br.com.fiap.pos.soat3.pedido.infrastructure.gateways.pedido.PedidoRepositoryGateway;
import br.com.fiap.pos.soat3.pedido.infrastructure.gateways.produto.ProdutoRepositoryGateway;
import br.com.fiap.pos.soat3.pedido.infrastructure.integration.messaging.pedidogerado.PedidoGeradoPublisher;
import br.com.fiap.pos.soat3.pedido.infrastructure.persistence.itempedido.ItemPedidoRepository;
import br.com.fiap.pos.soat3.pedido.infrastructure.persistence.pedido.PedidoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidoBeanConfig {

    @Bean
    AtualizaStatusPedidoInteractor atualizaStatusPedidoUseCase(PedidoGateway pedidoGateway) {
        return new AtualizaStatusPedidoInteractor(pedidoGateway);
    }

    @Bean
    ListaPedidosProducaoInteractor listaPedidosProducaoUseCase(PedidoGateway pedidoGateway) {
        return new ListaPedidosProducaoInteractor(pedidoGateway);
    }

    @Bean
    CadastraPedidoInteractor cadastraPedidoUseCase(PedidoGateway pedidoGateway, PedidoGeradoPublisher pedidoGeradoPublisher) {
        return new CadastraPedidoInteractor(pedidoGateway, pedidoGeradoPublisher);
    }

    @Bean
    PedidoDTOMapper pedidoDTOMapper() {
        return new PedidoDTOMapper();
    }

    @Bean
    ConsultaStatusPedidoInteractor consultaStatusPedidoUseCase(PedidoGateway pedidoGateway) {
        return new ConsultaStatusPedidoInteractor(pedidoGateway);
    }

    @Bean
    ListaPedidosInteractor listaPedidosUseCase(PedidoGateway pedidoGateway) {
        return new ListaPedidosInteractor(pedidoGateway);
    }

     @Bean
    PedidoEntityMapper pedidoEntityMapper() {
        return new PedidoEntityMapper();
    }

    @Bean
    PedidoRepositoryGateway pedidoRepositoryGateway(PedidoRepository pedidoRepository,
                                                    ItemPedidoRepository itemPedidoRepository,
                                                    ProdutoRepositoryGateway produtoRepositoryGateway,
                                                    PedidoEntityMapper pedidoEntityMapper) {
        return new PedidoRepositoryGateway(pedidoRepository, produtoRepositoryGateway, itemPedidoRepository, pedidoEntityMapper);
    }
}
