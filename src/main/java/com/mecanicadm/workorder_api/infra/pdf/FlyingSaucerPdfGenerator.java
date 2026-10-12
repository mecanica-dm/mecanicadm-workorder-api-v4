package com.mecanicadm.workorder_api.infra.pdf;

import com.lowagie.text.DocumentException;
import com.mecanicadm.workorder_api.infra.pdf.exception.PdfGenerationException;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class FlyingSaucerPdfGenerator implements PdfGenerator {

    private final SpringTemplateEngine templateEngine;

    private final ThreadLocal<ITextRenderer> rendererThreadLocal = ThreadLocal.withInitial(ITextRenderer::new);

    public FlyingSaucerPdfGenerator(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public byte[] generatePdfFromHtml(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = rendererThreadLocal.get();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, true);

            renderer.getSharedContext().reset();

            return outputStream.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new PdfGenerationException("Error generating PDF", e);
        } finally {
            rendererThreadLocal.remove();
        }
    }
}