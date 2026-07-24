package com.safora.server.repositories;

import com.safora.server.entities.SafetyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafetyReportRepository extends JpaRepository<SafetyReport, Long> {
    List<SafetyReport> findByUserIdOrderByTimestampDesc(Long userId);

    @Query(value = "SELECT * FROM safety_reports r WHERE r.status = :status AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(r.latitude)) * " +
           "cos(radians(r.longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(r.latitude)))) < :radiusKm", nativeQuery = true)
    List<SafetyReport> findNearbyReports(@Param("lat") Double lat, 
                                         @Param("lng") Double lng, 
                                         @Param("radiusKm") Double radiusKm,
                                         @Param("status") String status);
}
