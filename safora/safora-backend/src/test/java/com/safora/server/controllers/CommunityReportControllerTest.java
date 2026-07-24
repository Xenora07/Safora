package com.safora.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safora.server.dtos.CreateReportRequest;
import com.safora.server.enums.ReportCategory;
import com.safora.server.enums.ReportSeverity;
import com.safora.server.services.CommunityReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommunityReportController.class)
class CommunityReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommunityReportService service;

    @Test
    void testSubmitReport_ValidationFailure() throws Exception {
        CreateReportRequest req = new CreateReportRequest(); // Missing required fields

        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testSubmitReport_Success() throws Exception {
        CreateReportRequest req = new CreateReportRequest();
        req.setLatitude(10.0);
        req.setLongitude(20.0);
        req.setCategory(ReportCategory.ROAD_HAZARD);
        req.setSeverity(ReportSeverity.LOW);

        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}
