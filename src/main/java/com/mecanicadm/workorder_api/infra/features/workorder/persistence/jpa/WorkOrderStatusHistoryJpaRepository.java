package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderStatusHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkOrderStatusHistoryJpaRepository extends JpaRepository<WorkOrderStatusHistoryJpaEntity, Long> {

    List<WorkOrderStatusHistoryJpaEntity> findByWorkOrderIdOrderByChangedAtAscIdAsc(UUID workOrderId);
}
