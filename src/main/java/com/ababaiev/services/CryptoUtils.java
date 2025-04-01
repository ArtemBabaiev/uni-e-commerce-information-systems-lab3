package com.ababaiev.services;

import com.ababaiev.web.SocketEndpoint;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtils {
    private CryptoUtils() {
    }

    private static final PrivateKey privateKey;

    @Getter
    private static final PublicKey publicKey;

    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";

    static {
        privateKey = loadPrivateKey();
        publicKey = loadPublicKey();
    }

    private static PrivateKey loadPrivateKey() {
        try (InputStream privateIs = SocketEndpoint.class.getClassLoader().getResourceAsStream("private.pem")) {
            byte[] privateKeyBytes = privateIs.readAllBytes();
            String keyContent = new String(privateKeyBytes, StandardCharsets.UTF_8)
                    .replace(PRIVATE_KEY_HEADER, "")
                    .replace(PRIVATE_KEY_FOOTER, "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(keyContent);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static PublicKey loadPublicKey() {
        try (InputStream publicIs = SocketEndpoint.class.getClassLoader().getResourceAsStream("public.pub")) {
            byte[] publicKeyBytes = publicIs.readAllBytes();
            String keyContent = new String(publicKeyBytes, StandardCharsets.UTF_8)
                    .replace(PUBLIC_KEY_HEADER, "")
                    .replace(PUBLIC_KEY_FOOTER, "")
                    .replaceAll("\\s", ""); // Remove extra spaces and newlines

            // Decode Base64 key
            byte[] keyBytes = Base64.getDecoder().decode(keyContent);

            // Generate PublicKey object
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @SneakyThrows
    public static String decrypt64Rsa(String message64) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] messageBytes = decoder.decode(message64);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = cipher.doFinal(messageBytes);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static String decode64Des(byte[] key, byte[] iv, String message64) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] messageBytes = decoder.decode(message64);

        DESKeySpec keySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(messageBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static String encryptDes64(byte[] key, byte[] iv, byte[] message) {
        DESKeySpec keySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(message);

        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(decrypted);
    }

    @SneakyThrows
    public static String encryptRsa64(byte[] message) {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] decrypted = cipher.doFinal(message);

        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(decrypted);
    }
}
