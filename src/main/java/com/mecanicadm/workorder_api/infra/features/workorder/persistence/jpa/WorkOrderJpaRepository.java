package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderExecutionDurationProjection;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderExecutionSummaryProjection;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderJpaEntity, UUID>, JpaSpecificationExecutor<WorkOrderJpaEntity> {

    @Query("SELECT w.status FROM WorkOrderJpaEntity w WHERE w.id = :id")
    Optional<WorkOrderStatus> findStatusById(@Param("id") UUID id);

    @Query(value = "SELECT CAST(jsonb_build_object(" +
            "'id', w.id, 'description', w.description, " +
            "'client', jsonb_build_object('id', c.id, 'name', c.name, 'email', c.email, 'document', c.document, 'phone', c.phone), " +
            "'vehicle', jsonb_build_object('licensePlate', v.license_plate, 'model', v.model, 'brand', v.brand, 'modelYear', v.model_year), " +
            "'status', w.status, 'executionStartAt', w.execution_start_at, 'executionEndAt', w.execution_end_at, " +
            "'laborItems', COALESCE((SELECT jsonb_agg(jsonb_build_object(" +
            "'id', i.id, 'laborId', i.labor_id, 'name', l.name, 'price', l.price, 'status', i.status, " +
            "'executionStartAt', i.execution_start_at, 'executionEndAt', i.execution_end_at) ORDER BY i.id) " +
            "FROM work_order_labor_items i JOIN labors l ON l.id = i.labor_id " +
            "WHERE i.work_order_id = w.id), CAST('[]' AS jsonb)), " +
            "'materialItems', COALESCE((SELECT jsonb_agg(jsonb_build_object(" +
            "'id', i.id, 'materialId', i.material_id, 'name', m.name, 'brand', m.brand, 'description', m.description, " +
            "'type', m.type, 'unitPrice', m.price, 'quantity', i.quantity, 'totalPrice', m.price * i.quantity) ORDER BY i.id) " +
            "FROM work_order_material_items i JOIN materials m ON m.id = i.material_id " +
            "WHERE i.work_order_id = w.id), CAST('[]' AS jsonb))" +
            ") AS VARCHAR) FROM work_orders w " +
            "LEFT JOIN clients c ON c.id = w.client_id LEFT JOIN vehicle v ON v.license_plate = w.vehicle_id " +
            "WHERE w.id = :id", nativeQuery = true)
    String findDetailsJsonById(@Param("id") UUID id);

    @Query(value = "SELECT COALESCE(SUM(m.price * wmi.quantity), 0) " +
            "FROM work_order_material_items wmi " +
            "JOIN materials m ON wmi.material_id = m.id " +
            "WHERE wmi.work_order_id = :workOrderId", nativeQuery = true)
    BigDecimal sumMaterialsTotalByWorkOrderId(@Param("workOrderId") UUID workOrderId);

    @Query(value = "SELECT COALESCE(SUM(l.price), 0) " +
            "FROM work_order_labor_items wli " +
            "JOIN labors l ON wli.labor_id = l.id " +
            "WHERE wli.work_order_id = :workOrderId", nativeQuery = true)
    BigDecimal sumLaborTotalByWorkOrderId(@Param("workOrderId") UUID workOrderId);

    @Query(value = "SELECT COUNT(*) AS totalProcessedWorkOrders, " +
            "COALESCE(AVG((EXTRACT(EPOCH FROM wo.execution_end_at) - EXTRACT(EPOCH FROM wo.execution_start_at)) / 60.0), 0) AS averageExecutionMinutes " +
            "FROM work_orders wo " +
            "WHERE wo.deleted_at IS NULL " +
            "AND wo.execution_start_at IS NOT NULL " +
            "AND wo.execution_end_at IS NOT NULL " +
            "AND (CAST(:initialDateTime AS TIMESTAMP) IS NULL OR wo.execution_end_at >= CAST(:initialDateTime AS TIMESTAMP)) " +
            "AND (CAST(:finalDateTimeExclusive AS TIMESTAMP) IS NULL OR wo.execution_end_at < CAST(:finalDateTimeExclusive AS TIMESTAMP))", nativeQuery = true)
    WorkOrderExecutionSummaryProjection getExecutionTimeSummary(
            @Param("initialDateTime") LocalDateTime initialDateTime,
            @Param("finalDateTimeExclusive") LocalDateTime finalDateTimeExclusive
    );

    @Query(value = "SELECT CAST(wo.id AS VARCHAR) AS workOrderId, " +
            "((EXTRACT(EPOCH FROM wo.execution_end_at) - EXTRACT(EPOCH FROM wo.execution_start_at)) / 60.0) AS durationMinutes " +
            "FROM work_orders wo " +
            "WHERE wo.deleted_at IS NULL " +
            "AND wo.execution_start_at IS NOT NULL " +
            "AND wo.execution_end_at IS NOT NULL " +
            "AND (CAST(:initialDateTime AS TIMESTAMP) IS NULL OR wo.execution_end_at >= CAST(:initialDateTime AS TIMESTAMP)) " +
            "AND (CAST(:finalDateTimeExclusive AS TIMESTAMP) IS NULL OR wo.execution_end_at < CAST(:finalDateTimeExclusive AS TIMESTAMP)) " +
            "ORDER BY ((EXTRACT(EPOCH FROM wo.execution_end_at) - EXTRACT(EPOCH FROM wo.execution_start_at)) / 60.0) DESC " +
            "LIMIT 1", nativeQuery = true)
    WorkOrderExecutionDurationProjection getSlowestExecution(
            @Param("initialDateTime") LocalDateTime initialDateTime,
            @Param("finalDateTimeExclusive") LocalDateTime finalDateTimeExclusive
    );

    @Query(value = "SELECT CAST(wo.id AS VARCHAR) AS workOrderId, " +
            "((EXTRACT(EPOCH FROM wo.execution_end_at) - EXTRACT(EPOCH FROM wo.execution_start_at)) / 60.0) AS durationMinutes " +
            "FROM work_orders wo " +
            "WHERE wo.deleted_at IS NULL " +
            "AND wo.execution_start_at IS NOT NULL " +
            "AND wo.execution_end_at IS NOT NULL " +
            "AND (CAST(:initialDateTime AS TIMESTAMP) IS NULL OR wo.execution_end_at >= CAST(:initialDateTime AS TIMESTAMP)) " +
            "AND (CAST(:finalDateTimeExclusive AS TIMESTAMP) IS NULL OR wo.execution_end_at < CAST(:finalDateTimeExclusive AS TIMESTAMP)) " +
            "ORDER BY ((EXTRACT(EPOCH FROM wo.execution_end_at) - EXTRACT(EPOCH FROM wo.execution_start_at)) / 60.0) ASC " +
            "LIMIT 1", nativeQuery = true)
    WorkOrderExecutionDurationProjection getFastestExecution(
            @Param("initialDateTime") LocalDateTime initialDateTime,
            @Param("finalDateTimeExclusive") LocalDateTime finalDateTimeExclusive
    );

    @Query(value = "SELECT COALESCE(AVG((EXTRACT(EPOCH FROM wli.execution_end_at) - EXTRACT(EPOCH FROM wli.execution_start_at)) / 60.0), 0) " +
            "FROM work_order_labor_items wli " +
            "JOIN work_orders wo ON wo.id = wli.work_order_id " +
            "WHERE wo.deleted_at IS NULL " +
            "AND wli.execution_start_at IS NOT NULL " +
            "AND wli.execution_end_at IS NOT NULL " +
            "AND (CAST(:initialDateTime AS TIMESTAMP) IS NULL OR wli.execution_end_at >= CAST(:initialDateTime AS TIMESTAMP)) " +
            "AND (CAST(:finalDateTimeExclusive AS TIMESTAMP) IS NULL OR wli.execution_end_at < CAST(:finalDateTimeExclusive AS TIMESTAMP))", nativeQuery = true)
    Double getAverageLaborExecutionMinutes(
            @Param("initialDateTime") LocalDateTime initialDateTime,
            @Param("finalDateTimeExclusive") LocalDateTime finalDateTimeExclusive
    );
}
