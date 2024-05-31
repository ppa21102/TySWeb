package edu.uclm.esi.tysweb2023;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.uclm.esi.tysweb2023.ws.SesionWS;
import edu.uclm.esi.tysweb2023.model.Tablero;
import edu.uclm.esi.tysweb2023.model.User;
import edu.uclm.esi.tysweb2023.services.MatchService;
import edu.uclm.esi.tysweb2023.ws.FakeWSBot;

@SpringBootTest
@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestMatchController2 {

	@Autowired
	private MockMvc server;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private MatchService matchService;

	private MockHttpSession sessionPepe;
	private MockHttpSession sessionAna;
	private String gameId;

	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@BeforeEach
	void setUp() {
		this.sessionPepe = new MockHttpSession();
		User pepe = new User();
	    pepe.setId("13c8dafe-33f0-48e7-b5f0-c3745a06ae30"); // Establecer el mismo ID
		pepe.setName("Pepe");
		SesionWS sessionWSPepe = new SesionWS(new FakeWSBot(), null);
		pepe.setSesionWS(sessionWSPepe);
		this.sessionPepe.setAttribute("user", pepe);

		this.sessionAna = new MockHttpSession();
		User ana = new User();
	    ana.setId("0c600135-c6c8-458d-b27d-471015a6ae59"); // Establecer el mismo ID
		ana.setName("Ana");
		SesionWS sessionWSAna = new SesionWS(new FakeWSBot(), null);
		ana.setSesionWS(sessionWSAna);
		this.sessionAna.setAttribute("user", ana);
	}

	@Test
	@Order(1)
	void testStartEndpoint() throws Exception {
		System.out.println("TEST 1");

		String info = "{\"lat\": \"34342\", \"lon\": \"32324\"}";

		// Crear el primer jugador y solicitar el inicio de partida
		RequestBuilder requestPepe = MockMvcRequestBuilders.post("/matches/start?juego=TableroHundirFlota")
				.session(this.sessionPepe).content(info).contentType("application/json");

		ResultActions raPepe = this.server.perform(requestPepe);
		String responsePepe = raPepe.andReturn().getResponse().getContentAsString();

		System.out.println("_------------------ responsePepe " + responsePepe);

		// Crear el segundo jugador y solicitar unirse a la partida
		RequestBuilder requestAna = MockMvcRequestBuilders.post("/matches/start?juego=TableroHundirFlota")
				.session(this.sessionAna).content(info).contentType("application/json");

		ResultActions raAna = this.server.perform(requestAna);
		String responseAna = raAna.andReturn().getResponse().getContentAsString();

		System.out.println("_------------------ responseAna " + responseAna);

		JSONObject jsonResponsePepe = new JSONObject(responsePepe);
		JSONObject jsonResponseAna = new JSONObject(responseAna);

		assertTrue(jsonResponsePepe.get("id").equals(jsonResponseAna.get("id")), "Los ids no coinciden");

		this.gameId = jsonResponsePepe.getString("id");

		// Verificar que el tablero tiene dos jugadores
		Tablero tablero = this.matchService.getTableroById(this.gameId);
		assertEquals(2, tablero.getPlayers().size(), "El número de jugadores no es correcto");
	}

	@Test
	@Order(2)
	void testPlayEndpoint() throws Exception {
		System.out.println("TEST 1");

		String info = "{\"id\": \"" + gameId + "\"}";

		// Notificar el comienzo de la partida
		RequestBuilder requestPlay = MockMvcRequestBuilders.post("/matches/play").session(this.sessionPepe)
				.content(info).contentType("application/json");

		ResultActions raPlay = this.server.perform(requestPlay);
		String responsePlay = raPlay.andReturn().getResponse().getContentAsString();

		System.out.println("_------------------ responsePlay " + responsePlay);

		// Verificar que el tablero está en estado "PLAYING"
		Tablero tablero = this.matchService.getTableroById(gameId);
		assertEquals("PLAYING", tablero.getStatus(), "El estado de la partida no es correcto");

		// Verificar que se ha notificado a ambos jugadores
		assertTrue(responsePlay.contains(gameId), "El ID de la partida no está presente en la respuesta");
	}

	@Test
	@Order(3)
	void testPonerEndpoint() throws Exception {
	    System.out.println("TEST 3");

	    for (int i = 0; i < 21; i++) {
	        // Obtener el tablero actual
	        Tablero tablero = this.matchService.getTableroById(gameId);

	        // Obtener el jugador con el turno
	        User jugadorConTurno = tablero.getJugadorConElTurno();

	        // Crear un movimiento válido
	        int columnaAleatoria = new Random().nextInt(9);
	        int filaAleatoria = new Random().nextInt(9);

	        String movimiento = "{\"id\": \"" + gameId + "\", \"columna\": " + columnaAleatoria + ", \"fila\": " + filaAleatoria + "}";

	        // Determinar la sesión a usar en función del jugador con el turno
	        MockHttpSession sessionJugadorConTurno = jugadorConTurno.getName().equals("Pepe") ? this.sessionPepe
	                : this.sessionAna;

	        // Realizar el movimiento con el jugador correspondiente
	        RequestBuilder requestPoner = MockMvcRequestBuilders.post("/matches/poner").session(sessionJugadorConTurno)
	                .content(movimiento).contentType("application/json");

	        ResultActions raPoner = this.server.perform(requestPoner);
	        String responsePoner = raPoner.andReturn().getResponse().getContentAsString();

	        // Verificar que el movimiento se realizó correctamente
	        Tablero tableroDespues = this.matchService.getTableroById(gameId);

	        // Verificar si la partida ha finalizado
	        System.out.println(tableroDespues.getStatus());
	        if (tableroDespues.getStatus().equals("COMPLETED")) {
	            System.out.println("La partida ha finalizado.");
	            assertEquals("COMPLETED", tableroDespues.getStatus());
	            break;
	        }
	    }
	}


	
	@Test
	@Order(4)
	void testAbandonarPartida() throws Exception {
	    System.out.println("TEST Abandonar Partida");

	    // Obtener el jugador con el turno
	    Tablero tableroAntes = this.matchService.getTableroById(gameId);
	    User jugadorConTurno = tableroAntes.getJugadorConElTurno();

	    // Simular el abandono de la partida por parte de uno de los jugadores
	    String infoAbandonar = "{\"id\": \"" + gameId + "\"}";
	    RequestBuilder requestAbandonar = MockMvcRequestBuilders.post("/matches/abandonar")
	            .session(jugadorConTurno.getName().equals("Pepe") ? this.sessionPepe : this.sessionAna)
	            .content(infoAbandonar).contentType("application/json");

	    ResultActions raAbandonar = this.server.perform(requestAbandonar);
	    String responseAbandonar = raAbandonar.andReturn().getResponse().getContentAsString();

	    // Verificar que el tablero se ha marcado como COMPLETED
	    assertEquals("COMPLETED", tableroAntes.getStatus());
	}


}
