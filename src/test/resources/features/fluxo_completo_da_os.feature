# language: pt
Funcionalidade: Fluxo completo da Ordem de Serviço na Workorder
  Como oficina, quero que a OS percorra todas as situações, da abertura à entrega do veículo,
  e que a Workorder avise a saga a cada passo com um evento.

  Cenário: A OS percorre o fluxo da abertura até a entrega do veículo
    Dado que a OS foi aberta com a mão de obra "Troca de oleo" e 2 unidades de óleo
    Então a OS está "RECEIVED"
    E a Workorder publicou o evento "WorkOrderCreatedEvent"

    Quando o mecânico adiciona a mão de obra "Alinhamento"
    E o mecânico diagnostica a OS
    Então a OS está "DIAGNOSED"
    E a Workorder publicou o evento "WorkOrderDiagnosedEvent"

    Quando a saga manda autorizar a OS
    Então a OS está "AWAITING_EXECUTION"
    E a Workorder publicou o evento "WorkOrderAuthorizedEvent"

    Quando o mecânico inicia a execução
    Então a OS está "IN_EXECUTION"
    E a Workorder publicou o evento "WorkOrderExecutionStartedEvent"

    Quando o mecânico executa todas as mãos de obra
    E o mecânico finaliza a execução
    Então a OS está "EXECUTION_COMPLETED"
    E a Workorder publicou o evento "WorkOrderExecutionFinishedEvent"

    Quando a saga manda liberar o veículo
    Então a OS está "PAID"
    E a Workorder publicou o evento "WorkOrderVehicleReleasedEvent"

    Quando o veículo é entregue ao cliente
    Então a OS está "DELIVERED"
    E os eventos publicados, em ordem, são "WorkOrderCreatedEvent, WorkOrderUpdatedEvent, WorkOrderDiagnosedEvent, WorkOrderAuthorizedEvent, WorkOrderExecutionStartedEvent, WorkOrderExecutionFinishedEvent, WorkOrderVehicleReleasedEvent, WorkOrderVehicleDeliveredEvent"
    E o histórico de situações da OS registra a passagem por "DELIVERED"

  Cenário: O cliente pede alterações, a OS volta para ajuste e segue até a autorização
    Dado que a OS foi aberta com a mão de obra "Troca de oleo" e 2 unidades de óleo
    E que a OS foi diagnosticada
    Quando a saga manda registrar o pedido de alterações "Incluir o alinhamento"
    Então a OS está "CHANGES_REQUESTED"
    E a Workorder publicou o evento "WorkOrderChangesRequestedEvent"

    Quando o mecânico adiciona a mão de obra "Alinhamento"
    E o mecânico diagnostica a OS
    Então a OS está "DIAGNOSED"
    E a Workorder publicou o evento "WorkOrderDiagnosedEvent" 2 vezes

    Quando a saga manda autorizar a OS
    Então a OS está "AWAITING_EXECUTION"
