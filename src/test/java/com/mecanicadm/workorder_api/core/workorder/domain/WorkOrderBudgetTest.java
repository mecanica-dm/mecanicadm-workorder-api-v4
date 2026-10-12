package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderBudgetTest {

    @Test
    @DisplayName("Deve criar orcamento com status inicial PENDING")
    void shouldCreateBudgetWithWaitingApprovalStatus() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), budget.getTotalPrice());
        assertEquals(WorkOrderBudgetStatus.PENDING, budget.getStatus());
    }

    @Test
    @DisplayName("Deve atualizar total do orcamento e voltar para PENDING")
    void shouldUpdateBudgetTotalPriceAndResetStatus() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        budget.send();

        budget.updateTotalPrice(new BigDecimal("150.00"));

        assertEquals(new BigDecimal("150.00"), budget.getTotalPrice());
        assertEquals(WorkOrderBudgetStatus.PENDING, budget.getStatus());
    }

    @Test
    @DisplayName("Deve marcar orcamento como enviado")
    void shouldMarkBudgetAsSent() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        budget.send();

        assertEquals(WorkOrderBudgetStatus.WAITING_DECISION, budget.getStatus());
    }

    @Test
    @DisplayName("Nao deve criar orcamento com ordem de servico nula")
    void shouldNotCreateBudgetWithNullWorkOrder() {
        BigDecimal amount = new BigDecimal("100.00");

        assertThrows(
                NullPointerException.class,
                () -> WorkOrderBudget.create(null, amount)
        );
    }

    @Test
    @DisplayName("Nao deve criar orcamento com valor total nulo")
    void shouldNotCreateBudgetWithNullTotalPrice() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        assertThrows(NullPointerException.class, () -> WorkOrderBudget.create(workOrder, null));
    }

    @Test
    @DisplayName("Nao deve atualizar valor total para nulo")
    void shouldNotUpdateTotalPriceToNull() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        assertThrows(NullPointerException.class, () -> budget.updateTotalPrice(null));
    }

    @Test
    @DisplayName("Deve restaurar orcamento a partir de dados existentes")
    void shouldRestoreBudgetFromExistingData() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrderBudget budget = WorkOrderBudget.restore(
                workOrderId, new BigDecimal("250.00"), WorkOrderBudgetStatus.APPROVED, "Aprovado");

        assertEquals(workOrderId, budget.getWorkOrderId());
        assertEquals(new BigDecimal("250.00"), budget.getTotalPrice());
        assertEquals(WorkOrderBudgetStatus.APPROVED, budget.getStatus());
        assertEquals("Aprovado", budget.getObservation());
    }

    @Test
    @DisplayName("Deve aprovar orcamento com observacao")
    void shouldApproveBudgetWithNote() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        budget.approve("Aprovado sem ressalvas");

        assertEquals(WorkOrderBudgetStatus.APPROVED, budget.getStatus());
        assertEquals("Aprovado sem ressalvas", budget.getObservation());
    }

    @Test
    @DisplayName("Deve aprovar orcamento sem observacao")
    void shouldApproveBudgetWithoutNote() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        budget.approve(null);

        assertEquals(WorkOrderBudgetStatus.APPROVED, budget.getStatus());
        assertNull(budget.getObservation());
    }

    @Test
    @DisplayName("Deve rejeitar orcamento com necessidade de revisao")
    void shouldRejectBudgetWithRevision() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        budget.reject("Valor muito alto", true);

        assertEquals(WorkOrderBudgetStatus.CHANGES_REQUESTED, budget.getStatus());
        assertEquals("Valor muito alto", budget.getObservation());
    }

    @Test
    @DisplayName("Deve rejeitar orcamento sem necessidade de revisao")
    void shouldRejectBudgetWithoutRevision() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        budget.reject("Nao tenho interesse", false);

        assertEquals(WorkOrderBudgetStatus.REJECTED, budget.getStatus());
        assertEquals("Nao tenho interesse", budget.getObservation());
    }

    @Test
    @DisplayName("Nao deve rejeitar orcamento com motivo nulo")
    void shouldNotRejectBudgetWithNullReason() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        assertThrows(NullPointerException.class, () -> budget.reject(null, false));
    }

    @Test
    @DisplayName("Nao deve criar orcamento com valor total zero")
    void shouldNotCreateBudgetWithZeroTotalPrice() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");

        assertThrows(com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions.BudgetTotalPriceInvalid.class,
                () -> WorkOrderBudget.create(workOrder, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Nao deve criar orcamento com valor total negativo")
    void shouldNotCreateBudgetWithNegativeTotalPrice() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");

        assertThrows(com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions.BudgetTotalPriceInvalid.class,
                () -> WorkOrderBudget.create(workOrder, new BigDecimal("-50.00")));
    }

    @Test
    @DisplayName("Nao deve atualizar valor total para zero")
    void shouldNotUpdateTotalPriceToZero() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "VEH-001", "Servico");
        WorkOrderBudget budget = WorkOrderBudget.create(workOrder, new BigDecimal("100.00"));

        assertThrows(com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions.BudgetTotalPriceInvalid.class,
                () -> budget.updateTotalPrice(BigDecimal.ZERO));
    }
}
