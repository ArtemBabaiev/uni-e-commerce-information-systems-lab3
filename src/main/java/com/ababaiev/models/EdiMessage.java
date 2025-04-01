package com.ababaiev.models;

import com.ababaiev.annotations.SName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EdiMessage {
    MessageTypes type;

    @SName(SegmentNames.PACKET_HEADER)
    private String packetHeader;

    @SName(SegmentNames.PACKET_END)
    private Long packetEnd;

    @SName(SegmentNames.MESSAGE_START)
    private String messageStart;

    @SName(SegmentNames.MESSAGE_END)
    private String messageEnd;

    @SName("НАЗ")
    private String documentTitle;

    @SName("ДЧП")
    private String dateTimePeriod;

    @SName("ПОК")
    private String buyerName;

    @SName("ОПП")
    private String paymentPurposeOrItemDescription;

    @SName("ПЛА")
    private String payerName;

    @SName("ВАЛ")
    private String paymentCurrency;

    @SName("ОТР")
    private String recipientName;

    @SName("ЦІН")
    private String unitPrice;

    @SName("КІЛ")
    private String itemQuantity;

    @SName(SegmentNames.DOCUMENT_NAME)
    private String documentType;

    @SName("ТОВ")
    private String itemName;

    @SName("ОДВ")
    private String measurementUnit;

    @SName(value = "СУМ")
    private Double totalAmount;

    @SName("БКВ")
    private String senderBankName;

    @SName("РХВ")
    private String senderBankAccount;

    @SName("БКО")
    private String recipientBankName;

    @SName("РХО")
    private String recipientBankAccount;

    @SName("МФВ")
    private String senderBankMFO;

    @SName("МФО")
    private String recipientBankMFO;

    @SName("ВІН")
    private String initVector;

    @SName(SegmentNames.CIPHER)
    private String encryptionAlgorithm;

    @SName("КЛШ")
    private String encryptionKey;

    @SName("АЦП")
    private String digitalSignatureAlgorithm;

    @SName("КЦП")
    private String signatureVerificationKey;
}
