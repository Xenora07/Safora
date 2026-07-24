package com.safora.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safora.server.dtos.RouteCalculationRequest;
import com.safora.server.entities.RouteHistory;
import com.safora.server.repositories.RouteHistoryRepository;
import com.safora.server.services.RouteRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteRecommendationService recommendationService;

    @MockBean
    private RouteHistoryRepository historyRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCalculateRoutes_ValidationFailure() throws Exception {
        RouteCalculationRequest invalidReq = new RouteCalculationRequest();

        mockMvc.perform(post("/api/routes/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetHistory() throws Exception {
        when(historyRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.singletonList(new RouteHistory()));

        mockMvc.perform(get("/api/routes/history")
                .header("X-User-Id", 1))
                .andExpect(status().isOk());
    }
}
