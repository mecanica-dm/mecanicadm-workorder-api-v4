package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.port.LaborExecutionSummaryProjection;
import com.mecanicadm.workorder_api.core.workorder.domain.port.LaborTypeStatsProjection;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderLaborItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkOrderLaborItemJpaRepository extends JpaRepository<WorkOrderLaborItemJpaEntity, UUID> {

    List<WorkOrderLaborItemJpaEntity> findByWorkOrderId(UUID workOrderId);

    @Query(value = "SELECT COUNT(wli.id) AS totalProcessedLabors, " +
            "COALESCE(AVG((EXTRACT(EPOCH FROM wli.execution_end_at) - EXTRACT(EPOCH FROM wli.execution_start_at)) / 60.0), 0) AS averageExecutionMinutes " +
            "FROM work_order_labor_items wli " +
            "WHERE wli.execution_start_at IS NOT NULL " +
            "AND wli.execution_end_at IS NOT NULL " +
            "AND (CAST(:initialDateTime AS TIMESTAMP) IS NULL OR wli.execution_end_at >= CAST(:initialDateTime AS TIMESTAMP)) " +
            "AND (CAST(:finalDateTimeExclusive AS TIMESTAMP) IS NULL OR wli.execution_end_at < CAST(:finalDateTimeExclusive AS TIMESTAMP))", nativeQuery = true)
    LaborExecutionSummaryProjection getExecutionTimeSummary(
            @Param("initialDateTime") LocalDateTime initialDateTime,
            @Param("finalDateTimeExclusive") LocalDateTime finalDateTimeExclusive
    );

    @Query(value = "SELECT CAST(l.id AS VARCHAR) AS laborId, l.name AS laborName, " +
            "COUNT(wli.id) AS totalExecutions, " +
            "AVG((EXTRACT(EPOCH FROM wli.execution_end_at) - EXTRACT(EPOCH FROM wli.execution_start_at)) / 60.0) AS averageExecutionMinutes, " +
            "MIN((EXTRACT(EPOCH FROM wli.execution_end_at) - EXTRACT(EPOCH FROM wli.execution_start_at)) / 60.0) AS minExecutionMinutes, " +
            "MAX((EXTRACT(EPOCH FROM wli.execution_end_at) - EXTRACT(EPOCH FROM wli.execution_start_at)) / 60.0) AS maxExecutionMinutes " +
            "FROM work_order_labor_items wli " +
            "JOIN labors l ON l.id = wli.labor_id " +
            "WHERE wli.execution_start_at IS NOT NULL " +
            "AND wli.execution_end_at IS NOT NULL " +
            "AND (CAST(:initialDateTime AS TIMESTAMP) IS NULL OR wli.execution_end_at >= CAST(:initialDateTime AS TIMESTAMP)) " +
            "AND (CAST(:finalDateTimeExclusive AS TIMESTAMP) IS NULL OR wli.execution_end_at < CAST(:finalDateTimeExclusive AS TIMESTAMP)) " +
            "GROUP BY l.id, l.name", nativeQuery = true)
    List<LaborTypeStatsProjection> getStatsByLaborType(
            @Param("initialDateTime") LocalDateTime initialDateTime,
            @Param("finalDateTimeExclusive") LocalDateTime finalDateTimeExclusive
    );
}
