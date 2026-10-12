package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.shared.domain.port.EmailAttachment;
import com.mecanicadm.workorder_api.core.shared.domain.port.EmailService;
import com.mecanicadm.workorder_api.core.workorder.domain.BudgetDecisionToken;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.port.BudgetDecisionTokenGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.SendWorkOrderBudgetCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetResponse;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetPrintableBudgetQuery;
import com.mecanicadm.workorder_api.shared.usecase.VoidUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.UUID;

public class SendWorkOrderBudgetUseCase implements VoidUseCase<SendWorkOrderBudgetCommand> {
    private static final Logger logger = LoggerFactory.getLogger(SendWorkOrderBudgetUseCase.class);

    private final WorkOrderGateway gateway;
    private final BudgetDecisionTokenGateway tokenGateway;
    private final ClientGateway clientGateway;
    private final EmailService emailService;
    private final GetPrintableBudgetUseCase getPrintableBudgetUseCase;
    private final String budgetDecisionPath;

    public SendWorkOrderBudgetUseCase(WorkOrderGateway gateway,
                                      BudgetDecisionTokenGateway tokenGateway,
                                      ClientGateway clientGateway,
                                      EmailService emailService,
                                      GetPrintableBudgetUseCase getPrintableBudgetUseCase,
                                      String budgetDecisionPath) {
        this.gateway = gateway;
        this.tokenGateway = tokenGateway;
        this.clientGateway = clientGateway;
        this.emailService = emailService;
        this.getPrintableBudgetUseCase = getPrintableBudgetUseCase;
        this.budgetDecisionPath = budgetDecisionPath;
    }

    public void execute(SendWorkOrderBudgetCommand cmd) {
        WorkOrder workOrder = gateway.findById(cmd.workOrderId())
                .orElseThrow(WorkOrderExceptions.NotFound::new);

        workOrder.getBudget().ifPresentOrElse(
                budget -> {
                    budget.send();
                    gateway.saveBudget(budget);

                    BudgetDecisionToken token = BudgetDecisionToken.create(workOrder.getId());
                    tokenGateway.create(token);

                    String clientEmail = clientGateway.findById(workOrder.getClientId())
                            .map(Client::getEmail)
                            .orElse(null);

                    if (clientEmail != null) {
                        String subject = "Orçamento - Ordem de Serviço #" + workOrder.getId().toString();
                        String htmlBody = buildEmailHtml(workOrder, budget.getTotalPrice(), token.getToken(), cmd.baseUrl());
                        EmailAttachment attachment = generatePdfAttachment(cmd.workOrderId());
                        emailService.send(clientEmail, subject, htmlBody, attachment);
                    }
                },
                () -> {
                    throw new WorkOrderExceptions.BudgetNotFound();
                }
        );
    }

    private EmailAttachment generatePdfAttachment(UUID workOrderId) {
        try {
            PrintableBudgetResponse response = getPrintableBudgetUseCase.execute(
                    new GetPrintableBudgetQuery(workOrderId));
            byte[] pdfBytes = Base64.getDecoder().decode(response.base64Content());
            return new EmailAttachment(response.fileName(), pdfBytes);
        } catch (Exception e) {
            logger.warn("Não foi possível gerar PDF do orçamento para OS {}: {}", workOrderId, e.getMessage());
            return null;
        }
    }

    private String buildEmailHtml(WorkOrder workOrder, BigDecimal totalPrice, String token, String baseUrl) {
        String approveUrl = baseUrl + budgetDecisionPath + token + "/form?action=APPROVED";
        String rejectUrl = baseUrl + budgetDecisionPath + token + "/form?action=REJECTED";
        String changesUrl = baseUrl + budgetDecisionPath + token + "/form?action=CHANGES_REQUESTED";

        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #333;">Orçamento - Ordem de Serviço #%s</h2>
                    <p>Prezado(a) cliente,</p>
                    <p>Segue abaixo o orçamento para a sua ordem de serviço. O detalhamento completo está no arquivo PDF em anexo.</p>
                    <div style="background: #f5f5f5; padding: 15px; border-radius: 8px; margin: 20px 0;">
                        <p><strong>Veículo:</strong> %s</p>
                        <p><strong>Descrição:</strong> %s</p>
                        <p style="font-size: 1.2em; color: #2e7d32;"><strong>Valor Total: R$ %s</strong></p>
                    </div>
                    <p>Por favor, responda ao orçamento clicando em um dos botões abaixo:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background: #4caf50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 5px; display: inline-block;">Aprovar</a>
                        <a href="%s" style="background: #f44336; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 5px; display: inline-block;">Rejeitar</a>
                        <a href="%s" style="background: #ff9800; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 5px; display: inline-block;">Solicitar Alterações</a>
                    </div>
                    <p style="color: #666; font-size: 0.9em;">Este link é válido por 24 horas.</p>
                    <hr style="border: none; border-top: 1px solid #eee;">
                    <p style="color: #999; font-size: 0.8em;">MecânicaDM - Sistema de Gestão de Oficina Mecânica</p>
                </body>
                </html>
                """.formatted(
                workOrder.getId().toString().substring(0, 8),
                workOrder.getVehicleId(),
                workOrder.getDescription(),
                totalPrice,
                approveUrl,
                rejectUrl,
                changesUrl
        );
    }
}
