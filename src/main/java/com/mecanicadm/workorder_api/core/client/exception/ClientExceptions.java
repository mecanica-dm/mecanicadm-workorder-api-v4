package com.mecanicadm.workorder_api.core.client.exception;

import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;

public class ClientExceptions {

    private ClientExceptions() {
    }

    public static class NotFound extends DomainExceptionCore {
        public NotFound() {
            super("client.not.found", 404);
        }
    }

    public static class DocumentExists extends DomainExceptionCore {
        public DocumentExists() {
            super("client.document.exists", 409);
        }
    }

    public static class EmailExists extends DomainExceptionCore {
        public EmailExists() {
            super("client.email.exists", 409);
        }
    }

    public static class NameNotEmpty extends DomainExceptionCore {
        public NameNotEmpty() {
            super("client.name.not.empty", 400);
        }
    }

    public static class EmailNotEmpty extends DomainExceptionCore {
        public EmailNotEmpty() {
            super("client.email.not.empty", 400);
        }
    }

    public static class DocumentNotEmpty extends DomainExceptionCore {
        public DocumentNotEmpty() {
            super("client.document.not.empty", 400);
        }
    }
}
