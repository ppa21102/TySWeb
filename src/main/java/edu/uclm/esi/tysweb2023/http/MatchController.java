package edu.uclm.esi.tysweb2023.http;

import edu.uclm.esi.tysweb2023.dao.UserDAO;
import edu.uclm.esi.tysweb2023.model.Tablero;
import edu.uclm.esi.tysweb2023.model.User;
import edu.uclm.esi.tysweb2023.services.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("matches")
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

	@GetMapping("/start")
	public Tablero start(HttpSession session, @RequestParam String juego) {
		//star?=Tablero4R
		try {
			System.out.println("Dentro");
			//String idUser = session.getAttribute("idUser").toString();
			User user = (User) session.getAttribute("user");
			System.out.println("user" + user);

			//Optional<User> optUser = this.userDAO.findById(user);
			Tablero result = this.matchService.newMatch(user, juego);
			System.out.println(juego);
			System.out.println(result.toString());
			return result;
		} catch (Exception e){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
			//throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "malaa " + juego + " " + session.toString());
		}
	}
	
	@PostMapping("/poner")
	public Tablero poner(HttpSession session, @RequestBody Map<String, Object> info) {
		String id = info.get("id").toString();
		String idUser = session.getAttribute("idUser").toString();
		return this.matchService.poner(id, info, idUser);
	}
	
	
	@GetMapping("/meToca")
	public boolean meToca(HttpSession session, @RequestParam String id) {
		String idUser = session.getAttribute("idUser").toString();
		Tablero result = this.matchService.findMatch(id);
		return result.getJugadorConElTurno().getId().equals(idUser); 
		
	}
	
}
	

