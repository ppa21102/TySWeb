package edu.uclm.esi.tysweb2023;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class TestMatchController {

    @Autowired
    private MockMvc mockMvc;

    private static String gameId;

    @Test
    @Order(1)
    void testStartMatch() throws Exception {
        Map<String, Object> info = new HashMap<>();
        info.put("lat", "40.416775");
        info.put("lon", "-3.703790");

        JSONObject jsonInfo = new JSONObject(info);

        RequestBuilder request = MockMvcRequestBuilders.post("/matches/start?juego=Tablero4R")
                                                      .contentType("application/json")
                                                      .content(jsonInfo.toString());

        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(response);
        gameId = jsonResponse.getString("id");
    }

    @Test
    @Order(2)
    void testJoinMatch() throws Exception {
        Map<String, Object> info = new HashMap<>();
        info.put("lat", "40.416775");
        info.put("lon", "-3.703790");

        JSONObject jsonInfo = new JSONObject(info);

        RequestBuilder request = MockMvcRequestBuilders.post("/matches/start?juego=Tablero4R")
                                                      .contentType("application/json")
                                                      .content(jsonInfo.toString());

        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void testPlay() throws Exception {
        if (gameId == null) {
            throw new IllegalStateException("No hay partida para jugar.");
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", gameId);

        JSONObject jsonInfo = new JSONObject(info);

        RequestBuilder request = MockMvcRequestBuilders.post("/matches/play")
                                                      .contentType("application/json")
                                                      .content(jsonInfo.toString());

        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void testPutPiece() throws Exception {
        if (gameId == null) {
            throw new IllegalStateException("No hay partida para jugar.");
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", gameId);
        info.put("column", 0);

        JSONObject jsonInfo = new JSONObject(info);

        RequestBuilder request = MockMvcRequestBuilders.post("/matches/poner")
                                                      .contentType("application/json")
                                                      .content(jsonInfo.toString());

        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void testSendMessageChat() throws Exception {
        Map<String, Object> messageInfo = new HashMap<>();
        messageInfo.put("msg", "test message");

        JSONObject jsonMessageInfo = new JSONObject(messageInfo);

        RequestBuilder request = MockMvcRequestBuilders.post("/matches/sendMessageChat")
                                                      .contentType("application/json")
                                                      .content(jsonMessageInfo.toString());

        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void testLeaveMatch() throws Exception {
        if (gameId == null) {
            throw new IllegalStateException("No hay partida para abandonar.");
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", gameId);

        JSONObject jsonInfo = new JSONObject(info);

        RequestBuilder request = MockMvcRequestBuilders.post("/matches/abandonar")
                                                      .contentType("application/json")
                                                      .content(jsonInfo.toString());

        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
    }
}
