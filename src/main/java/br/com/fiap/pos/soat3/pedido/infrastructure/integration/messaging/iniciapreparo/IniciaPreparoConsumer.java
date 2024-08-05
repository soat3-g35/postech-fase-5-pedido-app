package br.com.fiap.pos.soat3.pedido.infrastructure.integration.messaging.iniciapreparo;

import br.com.fiap.pos.soat3.pedido.application.usecases.pedido.AtualizaStatusPedidoInteractor;
import br.com.fiap.pos.soat3.pedido.domain.entity.StatusPedido;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class IniciaPreparoConsumer {

    private final Logger log = LoggerFactory.getLogger(IniciaPreparoConsumer.class);

    @Value("${aws.queueName.iniciaPreparo}")
    private String queueName;

    @Autowired
    private final AmazonSQS amazonSQSClient;

    @Autowired
    private final ObjectMapper objectMapper;

    @Autowired
    private AtualizaStatusPedidoInteractor atualizaStatusPedidoInteractor;

    public IniciaPreparoConsumer(AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 500) // It runs every 5 seconds.
    public void consumeMessages() {

        try {
            String queueUrl = amazonSQSClient.getQueueUrl(queueName).getQueueUrl();

            ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage(queueUrl);

            if (!receiveMessageResult.getMessages().isEmpty()) {
                log.info("SAGA 9: Consome preparo iniciado");
                com.amazonaws.services.sqs.model.Message message = receiveMessageResult.getMessages().get(0);
                log.info("Pedido: Read Message from queue: {}", message.getBody());
                String pedidoId = objectMapper.readValue(message.getBody(), String.class);

                log.info("SAGA 10: Atualiza pedido - preparo iniciado, pedidoId {}", pedidoId);
                atualizaStatusPedidoInteractor.atualizaStatusPedido(Long.parseLong(pedidoId), StatusPedido.PREPARACAO.name());

                amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
            }

        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }
}
