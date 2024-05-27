package edu.uclm.esi.tysweb2023.http;

import edu.uclm.esi.tysweb2023.dao.UserDAO;
import edu.uclm.esi.tysweb2023.model.Tablero;
import edu.uclm.esi.tysweb2023.model.User;
import edu.uclm.esi.tysweb2023.services.MatchService;
import edu.uclm.esi.tysweb2023.ws.Manager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.stripe.model.PaymentIntent;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;

@RestController
@RequestMapping("matches")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MatchController {
	
	@Autowired
	private MatchService matchService;
	
	@Autowired
	private UserDAO userDAO;
	
	// Start: para el juego especificado, busca un tablero disponible. Si no
	// existe tablero crea uno nuevo. La partida no comienza hasta que el segundo jugador
	// envia una petición al endpoint play
	@PostMapping("/start")
	public String start(HttpSession session, @RequestParam String juego, @RequestBody Map<String, Object> info) {
	    try {
	        User user;
	        if (session.getAttribute("user") != null) {
	            user = (User) session.getAttribute("user");
	            
	        } else {
	            user = new User();
	            user.setName("randomUser" + new SecureRandom().nextInt(1000));
	            session.setAttribute("user", user);
	            UserController.httpSessions.put(session.getId(), session);
	        }
	        
	        String latitud = (String) info.get("lat");
	        String longitud = (String) info.get("lon");
	        user.setLat(latitud);
	        user.setLon(longitud);

	        System.out.println("Latitud: " + latitud);
	        System.out.println("Longitud: " + longitud);

	        
	        System.out.println("User ID: " + user.getId());
	        System.out.println("User Name: " + user.getName());
	        System.out.println("Juego: " + juego);

	        Tablero result = this.matchService.newMatch(user, juego);

	        System.out.println("New Match Result: " + result);
	        
	        // Devulve JSON string con id de partida
			JSONObject jso = new JSONObject();
			jso.put("id", result.getId());
	        jso.put("httpSessionId", session.getId()); 
			if (result.getPlayers().size() == 2) {
				jso.put("status", "READY");
			} else {
				jso.put("status", "CREATED");
			}
			
			System.out.println("####jso: "+jso.toString());

			return jso.toString();
	    } catch (Exception e) {
	        System.out.println("Exception in /start: " + e.getMessage());
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
	    }
	}
	
	// comienza la partida
	@PostMapping("/play")
	public String play(HttpSession session, @RequestBody Map<String, Object> info) {
		String idPartida = info.get("id").toString();
		
		Tablero match = this.matchService.notificarComienzo(idPartida, info);
		
        // Devulve JSON string con id de partida
		JSONObject jso = new JSONObject();
		jso.put("id", match.getId());
		
		return jso.toString();
	}
	
	@PostMapping("/poner")
	public String poner(HttpSession session, @RequestBody Map<String, Object> info) {
		String id = info.get("id").toString();
		System.out.println("EN PONER---------------------------------------------------"); 
		System.out.println("EN PONER, id: "+id); 
		User user = (User) session.getAttribute("user");
		System.out.println("EN PONER, info: "+info); 
		System.out.println("EN PONER, user: "+user.getName()); 
		Tablero match = this.matchService.poner(id, info, user.getId());
		System.out.println("EN PONER, ES EL TURNO DE: "+match.getJugadorConElTurno().getName()); 
		JSONObject jso = new JSONObject();
		jso.put("id", match.getId());
		
		return jso.toString();
	}

	@PostMapping("/sendMessageChat")
	public void sendMessageChat(HttpSession session, @RequestBody Map<String, Object> messageInfo) {
		System.out.println("EN SEND MESSAGE CHAT");
		JSONObject jso = new JSONObject(messageInfo);
		String msg = jso.getString("msg");
		User user = (User) session.getAttribute("user");

		JSONObject jsoMsg = new JSONObject();
		jsoMsg.put("user", user.getName());
		jsoMsg.put("msg", msg);
		byte[] payload = jsoMsg.toString().getBytes();
		TextMessage message = new TextMessage(payload);
		System.out.println("USER: " + user.getName() + " envió " + msg);

		for (WebSocketSession ws : Manager.get().getChatSessions()) {
			try {
				ws.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@PostMapping("/abandonar")
	public String abandonar(HttpSession session, @RequestBody Map<String, Object> info) {
		String idPartida = info.get("id").toString();
		User user = (User) session.getAttribute("user");

		Tablero match = this.matchService.abandonarPartida(idPartida, user.getId());
		
		
        // Devulve JSON string con id de partida
		JSONObject jso = new JSONObject();
		jso.put("id", match.getId());
		
		return jso.toString();

	}

}
	
