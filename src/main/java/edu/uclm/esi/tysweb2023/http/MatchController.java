package edu.uclm.esi.tysweb2023.http;

import edu.uclm.esi.tysweb2023.dao.UserDAO;
import edu.uclm.esi.tysweb2023.model.Tablero;
import edu.uclm.esi.tysweb2023.model.User;
import edu.uclm.esi.tysweb2023.services.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;

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

	//@GetMapping("/start")
	//public Tablero4R start(HttpSession session) {
	//	String idUser = session.getAttribute("idUser").toString();
	//	Optional<User> optUser = this.userDAO.findById(idUser);
	//	Tablero4R result = this.matchService.newMatch(optUser.get());
	//	return result;
	//}
	
	// Start: para el juego especificado, busca un tablero disponible. Si no
	// existe tablero crea uno nuevo. La partida no comienza hasta que el segundo jugador
	// envia una petición al endpoint play
	@GetMapping("/start")
	public String start(HttpSession session, @RequestParam String juego) {
	    try {
	        User user;
	        if (session.getAttribute("user") != null) {
	            user = (User) session.getAttribute("user");
	        } else {
	            user = new User();
	            user.setName("randomUser" + new SecureRandom().nextInt(1000));
	            session.setAttribute("user", user);
	        }

	        System.out.println("User ID: " + user.getId());
	        System.out.println("User Name: " + user.getName());
	        System.out.println("Juego: " + juego);

	        Tablero result = this.matchService.newMatch(user, juego);

	        System.out.println("New Match Result: " + result);
	        
	        // Devulve JSON string con id de partida
			JSONObject jso = new JSONObject();
			jso.put("id", result.getId());
			if (result.getPlayers().size() == 2) {
				jso.put("status", "READY");
			} else {
				jso.put("status", "CREATED");
			}

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
	public Tablero poner(HttpSession session, @RequestBody Map<String, Object> info) {
		String id = info.get("id").toString();
		//String idUser = session.getAttribute("idUser").toString();
		User user = (User) session.getAttribute("user");
		return this.matchService.poner(id, info, user.getId());
	}
	
	
	@GetMapping("/meToca")
	public boolean meToca(HttpSession session, @RequestParam String id) {
		System.out.println("En meToca -----------------------------------------------------"); 
		System.out.println("HttpSession: " + session.getAttribute(id).toString());
		String idUser = session.getAttribute("idUser").toString();
		Tablero result = this.matchService.findMatch(id);
		System.out.print("idUser:" + idUser);
		System.out.print("tablero:" + result);
		
		return result.getJugadorConElTurno().getId().equals(idUser); 
		
	}
	
}
	

