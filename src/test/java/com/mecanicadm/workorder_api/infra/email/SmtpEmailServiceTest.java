package com.mecanicadm.workorder_api.infra.email;

import com.mecanicadm.workorder_api.core.shared.domain.port.EmailAttachment;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private SmtpEmailService smtpEmailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(smtpEmailService, "sender", "noreply@mecanicadm.com");
    }

    @Test
    @DisplayName("Deve enviar email sem anexo com sucesso")
    void shouldSendEmailWithoutAttachment() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        smtpEmailService.send("client@test.com", "Teste", "<p>Hello</p>", null);

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email com anexo PDF")
    void shouldSendEmailWithPdfAttachment() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        EmailAttachment attachment = new EmailAttachment("budget.pdf", new byte[]{1, 2, 3});

        smtpEmailService.send("client@test.com", "Teste", "<p>Hello</p>", attachment);

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve logar erro quando envio falhar")
    void shouldLogErrorWhenSendFails() {
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

        smtpEmailService.send("client@test.com", "Teste", "<p>Hello</p>", null);

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }
}
