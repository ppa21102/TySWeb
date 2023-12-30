package edu.uclm.esi.tysweb2023.model;

import java.util.UUID;

import edu.uclm.esi.tysweb2023.ws.SesionWS;
import jakarta.persistence.*;

import org.springframework.web.socket.WebSocketSession;

@Entity
@Table(name = "users", indexes = { @Index(columnList = "name", unique = true),
		@Index(columnList = "email", unique = true) })
public class User {

	@Id
	@Column(length = 36)
	private String id;
	private String name;
	private String pwd;
	private String email;
	@Transient
	private SesionWS sesionWS;

	public User() {
		this.id = UUID.randomUUID().toString();
	}

	// get y set para id, name y email
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = org.apache.commons.codec.digest.DigestUtils.sha512Hex(pwd);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSesionWS(SesionWS sesionWS) { this.sesionWS = sesionWS; }
	
	public WebSocketSession getWebSocketSesion() {
		return this.sesionWS.getSession(); 
	}

}
