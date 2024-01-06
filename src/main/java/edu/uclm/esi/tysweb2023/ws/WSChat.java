package edu.uclm.esi.tysweb2023.ws;

//import edu.uclm.esi.tys2122.http.Manager;
import org.json.JSONObject;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class WSChat extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
        wsSession.setBinaryMessageSizeLimit(1000 * 1024 * 1024);
        Manager.get().addChatSession(wsSession);
        System.out.println("CHAT AÑADIDO");
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        session.setBinaryMessageSizeLimit(1000 * 1024 * 1024);

        @SuppressWarnings("unused")
        byte[] payload = message.getPayload().array();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
    }

    @SuppressWarnings("unused")
    private void send(WebSocketSession session, Object... typesAndValues) {
        JSONObject jso = new JSONObject();
        int i = 0;
        while (i < typesAndValues.length) {
            jso.put(typesAndValues[i].toString(), typesAndValues[i + 1]);
            i += 2;
        }
        WebSocketMessage<?> wsMessage = new TextMessage(jso.toString());
        try {
            session.sendMessage(wsMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession wssession, CloseStatus status) throws Exception {
        Manager.get().removeChatSession(wssession);

        super.afterConnectionClosed(wssession, status);
    }
}
