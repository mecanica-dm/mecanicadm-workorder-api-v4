package com.mecanicadm.workorder_api.infra.email;

import com.mecanicadm.workorder_api.core.shared.domain.port.EmailAttachment;
import com.mecanicadm.workorder_api.core.shared.domain.port.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class SmtpEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailService.class);
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public SmtpEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Async
    public void send(String to, String subject, String htmlContent, EmailAttachment attachment) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            if (attachment != null) {
                helper.addAttachment(attachment.fileName(), new jakarta.activation.DataSource() {
                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(attachment.content());
                    }

                    @Override
                    public OutputStream getOutputStream() {
                        throw new UnsupportedOperationException("Read-only DataSource");
                    }

                    @Override
                    public String getContentType() {
                        return "application/pdf";
                    }

                    @Override
                    public String getName() {
                        return attachment.fileName();
                    }
                });
            }

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Erro ao enviar email para {}: {}", to, e.getMessage(), e);
        }
    }
}
