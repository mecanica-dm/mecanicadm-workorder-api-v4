# language: pt
Funcionalidade: Cancelamento e regras de negócio da OS
  Como oficina, quero que uma OS cancelada fique encerrada, sem nenhuma outra ação possível,
  e que a OS não pule etapas do fluxo.

  Cenário: Cancelar a OS avisa a saga uma única vez
    Dado que a OS foi aberta com a mão de obra "Troca de oleo" e 2 unidades de óleo
    E que a OS foi diagnosticada
    Quando a saga manda cancelar a OS
    Então a OS está "CANCELLED"
    E a Workorder publicou o evento "WorkOrderCanceledEvent" 1 vez

    Quando a saga manda cancelar a OS
    Então a OS está "CANCELLED"
    E a Workorder publicou o evento "WorkOrderCanceledEvent" 1 vez

  Cenário: Uma OS cancelada não aceita nenhuma outra ação
    Dado que a OS foi aberta com a mão de obra "Troca de oleo" e 2 unidades de óleo
    E que a OS foi cancelada

    Quando o mecânico tenta alterar a descrição da OS
    Então a operação é recusada com "work.order.status.not.editable"

    Quando o mecânico tenta adicionar a mão de obra "Alinhamento"
    Então a operação é recusada com "work.order.status.not.editable"

    Quando o mecânico tenta diagnosticar a OS
    Então a operação é recusada com "work.order.status.transition.invalid"

    Quando a saga tenta autorizar a OS
    Então a operação é recusada com "work.order.status.transition.invalid"

    Quando a saga tenta registrar um pedido de alterações
    Então a operação é recusada com "work.order.status.transition.invalid"
    E a OS está "CANCELLED"

  Cenário: Não é possível diagnosticar uma OS sem mão de obra
    Dado que a OS foi aberta sem nenhuma mão de obra
    Quando o mecânico tenta diagnosticar a OS
    Então a operação é recusada com "work.order.labor.required"
    E a OS está "RECEIVED"
    E os eventos publicados, em ordem, são "WorkOrderCreatedEvent"

  Cenário: Não é possível autorizar uma OS que ainda não foi diagnosticada
    Dado que a OS foi aberta com a mão de obra "Troca de oleo" e 2 unidades de óleo
    Quando a saga tenta autorizar a OS
    Então a operação é recusada com "work.order.status.transition.invalid"
    E a OS está "RECEIVED"

  Cenário: Não é possível entregar o veículo antes do pagamento
    Dado que a OS foi aberta com a mão de obra "Troca de oleo" e 2 unidades de óleo
    E que a OS foi autorizada
    Quando o veículo é entregue ao cliente sem o pagamento
    Então a operação é recusada com "work.order.status.transition.invalid"
    E a OS está "AWAITING_EXECUTION"

  Cenário: Uma OS entregue não aceita pedido de alterações
    Dado que a OS foi aberta com a mão de obra "Troca de oleo" e 2 unidades de óleo
    E que a OS foi entregue ao cliente
    Quando a saga tenta registrar um pedido de alterações
    Então a operação é recusada com "work.order.status.transition.invalid"
    E a OS está "DELIVERED"
