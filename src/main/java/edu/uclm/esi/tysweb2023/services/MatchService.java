package edu.uclm.esi.tysweb2023.services;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uclm.esi.tysweb2023.exceptions.MovimientoIlegalException;
import edu.uclm.esi.tysweb2023.model.Tablero;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tysweb2023.model.User;

@Service
public class MatchService {
	
	private Map<String, Tablero> tableros = new HashMap<>();
	private List<Tablero> tablerosPendientes = new ArrayList<>();

	/*public Tablero4R newMatch(User user) {
		Tablero4R tablero;
		if (this.tablerosPendientes.isEmpty()) {
			tablero = new Tablero4R();
			tablero.addUser(user);
			this.tablerosPendientes.add(tablero);
		}
		else {
			tablero = this.tablerosPendientes.remove(0);
			tablero.addUser(user);
			tablero.iniciar();
			this.tableros.put(tablero.getId(), tablero);
		}
		
		return tablero;
	}*/

	public Tablero newMatch(User user, String juego) throws Exception {
		Tablero tablero = null;
		if (this.tablerosPendientes.isEmpty()) {

			Class<?> clazz = null;
			try {
				//System.out.println(juego);
				juego="edu.uclm.esi.tysweb2023.model." + juego;
				//System.out.println(juego);
				clazz = Class.forName(juego);
			} catch (ClassNotFoundException e) {
				throw new Exception("El juego indicado no existe");
			}
			Constructor constructor = clazz.getConstructors()[0];
			try {
				tablero = (Tablero) constructor.newInstance();
			} catch (Exception e) {
				throw new Exception("Contacta con el administrador");
			}

			tablero.addUser(user);
			this.tablerosPendientes.add(tablero);
		}
		else {
			tablero = this.tablerosPendientes.remove(0);
			tablero.addUser(user);
			tablero.iniciar();
			this.tableros.put(tablero.getId(), tablero);
		}

		return tablero;
	}

	public Tablero poner(String id, Map<String, Object> movimiento, String idUser) {
		Tablero tablero = this.tableros.get(id);
		if (tablero == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encuentro esa partida");
		try {
			tablero.poner(movimiento, idUser);
		} catch (MovimientoIlegalException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
		return tablero;
	}
	
	public Tablero findMatch(String id) {
		return this.tableros.get(id); 
		
	}

}
