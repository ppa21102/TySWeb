package edu.uclm.esi.tysweb2023.services;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uclm.esi.tysweb2023.dao.MatchDAO;
import edu.uclm.esi.tysweb2023.dao.TokenDAO;
import edu.uclm.esi.tysweb2023.exceptions.MovimientoIlegalException;
import edu.uclm.esi.tysweb2023.model.Match;
import edu.uclm.esi.tysweb2023.model.Tablero;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;

import edu.uclm.esi.tysweb2023.model.User;

@Service
public class MatchService {

	private Map<String, Tablero> tableros = new HashMap<>();
	private List<Tablero> tablerosPendientes = new ArrayList<>();
	
	@Autowired
	private MatchDAO matchDAO;

	/////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// 	 		PARTIDAS		 	/////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

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
		System.out.println("---- idTablero: "+ id);
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

	public Tablero abandonarPartida(String id, String idUsuario) {
	    Tablero tablero = this.tableros.get(id);
	    if (tablero == null)
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró esa partida");
 
	    // Actualizar los contadores de partidas para ambos jugadores
	    for (User player : tablero.getPlayers()) {
	        if (player.getId().equals(idUsuario)) {
	            actualizarContadoresPartida(player, false, false); // Perdedor
	            tablero.setPerdedor(player.getId());
	        } else {
	            actualizarContadoresPartida(player, false, true); // Ganador
	            tablero.setGanador(player.getId());
	        }
	    }
	    tablero.setStatus("COMPLETED");

	    // Notificar a los jugadores sobre el abandono
	    for (User player : tablero.getPlayers()) {
	        TextMessage msg = buildNotificationMsg("ABANDONED", tablero, player.getId());
	        try {
	            player.getWebSocketSesion().sendMessage(msg);
	            System.out.println("Mensaje ABANDONED enviado a " + player.getId());
	        } catch (IOException e) {
	            System.out.println("Error enviando mensaje ABANDONED");
	            e.printStackTrace();
	        }
	    }

	    // Eliminar la partida abandonada de la lista de tableros activos
	    this.tableros.remove(id);

	    return tablero;
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
            playerData.put("lat", player.getLat());
            playerData.put("lon", player.getLon());
            players.add(playerData);
        }
        data.put("players", players);
        data.put("movimientosRestantesJugador1", tablero.getMovimientosRestantesJugador1());
        data.put("movimientosRestantesJugador2", tablero.getMovimientosRestantesJugador2());
        data.put("barcosHundidosJugador1", tablero.getBarcosHundidosJugador1());
        data.put("barcosHundidosJugador2", tablero.getBarcosHundidosJugador2());
        data.put("barcosRestantes", tablero.getBarcosRestantes());




       
        
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
            for (User player : tablero.getPlayers()) {
                actualizarContadoresPartida(player,empate,ganador);
            }

        } else {
        	 data.put("meToca", tablero.getJugadorConElTurno().getId().equals(idUser));
        }

        TextMessage msg = new TextMessage(data.toString());

        return msg;
    }


	/////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// 	ESTADISTICAS DE PARTIDAS 	/////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	private void actualizarContadoresPartida(User jugador, boolean empate, boolean ganador) {
		System.out.println("EN actualizarContadoresPartida"); 
	    String idJugador = jugador.getId();
		System.out.println("EN actualizarContadoresPartida, idJugador: " + idJugador); 

	    Match match = this.matchDAO.findByIdUser(idJugador);
	    
	    // Verificar si se encontró una partida para el usuario
	    if (match == null) {
	        // Si no se encontró, crea una nueva instancia de Match
	        match = new Match();
	        match.setUser(idJugador);
	        match.setPartidasJugadas(0);
	        match.setPartidasEmpatadas(0);
	        match.setPartidasGanadas(0);
	        match.setPartidasPerdidas(0);
	    }
	    
	    match.setPartidasJugadas(match.getPartidasJugadas()+1);

	    if (empate) {
	    	match.setPartidasEmpatadas(match.getPartidasEmpatadas()+1);	
	    } else {
    	   if (ganador) {
    		   match.setPartidasGanadas(match.getPartidasGanadas()+1);
    	   } else if (!ganador) {
    		   match.setPartidasPerdidas(match.getPartidasPerdidas()+1);
    	   }
	    }
	    
	    this.matchDAO.save(match);

	}
	
    public Match obtenerDatosUsuario(String idUsuario) {
        return this.matchDAO.findByIdUser(idUsuario);
    }
    
    public Tablero getTableroById(String idPartida) {
        return this.tableros.get(idPartida);
    }
	
	/////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// 				CHAT		 	/////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////


	// Añade un método para enviar mensajes de chat
    public void enviarMensajeChat(String idTablero, String remitente, String mensaje) {
        Tablero tablero = this.tableros.get(idTablero);
        if (tablero == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encuentro esa partida");

        // Lógica para enviar el mensaje de chat a los jugadores en el tablero
        // Puedes almacenar los mensajes en una lista en el tablero o manejarlos de otra manera
        // ...

        // Envía el mensaje a los jugadores en el tablero
        for (User player : tablero.getPlayers()) {
            TextMessage msg = buildChatMessage(remitente, mensaje);
            try {
                player.getWebSocketSesion().sendMessage(msg);
                System.out.println("Mensaje de chat enviado a " + player.getId());
            } catch (IOException e) {
                System.out.println("Error enviando mensaje de chat");
                e.printStackTrace();
            }
        }
    }

    // Método para construir un mensaje de chat
    private TextMessage buildChatMessage(String remitente, String mensaje) {
        JSONObject data = new JSONObject()
            .put("tipo", "MENSAJE CHAT")
            .put("remitente", remitente)
            .put("mensaje", mensaje);

        return new TextMessage(data.toString());
    }
    
    

}
