package br.com.fiap.pos.soat3.pedido.infrastructure.integration.messaging.pedidogerado;


public class PedidoGeradoMessage {

    private String pedidoId;
    private String clienteId;
    private String status;
    private String totalPedido;

    public PedidoGeradoMessage(String pedidoId, String clienteId, String status, String totalPedido) {
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.status = status;
        this.totalPedido = totalPedido;
    }

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(String totalPedido) {
        this.totalPedido = totalPedido;
    }
}
