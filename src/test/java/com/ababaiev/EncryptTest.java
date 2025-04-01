package com.ababaiev;

import com.ababaiev.services.CryptoUtils;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Log4j
public class EncryptTest {

    @Test
    void generateDesKeys() throws NoSuchAlgorithmException {
        // Generate DES Key
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56); // DES uses a 56-bit key
        SecretKey secretKey = keyGen.generateKey();

        // Generate IV (DES block size is 8 bytes = 64 bits)
        byte[] ivBytes = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // Encode to Base64 for easy output
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        String encodedIV = Base64.getEncoder().encodeToString(iv.getIV());

        System.out.println("Generated DES Key (Base64): " + encodedKey);
        System.out.println("Generated IV (Base64): " + encodedIV);

        Base64.getDecoder().decode(encodedKey);
        Base64.getDecoder().decode(encodedKey);
    }

    @Test
    void generateEncryptedCipherMessage() throws NoSuchAlgorithmException {

        String rsa = CryptoUtils.encryptRsa64("""
                ЗАГ+0000'ПОЧ'ДЧП+20250329:2216:24'ОПП+0001'ШИФ+DES'КЛШ+1cTLeg0QvJQ='ВІН+TNpRMnu+Y6Y='КІП'КІН+0008'
                """.getBytes(StandardCharsets.UTF_8));

        System.out.println(rsa);
    }

    @Test
    void generatePaymentMessage() throws NoSuchAlgorithmException {
        var decoder = Base64.getDecoder();
        byte[] key = decoder.decode("1cTLeg0QvJQ=");
        byte[] iv = decoder.decode("TNpRMnu+Y6Y=");

        String des = CryptoUtils.encryptDes64(key, iv, """
                ЗАГ+0000'ПОЧ'ДЧП+20250401:2018:24'ДОК+ОПЛ'ПЛА+ТОВ "Кооператор"'БКВ+Чернівецьке відділення КБ "Приватбанк"' \
                МФВ+356032'РХВ+2600123456789'ОТР+ТОВ "Калинівський ринок"'БКО+ЧФ АКБ "Укрексімбанк"'МФО+356026'РХО+2600987654321'\
                ОПП+Оплата за товар по рахунку №23 від 10.09.2010 р;'ВАЛ+грн'СУМ+10000'КІП'КІН+0016'
                """.getBytes(StandardCharsets.UTF_8));
        System.out.println(des);

    }

    @Test
    void generateInvoiceMessage() throws NoSuchAlgorithmException {
        var decoder = Base64.getDecoder();
        byte[] key = decoder.decode("1cTLeg0QvJQ=");
        byte[] iv = decoder.decode("TNpRMnu+Y6Y=");

        String des = CryptoUtils.encryptDes64(key, iv, """
                ЗАГ+0003'ПОЧ'ДЧП+20250330:1145:24'ДОК+НАК'ПОК+ТОВ «УкрТоргСервіс»'ОТР+ТОВ «КиївМаркет»'ТОВ+Пральний порошок "CleanPro"' \
                КІЛ+20'ЦІН+150.00'ОДВ+кг'СУМ+3000.00' \
                ОПП+Поставка товару згідно з договором №45 від 25.03.2025'КІП'КІН+0013'
                """.getBytes(StandardCharsets.UTF_8));
        System.out.println(des);
    }
}
