package edu.uclm.esi.tysweb2023.services;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.uclm.esi.tysweb2023.dao.MatchDAO;
import edu.uclm.esi.tysweb2023.dao.TokenDAO;
import edu.uclm.esi.tysweb2023.exceptions.MovimientoIlegalException;
import edu.uclm.esi.tysweb2023.model.Match;
import edu.uclm.esi.tysweb2023.model.Tablero;
import edu.uclm.esi.tysweb2023.model.Tablero4R;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;

import edu.uclm.esi.tysweb2023.model.User;
import edu.uclm.esi.tysweb2023.ws.FakeWSBot;
import edu.uclm.esi.tysweb2023.ws.SesionWS;

@Service
public class MatchService {

	private Map<String, Tablero> tableros = new HashMap<>();
	private List<Tablero> tablerosPendientes4R = new ArrayList<>();
	private List<Tablero> tablerosPendientesHF = new ArrayList<>();
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Autowired
	private MatchDAO matchDAO;

	/////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// PARTIDAS /////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	public Tablero newMatch(User user, String juego) throws Exception {
		Tablero tablero = null;
		System.out.println("JUEGO: " + juego);

		if(juego.equals("Tablero4R")) {
			System.out.println("JUEGO 4R");

			// Si no hay tableros pendientes, crea un nuevo tablero
			if (this.tablerosPendientes4R.isEmpty()) {

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
				this.tablerosPendientes4R.add(tablero);

				System.out.println("PARTIDA CREADA CON 1 JUGADOR");

				if (juego.contains("Tablero4R")) {
					timer(tablero);
				}

			} else {
				tablero = this.tablerosPendientes4R.remove(0);
				tablero.addUser(user);
				tablero.iniciar();
				this.tableros.put(tablero.getId(), tablero);

				// enviar mensaje websocket para empezar partida

				System.out.println("PARTIDA CREADA CON 2 JUGADORES");
			}
		} else {
			System.out.println("JUEGO 4R");

			// Si no hay tableros pendientes, crea un nuevo tablero
			if (this.tablerosPendientesHF.isEmpty()) {

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
				this.tablerosPendientesHF.add(tablero);

				System.out.println("PARTIDA CREADA CON 1 JUGADOR");

				if (juego.contains("Tablero4R")) {
					timer(tablero);
				}

			} else {
				tablero = this.tablerosPendientesHF.remove(0);
				tablero.addUser(user);
				tablero.iniciar();
				this.tableros.put(tablero.getId(), tablero);

				// enviar mensaje websocket para empezar partida

				System.out.println("PARTIDA CREADA CON 2 JUGADORES");
			}
		}

		return tablero;
	}

	public void timer(Tablero tablero) {
		scheduler.schedule(() -> {
			if (tablero.getPlayers().size() == 1) {
				// Agregar un bot a la partida
				User bot = new User();
				bot.setName("BotPlayer");

				// Asignar una sesión WebSocket simulada al bot
				SesionWS fakeSessionWS = new SesionWS(new FakeWSBot(), null);
				bot.setSesionWS(fakeSessionWS);
				
				this.tablerosPendientes4R.remove(0);

				tablero.addUser(bot);
				System.out.println("BOT AÑADIDO");

				// Iniciar la partida con el bot
				tablero.iniciar();
				this.tableros.put(tablero.getId(), tablero);
				
	            realizarMovimientoBot(tablero);

				notifyPlayers(tablero, "START");

			}
		}, 10, TimeUnit.SECONDS);
	}

	public Tablero poner(String id, Map<String, Object> movimiento, String idUser) {
		Tablero tablero = this.tableros.get(id);
		if (tablero == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encuentro esa partida");
		try {
			System.out.println("EN MATCHSERVICE " + idUser);
			tablero.poner(movimiento, idUser);

			String msgType = "MOVEMENT";
			if (tablero.getStatus() == "COMPLETED") {
				msgType = "MATCH_END";
				for (User player : tablero.getPlayers()) {
					String ganador = tablero.getGanador();
			        boolean empate = ganador == null || ganador.isEmpty();
			        boolean ganadorActual = ganador != null && ganador.equals(player.getId());
			        actualizarContadoresPartida(player, empate, ganadorActual);
	            }
			}

			notifyPlayers(tablero, msgType);

			if (tablero.getJugadorConElTurno().getName().equals("BotPlayer")) {
				realizarMovimientoBot(tablero);
			}

		} catch (MovimientoIlegalException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}

		return tablero;
	}

	private void realizarMovimientoBot(Tablero tablero) {
	    if (tablero.getJugadorConElTurno().getName().equals("BotPlayer")) {
	        Random random = new Random();
	        int column;
	        
	        // Encuentra una columna disponible de forma aleatoria
	        do {
	            column = random.nextInt(tablero.getCasillas()[0].length);
	        } while (!isColumnAvailable(tablero, column));

	        Map<String, Object> movimiento = new HashMap<>();
	        movimiento.put("columna", column);
	        try {
	            tablero.poner(movimiento, tablero.getJugadorConElTurno().getId());
	            notifyPlayers(tablero, "MOVEMENT");
	        } catch (MovimientoIlegalException e) {
	            e.printStackTrace();
	        }
	    }
	}

	// Método para verificar si una columna está disponible
	private boolean isColumnAvailable(Tablero tablero, int column) {
	    for (int row = 0; row < tablero.getCasillas().length; row++) {
	        if (tablero.getCasillas()[row][column] == 'D') { // 'D' significa disponible
	            return true;
	        }
	    }
	    return false;
	}
	private void notifyPlayers(Tablero tablero, String msgType) {
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
	}

	public Tablero notificarComienzo(String id, Map<String, Object> movimiento) {
		Tablero tablero = this.tableros.get(id);
		System.out.println("---- idTablero: " + id);
		if (tablero == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encuentro esa partida");

		if (tablero.getPlayers().size() < 2)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La partida no puede comenzar");

		tablero.setStatus("PLAYING");
		for (User player : tablero.getPlayers()) {
			System.out.println(player);
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

		if (tablero.getStatus().equals("COMPLETED")) {
			boolean ganador = false, empate = false;
			if (tablero.getGanador() == null) {
				empate = true;
			} else if (tablero.getGanador().equals(idUser)) {
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

	/////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// ESTADISTICAS DE PARTIDAS
	///////////////////////////////////////////////////////////////////////////////////////// /////////////////////////////
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
		
		System.out.println("antes" + match.getPartidasJugadas());
		match.setPartidasJugadas(match.getPartidasJugadas() + 1);
		System.out.println("despues" + match.getPartidasJugadas());

		if (empate) {
			match.setPartidasEmpatadas(match.getPartidasEmpatadas() + 1);
		} else {
			if (ganador) {
				System.out.println("antes" + match.getPartidasGanadas());
				match.setPartidasGanadas(match.getPartidasGanadas() + 1);
				System.out.println("despues" + match.getPartidasGanadas());

			} else if (!ganador) {
				System.out.println("antes" + match.getPartidasPerdidas());
				match.setPartidasPerdidas(match.getPartidasPerdidas() + 1);
				System.out.println("despues" + match.getPartidasPerdidas());

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

}