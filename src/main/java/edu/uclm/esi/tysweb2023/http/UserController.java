package edu.uclm.esi.tysweb2023.http;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tysweb2023.services.UserService;
import edu.uclm.esi.tysweb2023.model.User;


@RestController
@RequestMapping("users")
//@CrossOrigin(origins = "*")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    private UserService userService;
    public static Map<String, HttpSession> httpSessions  = new HashMap<>();

    @PostMapping("/register")
    public void register(@RequestBody Map<String, Object> info) {
        String name = info.get("name").toString().trim();
        String email = info.get("email").toString().trim();
        String pwd1 = info.get("pwd1").toString().trim();
        String pwd2 = info.get("pwd2").toString().trim();

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El correo suministrado no tiene un formato válido");
        if (!pwd1.equals(pwd2))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Las contraseñas no coinciden");
        if (pwd1.length() <= 5)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "La contraseña debe tener al menos 5 caracteres");
        if (name.length() <= 5)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El nombre debe tener al menos 5 caracteres");

        try {
            this.userService.register(name, pwd1, email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ese correo electrónico ya existe");
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

        this.httpSessions.put(session.getId(), session);

        return result;
    }
}
