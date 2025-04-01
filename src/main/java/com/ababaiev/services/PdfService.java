package com.ababaiev.services;

import com.ababaiev.models.EdiMessage;
import com.ababaiev.models.MessageTypes;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PDFontSupplier;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;

public class PdfService {
    TemplateEngine templateEngine;
    public PdfService() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    public byte[] generatePdf(EdiMessage message, MessageTypes messageType) {
        Context context = new Context();
        context.setVariable("model", message);
        String html = switch (messageType) {
            case PAYMENT -> templateEngine.process("payment", context);
            case INVOICE -> templateEngine.process("invoice", context);
            default -> throw new IllegalArgumentException("Invalid message type");
        };

        return this.generatePdfFromHtml(html);
    }

    @SneakyThrows
    private byte[] generatePdfFromHtml(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String uri = getClass().getClassLoader().getResource("templates/invoice.html").toURI().toString();

            new PdfRendererBuilder()
                    .withHtmlContent(html, uri)
                    .toStream(outputStream)
                    .run();

            return outputStream.toByteArray();
        }
    }

//    @SneakyThrows
//    public byte[] generatePdfFromHtml(String html) {
//        String outputFolder = System.getProperty("user.home") + File.separator + "thymeleaf.pdf";
//        OutputStream outputStream = new FileOutputStream(outputFolder);
//        String uri = getClass().getClassLoader().getResource("templates/invoice.html").toURI().toString();
//
//        new PdfRendererBuilder()
//                .withHtmlContent(html, uri)
//                .toStream(outputStream)
//                .run();
//
//        outputStream.close();
//        return new byte[0];
//    }


}
