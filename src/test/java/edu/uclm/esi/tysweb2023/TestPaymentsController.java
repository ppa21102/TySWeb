package edu.uclm.esi.tysweb2023;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TestPaymentsController {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPrepay() throws Exception {
        // Given
        double amount = 10.0;
        String clientSecret = "test_client_secret";

        // When
        RequestBuilder request = MockMvcRequestBuilders.get("/payments/prepay")
            .param("amount", String.valueOf(amount))
            .contentType("application/json");
        ResultActions resultActions = mockMvc.perform(request);
        MockHttpServletResponse response = resultActions.andReturn().getResponse();

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());
    }
}
