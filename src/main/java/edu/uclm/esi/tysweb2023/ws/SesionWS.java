package edu.uclm.esi.tysweb2023.ws;

import org.springframework.web.socket.WebSocketSession;

import jakarta.servlet.http.HttpSession;

public class SesionWS {

    private String nombre;
    private WebSocketSession session;
    private HttpSession httpSession;

    public SesionWS(WebSocketSession session, HttpSession httpSession) {
        this.session = session;
        this.httpSession = httpSession;
    }

    public String getNombre() {
        return nombre;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public String getId() {
        return this.session.getId();
    }

    public void setNombre(String n){ this.nombre = n; }

}