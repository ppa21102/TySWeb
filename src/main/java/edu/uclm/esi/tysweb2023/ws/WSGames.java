package edu.uclm.esi.tysweb2023.ws;

import java.io.IOException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uclm.esi.tysweb2023.http.UserController;
import edu.uclm.esi.tysweb2023.model.Tablero;
import edu.uclm.esi.tysweb2023.model.User;
import edu.uclm.esi.tysweb2023.services.MatchService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.servlet.http.HttpSession;

@Component
public class WSGames extends TextWebSocketHandler {
	
	@Autowired
	private MatchService matchService;
	

	private List<WebSocketSession> sessions = new ArrayList<>();
	private Map<String, SesionWS> sessionsByNombre = new HashMap<>();
	private Map<String, SesionWS> sessionsById = new HashMap<>();
	private Map<String, Tablero> tableros = new HashMap<>();


	
	@Override
	public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
	    String query = wsSession.getUri().getQuery();
	    String sessionId = query.substring("httpSessionId=".length());

	    // Obtener la sesión HTTP usando el sessionId
	    HttpSession httpSession = UserController.httpSessions.get(sessionId);

	    SesionWS sesionWS = new SesionWS(wsSession, httpSession);

	    if (httpSession != null) {
	        User user = (User) httpSession.getAttribute("user");
	        if (user != null) {
	            sesionWS.setNombre(user.getName());
	            user.setSesionWS(sesionWS);
	        }
	    } else {
	        User temporalUser = new User();
	        temporalUser.setName("TempUser_" + wsSession.getId());
	        sesionWS.setNombre(temporalUser.getName());
	    }

	    this.sessionsById.put(wsSession.getId(), sesionWS);
	    this.sessions.add(wsSession);
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// 	 		CHAT			 	/////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("+++++++++++++++ EN handleTextMessage");
		JSONObject jso = new JSONObject(message.getPayload());
		String tipo = jso.getString("tipo");
		if (tipo.equals("IDENT")) {
			System.out.println("+++++++++++++++ EN IDENT");

			String nombre = jso.getString("nombre");
			// SesionWS sesionWS = new SesionWS(nombre, session);
			SesionWS sesionWS = this.sessionsById.get(session.getId());
			sesionWS.setNombre(nombre);

			this.sessionsByNombre.put(nombre, sesionWS);
			this.sessionsById.put(session.getId(), sesionWS);

			this.difundir(session, "tipo", "NUEVO USUARIO", "nombre", nombre);
			this.bienvenida(session);
			return;
		}
		if (tipo.equals("MENSAJE CHAT")) {
			System.out.println("+++++++++++++++ EN MENSAJE CHAT");
			String destinatario = jso.getString("destinatario");
			String texto = jso.getString("texto");

			String remitente = this.sessionsById.get(session.getId()).getNombre();

			JSONObject respuesta = new JSONObject().put("tipo", "MENSAJE PRIVADO").put("texto", texto).put("remitente",
					remitente);

			SesionWS sesionDestinatario = this.sessionsByNombre.get(destinatario);
			if (sesionDestinatario == null) {
				respuesta.put("tipo", "SE FUE");
				TextMessage messageRespuesta = new TextMessage(respuesta.toString());
				session.sendMessage(messageRespuesta);
			} else {
				WebSocketSession sessionDestinatario = this.sessionsByNombre.get(destinatario).getSession();
				TextMessage messageRespuesta = new TextMessage(respuesta.toString());
				sessionDestinatario.sendMessage(messageRespuesta);
			}
			return;
		}
	}
	
	

	private void bienvenida(WebSocketSession sessionDelTipoQueAcabaDeLlegar) {
		JSONObject jso = new JSONObject().put("tipo", "BIENVENIDA");
		JSONArray jsaUsuarios = new JSONArray();

		Collection<SesionWS> usuariosConectados = this.sessionsByNombre.values();
		for (SesionWS usuarioConectado : usuariosConectados) {
			if (usuarioConectado.getSession() != sessionDelTipoQueAcabaDeLlegar) {
				jsaUsuarios.put(usuarioConectado.getNombre());
			}
		}
		jso.put("usuarios", jsaUsuarios);
		try {
			sessionDelTipoQueAcabaDeLlegar.sendMessage(new TextMessage(jso.toString()));
		} catch (IOException e) {
			this.eliminarSesion(sessionDelTipoQueAcabaDeLlegar);
		}
	}

	private void difundir(WebSocketSession remitente, Object... clavesyValores) {
		// tipo, NUEVO USUARIO, nombre, Pepe, edad, 20, curso, 4º
		JSONObject jso = new JSONObject();
		for (int i = 0; i < clavesyValores.length; i = i + 2) {
			String clave = clavesyValores[i].toString();
			String valor = clavesyValores[i + 1].toString();
			jso.put(clave, valor);
		}
		for (WebSocketSession session : this.sessions) {
			if (session != remitente) {
				try {
					session.sendMessage(new TextMessage(jso.toString()));
				} catch (IOException e) {
					this.eliminarSesion(session);
				}
			}
		}
	}

	private void eliminarSesion(WebSocketSession session) {
		this.sessions.remove(session);
		SesionWS sesionWS = this.sessionsById.remove(session.getId());
		this.sessionsByNombre.remove(sesionWS.getNombre());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		SesionWS sesionWS = this.sessionsById.remove(session.getId());
		this.sessionsByNombre.remove(sesionWS.getNombre());
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	}
}