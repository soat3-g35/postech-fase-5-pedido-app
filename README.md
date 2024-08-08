
# Tech Challenge - FIAP
Projeto da Pós Tech da FIAP em Software Architecture

## Autoras

✨ [@leonaraqm](https://github.com/leonara) | [@alinedonatoc](https://github.com/alinedonatoc) | [@assouza19](https://github.com/assouza19) ✨

## Descrição do Projeto
Este módulo é responsável pela aplicação de Pedido do nosso sistema de lanchonete.

## Funcionalidades

- CRUD Produto;
- CRUD Cliente;
- CRUD Pedido;
- Listagem de pedidos - visão cliente;
- Listagem de pedidos em produção;

## Arquitetura

### Padrão SAGA - Coreografia
Justificativa para a Escolha

Autonomia dos Serviços: Na coreografia, cada serviço é responsável por executar suas próprias operações e comunicar-se com outros serviços através de eventos. Isso promove a autonomia dos serviços, permitindo que cada um funcione de forma independente.
Escalabilidade: Como não há um ponto central de controle, os serviços podem escalar de forma independente. Isso é particularmente útil para lidar com picos de demanda, como durante horários de almoço e jantar.

Ausência de Ponto Único de Falha: A coreografia elimina o ponto único de falha associado ao orquestrador. Se um serviço falhar, os demais podem continuar operando normalmente, aumentando a resiliência do sistema.
Distribuição de Carga: A responsabilidade pela execução do fluxo de trabalho é distribuída entre os serviços, evitando sobrecarga em um único componente.
Flexibilidade e Evolução:

Evolução Independente dos Serviços: Na coreografia, os serviços podem ser atualizados ou substituídos sem afetar o fluxo geral de trabalho. Isso permite maior flexibilidade para implementar melhorias e novas funcionalidades.
Adaptação a Mudanças: Serviços podem reagir a eventos de forma flexível, permitindo fácil adaptação a mudanças nos requisitos de negócio ou fluxo de trabalho.

#### Desenho da Arquitura
No desenho abaixo é possível ver nosso padrão SAGA: O microserviço de Pagamento publica um Evento ao ter o pagamento confirmado. 
O microserviço de Pedido consome essa informação e atualiza o status para "PAGAMENTO APROVADO".

  
![Diagrama](https://iili.io/JiJntae.md.png)

Também seria possível implementar os seguintes cenários:
Processo de Pedido: O cliente faz um pedido e o serviço de pedidos publica um evento de "Pedido Criado", para que os serviços interessados possam consumir essa informação.

Atualização de Status: Cada etapa do processo (pedido recebido, em preparação, pronto) pode ser publicada como um evento. Se posteriormente for criado um serviço de notificações, ele poderá consumir esses eventos e enviar atualizações ao cliente, de forma automática.

Cadastro de Cliente: O serviço de cadastro publica um evento de "Cliente Cadastrado" após a verificação e armazenamento dos dados. Outros serviços que necessitam dessas informações podem consumir este evento.

## Code Coverage
![Diagrama](https://iili.io/JiHOW7t.md.png)

## Rodando localmente

- Instale o Docker em sua máquina

- Clone o projeto

```bash
  git clone git@github.com:leonara/pos-tech.git
```

- Crie o volume no docker

```bash
  docker volume create data
```

- Inicie o serviço

```bash
  docker-compose -f local-autoservico.yaml up -d
```


### Rodando localmente com k8s

```bash
  kubectl apply -f k8s/db/pvs.yaml
  kubectl apply -f k8s/db/deployment.yaml
  
  kubectl exec -it <postgres_pod_name> -- bash
  psql -U local
  CREATE DATABASE autoservico;

