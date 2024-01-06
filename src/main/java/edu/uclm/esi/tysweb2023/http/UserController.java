package edu.uclm.esi.tysweb2023.http;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tysweb2023.services.MatchService;
import edu.uclm.esi.tysweb2023.services.UserService;
import edu.uclm.esi.tysweb2023.model.Match;
import edu.uclm.esi.tysweb2023.model.User;


@RestController
@RequestMapping("users")
//@CrossOrigin(origins = "*")
//@CrossOrigin
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private MatchService matchService; 
    public static Map<String, HttpSession> httpSessions  = new HashMap<>();

    @PostMapping("/register")
    public void register(@RequestBody Map<String, Object> info) {
    	System.out.println("####################################################################################"); 
    	System.out.println("Contenido de info: " + info);
    	String nombre = (info.get("nombre") != null) ? info.get("nombre").toString() : null;
    	String email = (info.get("email") != null) ? info.get("email").toString() : null;
    	String pwd1 = (info.get("pwd1") != null) ? info.get("pwd1").toString() : null;
    	String pwd2 = (info.get("pwd2") != null) ? info.get("pwd2").toString() : null;

    	System.out.println("Valor de nombre: " + info.get("nombre"));
    	System.out.println("Valor de email: " + info.get("email"));
    	System.out.println("Valor de pwd1: " + info.get("pwd1"));
    	System.out.println("Valor de pwd2: " + info.get("pwd2"));


        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El correo suministrado no tiene un formato válido");
        if (!pwd1.equals(pwd2))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Las contraseñas no coinciden");
        if (pwd1.length() <= 5)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "La contraseña debe tener al menos 5 caracteres");
        if (nombre.length() <= 5)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El nombre debe tener al menos 5 caracteres");

        try {
            this.userService.register(nombre, pwd1, email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/login")
    public Map<String, Object> login(HttpSession session, @RequestBody Map<String, String> info) {
        String email = info.get("email").trim();
        String pwd = info.get("pwd").trim();

        User user = this.userService.login(email, pwd);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales inválidas");
        //session.setAttribute("idUser", user.getId());
        
        session.setAttribute("user", user);
        Map<String, Object> result = new HashMap<>();
        result.put("httpId", session.getId());
        result.put("nombre", user.getName()); 
        result.put("id", user.getId()); 
        result.put("email", user.getEmail());

        UserController.httpSessions.put(session.getId(), session);

        return result;
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Match> obtenerDatosUsuario(@RequestParam  String idUsuario) {
    	System.out.println("####################################################################################"); 
    	System.out.println("##### idUsuario:" + idUsuario); 
        // Lógica para obtener los datos del usuario desde la base de datos
        Match match = this.matchService.obtenerDatosUsuario(idUsuario);

        // Verificar si se encontraron datos
        if (match != null) {
            return new ResponseEntity<>(match, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    
}
