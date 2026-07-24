package com.safora.server.services;

import com.safora.server.dtos.CreateReportRequest;
import com.safora.server.dtos.NearbyReportResponse;
import com.safora.server.dtos.SafetyReportResponse;
import com.safora.server.entities.SafetyReport;
import com.safora.server.enums.ReportCategory;
import com.safora.server.enums.ReportSeverity;
import com.safora.server.enums.ReportStatus;
import com.safora.server.repositories.SafetyReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommunityReportServiceTest {

    @Mock
    private SafetyReportRepository repository;

    @InjectMocks
    private CommunityReportService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitReport() {
        CreateReportRequest req = new CreateReportRequest();
        req.setLatitude(40.0);
        req.setLongitude(-73.0);
        req.setCategory(ReportCategory.POOR_LIGHTING);
        req.setSeverity(ReportSeverity.HIGH);

        SafetyReport saved = new SafetyReport();
        saved.setId(1L);
        saved.setUserId(2L);
        saved.setStatus(ReportStatus.ACTIVE);
        saved.setCategory(req.getCategory());
        saved.setSeverity(req.getSeverity());

        when(repository.save(any(SafetyReport.class))).thenReturn(saved);

        SafetyReportResponse res = service.submitReport(2L, req);
        assertEquals(1L, res.getId());
        assertEquals(2L, res.getUserId());
        assertEquals(ReportStatus.ACTIVE, res.getStatus());
    }

    @Test
    void testDeleteReport_Success() {
        SafetyReport report = new SafetyReport();
        report.setId(1L);
        report.setUserId(2L);
        
        when(repository.findById(1L)).thenReturn(Optional.of(report));
        
        service.deleteReport(1L, 2L);
        verify(repository, times(1)).delete(report);
    }

    @Test
    void testDeleteReport_Unauthorized() {
        SafetyReport report = new SafetyReport();
        report.setId(1L);
        report.setUserId(2L);
        
        when(repository.findById(1L)).thenReturn(Optional.of(report));
        
        assertThrows(IllegalArgumentException.class, () -> service.deleteReport(1L, 99L));
    }
}
