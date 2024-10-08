package br.com.fiap.pos.soat3.pedido.application.gateways;

import br.com.fiap.pos.soat3.pedido.domain.entity.Cliente;

public interface ClienteGateway {
    Cliente cadastraCliente(Cliente cliente);

    Cliente buscaClientePorCPF(String cpf);

    Boolean deletaClientePorCPF(String cpf);
}
