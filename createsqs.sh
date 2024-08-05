#!/bin/bash

export AWS_ACCESS_KEY_ID=localstack
export AWS_SECRET_ACCESS_KEY=localstack

awslocal sqs create-queue --queue-name pedido-gerado

echo "created table pedido-gerado"

#awslocal sqs create-queue --queue-name update-pagamento-status
#awslocal sqs list-queues
#awslocal sqs send-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/update-pagamento-status --message-body "{"pedidoId":"47","status":"RECUSADO"}"

#awslocal sqs receive-message --queue-url http://localhost:4566/000000000000/update-pagamento-status   

#awslocal sqs send-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/update-pagamento-status --message-body "Hello World"
 "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/pedido-gerado"
