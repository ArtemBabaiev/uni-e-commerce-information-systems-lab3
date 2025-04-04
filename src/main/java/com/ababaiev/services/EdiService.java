package com.ababaiev.services;

import com.ababaiev.annotations.SName;
import com.ababaiev.models.*;

import java.lang.reflect.Field;
import java.util.*;

public class EdiService {

    private static final Map<String, Field> map;

    static {
        map = new HashMap<>();
        for (var field : EdiMessage.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(SName.class)) {
                map.put(field.getDeclaredAnnotation(SName.class).value(), field);
            }
        }
    }

    PdfService pdfService = new PdfService();

    public EdiMessage process(String ediString) {
        List<Segment> segments = parseSegments(ediString);
        segments = validateSegments(segments);
        EdiMessage ediMessage = convertToPoJo(segments);
        ediMessage.setType(determineType(segments));
        return ediMessage;
    }

    public List<Segment> parseSegments(String message) {
        System.out.println(message);
        boolean readingHeader = true;
        boolean readingValue = false;
        StringBuilder headerBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        List<Segment> segments = new ArrayList<>();
        EdiMessage ediMessage = new EdiMessage();
        for (char entry : message.toCharArray()) {
            if (entry == '+' && readingValue) {
                valueBuilder.append(entry);
                continue;
            }
            if (entry == '+') {
                readingHeader = false;
                readingValue = true;
                continue;
            }
            if (entry == '\'') {
                String header = headerBuilder.toString().trim();
                String value = valueBuilder.toString().trim();
                segments.add(Segment.builder().name(header).value(value).build());
                valueBuilder.setLength(0);
                headerBuilder.setLength(0);
                readingHeader = true;
                readingValue = false;
                continue;
            }
            if (readingHeader) {
                headerBuilder.append(entry);
                continue;
            }
            valueBuilder.append(entry);
        }

        return segments;
    }

    public List<Segment> validateSegments(List<Segment> segments) {
        List<Segment> validatedSegments = new ArrayList<>();

        if (segments.size() <= 2) {
            throw new RuntimeException("Not enough Segments");
        }

        var firstSegment = segments.getFirst();
        if (!firstSegment.getName().equals(SegmentNames.PACKET_HEADER)) {
            throw new RuntimeException("Message should start with packet header");
        }
        validatedSegments.add(firstSegment);
        segments.remove(firstSegment);

        var lastSegment = segments.getLast();
        if (!lastSegment.getName().equals(SegmentNames.PACKET_END)) {
            throw new RuntimeException("Message should end with packet end segment");
        }
        segments.remove(lastSegment);

        var messageStart = segments.stream().filter(segment -> segment.getName().equals(SegmentNames.MESSAGE_START)).findFirst();
        if (messageStart.isEmpty()) {
            throw new RuntimeException("Message should have at least one message start segment");
        }

        var messageEnd = segments.stream().filter(segment -> segment.getName().equals(SegmentNames.MESSAGE_END)).findFirst();
        if (messageEnd.isEmpty()) {
            throw new RuntimeException("Message should have message end segment");
        }

        var messageStartIndex = segments.indexOf(messageStart.get());
        var messageEndIndex = segments.indexOf(messageEnd.get());

        var messageContent = segments.subList(messageStartIndex, messageEndIndex + 1);
        validatedSegments.addAll(messageContent);
        segments.removeAll(messageContent);

        if (!segments.isEmpty()) {
            throw new RuntimeException("Segments outside of the message");
        }

        if (validatedSegments.size() != Integer.parseInt(lastSegment.getValue())) {
            throw new RuntimeException("Number of valid segments does not match");
        }

        validatedSegments.add(lastSegment);
        return validatedSegments;
    }

    public EdiMessage convertToPoJo(List<Segment> segments) {
        try {
            EdiMessage message = new EdiMessage();
            for (Segment segment : segments) {
                String name = segment.getName();
                String value = segment.getValue();
                Field field = map.get(name);
                field.setAccessible(true);
                field.set(message, Converter.convert(field.getType(), value));
                field.setAccessible(false);
            }
            return message;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create POJO", e);
        }
    }

    public MessageTypes determineType(List<Segment> segments) {
        if (segments.stream().anyMatch(segment -> segment.getName().equals(SegmentNames.CIPHER))) {
            return MessageTypes.CIPHER;
        }
        var documentNameSegment = segments.stream().filter(segment -> segment.getName().equals(SegmentNames.DOCUMENT_NAME)).findFirst().get();
        if (documentNameSegment.getValue().equals("ОПЛ")) {
            return MessageTypes.PAYMENT;
        } else {
            return MessageTypes.INVOICE;
        }
    }

    public SessionConfig handleCipherMessage(List<Segment> segments) {
        segments = validateSegments(segments);
        var pojo = this.convertToPoJo(segments);
        String base64Key = pojo.getEncryptionKey();
        String base64Iv = pojo.getInitVector();
        var decoder = Base64.getDecoder();
        byte[] iv = decoder.decode(base64Iv);
        byte[] key = decoder.decode(base64Key);
        return SessionConfig.builder().sessionKey(key).iv(iv).build();
    }

    public byte[] handlePaymentMessage(List<Segment> segments) {
        segments = validateSegments(segments);
        var pojo = this.convertToPoJo(segments);
        return pdfService.generatePdf(pojo, MessageTypes.PAYMENT);
    }

    public byte[] handleInvoiceMessage(List<Segment> segments) {
        segments = validateSegments(segments);
        var pojo = this.convertToPoJo(segments);
        return pdfService.generatePdf(pojo, MessageTypes.INVOICE);
    }
}
