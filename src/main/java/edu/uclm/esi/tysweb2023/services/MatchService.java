package edu.uclm.esi.tysweb2023.services;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uclm.esi.tysweb2023.exceptions.MovimientoIlegalException;
import edu.uclm.esi.tysweb2023.model.Tablero;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;

import edu.uclm.esi.tysweb2023.model.User;

@Service
public class MatchService {

	private Map<String, Tablero> tableros = new HashMap<>();
	private List<Tablero> tablerosPendientes = new ArrayList<>();

	/*
	 * public Tablero4R newMatch(User user) { Tablero4R tablero; if
	 * (this.tablerosPendientes.isEmpty()) { tablero = new Tablero4R();
	 * tablero.addUser(user); this.tablerosPendientes.add(tablero); } else { tablero
	 * = this.tablerosPendientes.remove(0); tablero.addUser(user);
	 * tablero.iniciar(); this.tableros.put(tablero.getId(), tablero); }
	 * 
	 * return tablero; }
	 */

	public Tablero newMatch(User user, String juego) throws Exception {
		Tablero tablero = null;

		// Si no hay tableros pendientes, crea un nuevo tablero
		if (this.tablerosPendientes.isEmpty()) {

			Class<?> clazz = null;
			try {
				// System.out.println(juego);
				juego = "edu.uclm.esi.tysweb2023.model." + juego;
				// System.out.println(juego);
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

			System.out.println("PARTIDA CREADA CON 1 JUGADOR");
		} else {
			tablero = this.tablerosPendientes.remove(0);
			tablero.addUser(user);
			tablero.iniciar();
			this.tableros.put(tablero.getId(), tablero);

			// enviar mensaje websocket para empezar partida

			System.out.println("PARTIDA CREADA CON 2 JUGADORES");
		}

		return tablero;
	}

	public Tablero poner(String id, Map<String, Object> movimiento, String idUser) {
		Tablero tablero = this.tableros.get(id);
		if (tablero == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encuentro esa partida");
		try {
	        tablero.poner(movimiento, idUser);
	        
	        String msgType = "MOVEMENT";
	        if (tablero.getStatus() == "COMPLETED") {
	        	msgType = "MATCH_END"; 
	        }

	        for (User player : tablero.getPlayers()) {
	            TextMessage msg = buildNotificationMsg(msgType, tablero, player.getId());
	            try {
	                player.getWebSocketSesion().sendMessage(msg);
	                System.out.println("Mensaje " + msgType + " enviado a " + player.getId());
	            } catch (IOException e) {
	                System.out.println("Error enviando mensaje " + msgType);
	                e.printStackTrace();
	            }
	        }

	    } catch (MovimientoIlegalException e) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
	    }

	    return tablero;
	}

	public Tablero notificarComienzo(String id, Map<String, Object> movimiento) {
		Tablero tablero = this.tableros.get(id);
		if (tablero == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encuentro esa partida");

		if (tablero.getPlayers().size()<2)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La partida no puede comenzar");
		
		tablero.setStatus("PLAYING");
		for (User player : tablero.getPlayers()) {
			TextMessage msg = buildNotificationMsg("START", tablero, player.getId());
			try {
				player.getWebSocketSesion().sendMessage(msg);
				System.out.println("Mensaje START enviado a " + player.getId());
			} catch (IOException e) {
				System.out.println("Error enviando mensaje START");
				e.printStackTrace();
			}
		}

		return tablero;
	}

	public Tablero findMatch(String id) {
		return this.tableros.get(id);
	}

	private TextMessage buildNotificationMsg(String tipo, Tablero tablero, String idUser) {
        JSONObject data = new JSONObject().put("tipo", tipo);
        data.put("casillas", tablero.getCasillas());
        data.put("id", tablero.getId());

        List<JSONObject> players = new ArrayList<>();
        for (User player : tablero.getPlayers()) {
            JSONObject playerData = new JSONObject();
            playerData.put("name", player.getName());
            playerData.put("id", player.getId());
            players.add(playerData);
        }
        data.put("players", players);
       
        
        if (tablero.getStatus()== "COMPLETED") {
        	boolean ganador = false, empate = false; 
        	if (tablero.getGanador().length() == 0) {
        		empate = true; 
        	} else if(tablero.getGanador().equals(idUser)) {
        		ganador = true; 
        	}
        	data.put("meToca", false);
        	data.put("empate", empate); 
        	data.put("ganador", ganador);
        } else {
        	 data.put("meToca", tablero.getJugadorConElTurno().getId().equals(idUser));
        }

        TextMessage msg = new TextMessage(data.toString());

        return msg;
    }

}
