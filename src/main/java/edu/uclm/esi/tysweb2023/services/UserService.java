package edu.uclm.esi.tysweb2023.services;

import edu.uclm.esi.tysweb2023.dao.TokenDAO;
import edu.uclm.esi.tysweb2023.dao.UserDAO;
import edu.uclm.esi.tysweb2023.model.Email;
import edu.uclm.esi.tysweb2023.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uclm.esi.tysweb2023.model.User;

import java.io.IOException;

@Service
public class UserService {
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private TokenDAO tokenDAO;

	public void register(String name, String pwd, String email) throws IOException {
		User user = new User();
		user.setName(name);
		user.setPwd(pwd);
		user.setEmail(email);
		this.userDAO.save(user);
		Token token = new Token (user.getEmail());
		this.tokenDAO.save(token);
		Email smtp = new Email();
		String asunto = "Registro satisfactorio a nuestro proyecto de TecYSisWeb 2023";
	    smtp.send(email, asunto, token);

	}

	public User login(String email, String pwd) {
		pwd = org.apache.commons.codec.digest.DigestUtils.sha512Hex(pwd);
		return this.userDAO.findByEmailAndPwd(email, pwd);
	}
}