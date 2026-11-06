package com.mecanicadm.workorder_api.core.shared.domain.port;

public interface EmailService {
    void send(String to, String subject, String htmlBody, EmailAttachment attachment);
}
