package edu.uclm.esi.tysweb2023;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.uclm.esi.tysweb2023.model.User;
import org.aspectj.lang.annotation.Before;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Component;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import edu.uclm.esi.tysweb2023.dao.UserDAO;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class TestUser {
    @Autowired
    private MockMvc server;

    private MockHttpSession sessionPepe, sessionAna, sessionTurno;

    @Autowired
    private UserDAO userDAO;

    private String idTablero;

    @BeforeAll
    void setUp() {
        this.userDAO.deleteAll();
    }

    @ParameterizedTest
    @CsvSource({"Pepe Pérez, joseperez@gmail.com, joseperez, joseperez, 200, OK",
            "Ana, anaperez@gmail.com, anaperez, anaperez, 403, El nombre debe tener al menos 5 caracteres",
            "Ana Pérez, anaperez@gmail.com, ana, ana, 403, La contraseña debe tener al menos 5 caracteres",
            "Ana Pérez, anaperez@gmail.com, ana1234, ana123, 403, Las contraseñas no coinciden",
            "Ana Pérez, anaperez@gmail.com, ana1234, ana1234, 200, OK",
            "Ana López, anaperez@gmail.com, ana1234, ana1234, 403, Ese correo electrónico ya existe"})
    @Order(1)
    void testRegistroMultiple(String name, String email, String pwd1, String pwd2, int codigo, String mensaje)
            throws Exception {
        System.out.println("TEST 1");
        JSONObject jso = new JSONObject().put("nombre", name).put("email", email).put("pwd1", pwd1).put("pwd2", pwd2);

        RequestBuilder request = MockMvcRequestBuilders.post("/users/register").contentType("application/json")
                .content(jso.toString());

        ResultActions resultActions = this.server.perform(request);
        MockHttpServletResponse response = resultActions.andReturn().getResponse();

        String mensajeRecibido = response.getErrorMessage();
        if (codigo == 200)
            mensaje = null;
        resultActions.andExpect(status().is(codigo));
        assertEquals(mensajeRecibido, mensaje);
    }

    @ParameterizedTest
    @CsvSource({"joseperez@gmail.com, joseperez, 200", "anaperez@gmail.com, ana123, 403",
            "anaperez@gmail.com, ana1234, 200"})
    @Order(2)
    void testLoginsMultiples(String email, String pwd, int codigo) throws Exception {
        System.out.println("TEST 2");

        JSONObject jso = new JSONObject().put("email", email).put("pwd", pwd);

        RequestBuilder request = MockMvcRequestBuilders.put("/users/login").contentType("application/json")
                .content(jso.toString());

        ResultActions resultActions = this.server.perform(request);
        resultActions.andExpect(status().is(codigo));

        if ((codigo == 200) && (email.startsWith("jose")))
            this.sessionPepe = (MockHttpSession) resultActions.andReturn().getRequest().getSession();
        else if ((codigo == 200) && (email.startsWith("ana")))
            this.sessionAna = (MockHttpSession) resultActions.andReturn().getRequest().getSession();
    }

    @Test
    @Order(3)
    void testInicioDePartida() throws Exception {
        System.out.println("TEST 3");
        System.out.println("TERCER TEST");
        //RequestBuilder requestPepe = MockMvcRequestBuilders.get("/matches/start?juego=Tablero4R").session(this.sessionPepe);

        String info = "{\"lat\": \"34342\", \"lon\": \"32324\"}";
        RequestBuilder requestPepe = MockMvcRequestBuilders.post("/matches/start?juego=Tablero4R")
                .session(this.sessionPepe)
                .content(info)
                .contentType("application/json");


        //RequestBuilder requestAna = MockMvcRequestBuilders.get("/matches/start?juego=Tablero4R").session(this.sessionAna);
        RequestBuilder requestAna = MockMvcRequestBuilders.post("/matches/start?juego=Tablero4R")
                .session(this.sessionAna)
                .content(info)
                .contentType("application/json");


        System.out.println("request peppe" + requestPepe.toString());
        ResultActions raPepe = this.server.perform(requestPepe);
        System.out.println("ra peppe" + raPepe);
        String tableroPepe = raPepe.andReturn().getResponse().getContentAsString();
        System.out.println("Tablero peppe" + tableroPepe);
        JSONObject jsoTableroPepe = new JSONObject(tableroPepe);

        ResultActions raAna = this.server.perform(requestAna);
        String tableroAna = raAna.andReturn().getResponse().getContentAsString();
        JSONObject jsoTableroAna = new JSONObject(tableroAna);

        assertTrue(jsoTableroPepe.get("id").equals(jsoTableroAna.get("id")), "Los ids no coinciden");
        this.idTablero = jsoTableroPepe.getString("id");

        //String idJugadorConElTurno = jsoTableroAna.getJSONObject("jugadorConElTurno").getString("id");
        String idJugadorConElTurno = jsoTableroAna.getString("id");
        String idPepe = jsoTableroPepe.getString("id");

        if (idJugadorConElTurno.equals(idPepe))
            this.sessionTurno = this.sessionPepe;
        else
            this.sessionTurno = this.sessionAna;

        System.out.println("SESION TURNO " + this.sessionTurno.toString() + " " + this.sessionTurno.getId());
        //testPartida();
    }

    @Test
    @Order(5)
    void testMensajeDeChatPepe() throws Exception {
        System.out.println("TEST 5");
        System.out.println("TEST CHAT");

        String info = "{\"msg\": \"HOLA TEST\"}";
        RequestBuilder requestPepeChat = MockMvcRequestBuilders.post("/matches/sendMessageChat")
                .session(this.sessionPepe)
                .content(info)
                .contentType("application/json");

        ResultActions resultActions = this.server.perform(requestPepeChat);
        resultActions.andExpect(status().is(200));
    }
}