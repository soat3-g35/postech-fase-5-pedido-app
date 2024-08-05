package br.com.fiap.pos.soat3.pedido.infrastructure.integration.messaging.preparofinalizado;

import br.com.fiap.pos.soat3.pedido.application.usecases.pedido.AtualizaStatusPedidoInteractor;
import br.com.fiap.pos.soat3.pedido.domain.entity.StatusPedido;
import br.com.fiap.pos.soat3.pedido.infrastructure.persistence.pedido.PedidoEntity;
import br.com.fiap.pos.soat3.pedido.infrastructure.persistence.pedido.PedidoRepository;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PreparoFinalizadoConsumer {

    private final Logger log = LoggerFactory.getLogger(PreparoFinalizadoConsumer.class);

    @Value("${aws.queueName.preparoFinalizado}")
    private String queueName;

    @Autowired
    private final AmazonSQS amazonSQSClient;

    @Autowired
    private final ObjectMapper objectMapper;

    @Autowired
    private AtualizaStatusPedidoInteractor atualizaStatusPedidoInteractor;

    @Autowired
    private PedidoRepository pedidoRepository;

    public PreparoFinalizadoConsumer(AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 500) // It runs every 5 seconds.
    public void consumeMessages() {

        try {
            String queueUrl = amazonSQSClient.getQueueUrl(queueName).getQueueUrl();

            ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage(queueUrl);

            if (!receiveMessageResult.getMessages().isEmpty()) {
                log.info("SAGA 12: Consome pedido finalizado");
                com.amazonaws.services.sqs.model.Message message = receiveMessageResult.getMessages().get(0);
                log.info("Pedido: Read Message from queue: {}", message.getBody());
                String pedidoId = objectMapper.readValue(message.getBody(), String.class);
                PedidoEntity pedido = pedidoRepository.findById(Long.parseLong(pedidoId)).orElseThrow(ChangeSetPersister.NotFoundException::new);
                log.info("SAGA 13: Atualiza pedido finalizado, pedidoId {}", pedido.getId());
                atualizaStatusPedidoInteractor.atualizaStatusPedido(pedido.getId(), StatusPedido.FINALIZADO.name());
                log.info("SAGA 14: Notifica cliente, clienteId {}", pedido.getCliente().getId());
                log.info("Notifica Cliente: {}", pedido.getCliente().getId());
                amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
            }

        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }
}
