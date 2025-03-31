package com.ababaiev;

import com.ababaiev.converters.SName;
import com.ababaiev.models.MessageTypes;
import com.ababaiev.models.SegmentNames;
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
    private String title;

    @SName("ДЧП")
    private String date;

    @SName("ПОК")
    private String buyer;

    @SName("ОПП")
    private String itemDescription;

    @SName("ПЛА")
    private String payer;

    @SName("ВАЛ")
    private String currency;

    @SName("ОТР")
    private String recipient;

    @SName("ЦІН")
    private String orderPrice;

    @SName("КІЛ")
    private String commodityQuantity;

    @SName(SegmentNames.DOCUMENT_NAME)
    private String documentName;

    @SName("ТОВ")
    private String commodity;

    @SName("ОДВ")
    private String unitsOfMeasurement;

    @SName(value = "СУМ")
    private Double sum;

    @SName("БКВ")
    private String sendersBank;

    @SName("РХВ")
    private String sendersAccount;

    @SName("БКО")
    private String recipientsBank;

    @SName("РХО")
    private String recipientsAccount;

    @SName("МФВ")
    private String sendersBankMFI;

    @SName("МФО")
    private String recipientsBankMFI;

    @SName("ВІН")
    private String initializationVector;

    @SName(SegmentNames.CIPHER)
    private String cipher;

    @SName("КЛШ")
    private String cipherKey;

    @SName("АЦП")
    private String eSignatureAlgorithm;

    @SName("КЦП")
    private String eSignatureVerificationKey;
}
