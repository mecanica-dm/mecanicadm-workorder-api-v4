package com.mecanicadm.workorder_api.infra.pdf.exception;

public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
