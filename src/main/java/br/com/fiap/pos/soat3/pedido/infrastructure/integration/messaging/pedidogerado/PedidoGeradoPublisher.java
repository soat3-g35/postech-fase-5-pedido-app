package br.com.fiap.pos.soat3.pedido.infrastructure.integration.messaging.pedidogerado;

import br.com.fiap.pos.soat3.pedido.domain.entity.Pedido;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PedidoGeradoPublisher {

    private final Logger log = LoggerFactory.getLogger(PedidoGeradoPublisher.class);
    
    @Value("${aws.queueName.pedidoGerado}")
    private String queueName;
    private final AmazonSQS amazonSQSClient;
    private final ObjectMapper objectMapper;

    public PedidoGeradoPublisher(AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
    }


    public void publishMessage(Pedido pedido) {
        try {
            GetQueueUrlResult queueUrl = amazonSQSClient.getQueueUrl(queueName);
            var message = new PedidoGeradoMessage(pedido.getId().toString(), pedido.getClienteId().toString(),
                    pedido.getStatus().toString(), pedido.getTotalPedido());
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            amazonSQSClient.sendMessage(queueUrl.getQueueUrl(),
                    objectMapper.writeValueAsString(message));
            log.info("SAGA 2: Publica pedido gerado {}", pedido.getId());
            log.info("Queue Pedido Gerado publicado: {}", pedido.getId());
        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }
    
}

