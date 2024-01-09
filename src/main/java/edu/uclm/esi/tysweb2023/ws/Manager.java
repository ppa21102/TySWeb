package edu.uclm.esi.tysweb2023.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketSession;

import java.util.Vector;

public class Manager {

    private Vector<WebSocketSession> chatSessions;

    private Manager() {

        this.chatSessions = new Vector<>();

    }

    public void addChatSession(WebSocketSession wsSession) {
        this.chatSessions.add(wsSession);
    }

    public void removeChatSession(WebSocketSession wssession) {
        Vector<WebSocketSession> chatSessions = this.getChatSessions();
        for(WebSocketSession ws : chatSessions) {
            if(ws.getId() == wssession.getId()) {
                chatSessions.remove(ws);
                break;
            }
        }

    }

    public void setChatSessions(Vector<WebSocketSession> chatSessions) {
        this.chatSessions = chatSessions;
    }

    public Vector<WebSocketSession> getChatSessions() {
        return chatSessions;
    }

    private static class ManagerHolder {
        static Manager singleton=new Manager();
    }

    @Bean
    public static Manager get() {
        return ManagerHolder.singleton;
    }

}