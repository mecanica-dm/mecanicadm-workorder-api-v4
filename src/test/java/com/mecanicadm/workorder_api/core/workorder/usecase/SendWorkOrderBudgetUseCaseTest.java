package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.shared.domain.port.EmailService;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.BudgetDecisionTokenGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.SendWorkOrderBudgetCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendWorkOrderBudgetUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;
    @Mock
    private BudgetDecisionTokenGateway tokenGateway;
    @Mock
    private ClientGateway clientGateway;
    @Mock
    private EmailService emailService;
    @Mock
    private GetPrintableBudgetUseCase getPrintableBudgetUseCase;

    private SendWorkOrderBudgetUseCase useCase;

    private UUID workOrderId;
    private UUID clientId;
    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        useCase = new SendWorkOrderBudgetUseCase(gateway, tokenGateway, clientGateway, emailService, getPrintableBudgetUseCase, "/budget-decision/");

        workOrderId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        workOrder = WorkOrder.restore(
                workOrderId, clientId, "ABC-1234", "Troca de oleo",
                WorkOrderStatus.RECEIVED, null, null,
                new HashSet<>(), new HashSet<>(),
                WorkOrderBudget.restore(workOrderId, new BigDecimal("500.00"), WorkOrderBudgetStatus.PENDING, null),
                LocalDateTime.now(), LocalDateTime.now(), null
        );
    }

    @Test
    @DisplayName("Deve enviar orcamento com sucesso e enviar email")
    void shouldSendBudgetAndEmail() {
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(clientGateway.findById(clientId)).thenReturn(
                Optional.of(Client.restore(clientId, "Joao", "joao@test.com", "12345678901", "11999998888", LocalDateTime.now(), LocalDateTime.now(), null)));
        PrintableBudgetResponse pdfResponse = new PrintableBudgetResponse("budget.pdf", "cGRmQ29udGVudA==");
        when(getPrintableBudgetUseCase.execute(any())).thenReturn(pdfResponse);
        when(tokenGateway.create(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new SendWorkOrderBudgetCommand(workOrderId, "http://localhost:8080/api"));

        verify(gateway).saveBudget(any());
        verify(tokenGateway).create(any());
        verify(emailService).send(eq("joao@test.com"), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando work order nao for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        when(gateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new SendWorkOrderBudgetCommand(workOrderId, "http://localhost:8080/api")));

        verify(gateway, never()).saveBudget(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando orcamento nao existir")
    void shouldThrowExceptionWhenBudgetNotFound() {
        WorkOrder woWithoutBudget = WorkOrder.restore(
                workOrderId, clientId, "ABC-1234", "Troca de oleo",
                WorkOrderStatus.RECEIVED, null, null,
                new HashSet<>(), new HashSet<>(), null,
                LocalDateTime.now(), LocalDateTime.now(), null
        );
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(woWithoutBudget));

        assertThrows(WorkOrderExceptions.BudgetNotFound.class,
                () -> useCase.execute(new SendWorkOrderBudgetCommand(workOrderId, "http://localhost:8080/api")));
    }

    @Test
    @DisplayName("Deve enviar orcamento sem email quando cliente nao for encontrado")
    void shouldSendBudgetWithoutEmailWhenClientNotFound() {
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(clientGateway.findById(clientId)).thenReturn(Optional.empty());
        when(tokenGateway.create(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new SendWorkOrderBudgetCommand(workOrderId, "http://localhost:8080/api"));

        verify(gateway).saveBudget(any());
        verify(tokenGateway).create(any());
        verify(emailService, never()).send(anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Deve enviar orcamento sem anexo quando geracao de PDF falhar")
    void shouldSendBudgetWithoutAttachmentWhenPdfFails() {
        when(gateway.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(clientGateway.findById(clientId)).thenReturn(
                Optional.of(Client.restore(clientId, "Joao", "joao@test.com", "12345678901", "11999998888", LocalDateTime.now(), LocalDateTime.now(), null)));
        when(getPrintableBudgetUseCase.execute(any())).thenThrow(new RuntimeException("PDF error"));
        when(tokenGateway.create(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new SendWorkOrderBudgetCommand(workOrderId, "http://localhost:8080/api"));

        verify(emailService).send(eq("joao@test.com"), anyString(), anyString(), isNull());
    }
}
