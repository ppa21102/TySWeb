package edu.uclm.esi.tysweb2023.http;

import edu.uclm.esi.tysweb2023.dao.UserDAO;
import edu.uclm.esi.tysweb2023.model.Tablero;
import edu.uclm.esi.tysweb2023.model.User;
import edu.uclm.esi.tysweb2023.services.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Map;

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
		User user;
		try {
			if (session.getAttribute("user")!=null){
				user = (User) session.getAttribute("user");
			} else {
				user = new User();
				user.setName("randomUser" + new SecureRandom().nextInt(1000));
				session.setAttribute("user", user);
			}
			Tablero result = this.matchService.newMatch(user, juego);
			return result;
		} catch (Exception e){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
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
		String idUser = session.getAttribute("idUser").toString();
		Tablero result = this.matchService.findMatch(id);
		return result.getJugadorConElTurno().getId().equals(idUser); 
		
	}
	
}
	

