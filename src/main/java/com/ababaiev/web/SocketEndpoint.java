package com.ababaiev.web;

import com.ababaiev.models.SessionConfig;
import com.ababaiev.services.CryptoUtils;
import com.ababaiev.services.EdiService;
import com.ababaiev.models.MessageTypes;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws")
public class SocketEndpoint {
    private final EdiService ediService ;
    private static final Map<String, SessionConfig> sessionKeys = new ConcurrentHashMap<>();

    public SocketEndpoint() throws IOException {
        this.ediService = new EdiService();
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("Session opened: " + session.getId());
        String publicKey64 = Base64.getEncoder().encodeToString(CryptoUtils.getPublicKey().getEncoded());
        session.getBasicRemote().sendText(publicKey64);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            if (sessionKeys.containsKey(session.getId())) {
                SessionConfig sessionConfig = sessionKeys.get(session.getId());
                message = CryptoUtils.decode64Des(sessionConfig.getSessionKey(), sessionConfig.getIv(), message);
            } else {
                message = CryptoUtils.decrypt64Rsa(message);
            }

            var segments = ediService.parseSegments(message);
            var messageType = ediService.determineType(segments);

            if (messageType == MessageTypes.CIPHER) {
                SessionConfig sessionConfig = ediService.handleCipherMessage(segments);
                sessionKeys.put(session.getId(), sessionConfig);
            } else if (messageType == MessageTypes.PAYMENT) {
                byte[] pdf = ediService.handlePaymentMessage(segments);
                sendMessage(session, pdf);

            } else {
                byte[] pdf = ediService.handleInvoiceMessage(segments);
                sendMessage(session, pdf);
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.getBasicRemote().sendText(e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    private void sendMessage(Session session, byte[] message) throws IOException {
        SessionConfig sessionConfig = sessionKeys.get(session.getId());
        String encrypted64 = CryptoUtils.encryptDes64(sessionConfig.getSessionKey(), sessionConfig.getIv(), message);
        session.getBasicRemote().sendText(encrypted64);
    }
}
