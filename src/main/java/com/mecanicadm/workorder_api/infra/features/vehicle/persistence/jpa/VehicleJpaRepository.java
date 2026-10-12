package com.mecanicadm.workorder_api.infra.features.vehicle.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.vehicle.persistence.entity.VehicleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VehicleJpaRepository extends JpaRepository<VehicleJpaEntity, String>, JpaSpecificationExecutor<VehicleJpaEntity> {
    Optional<VehicleJpaEntity> findByLicensePlate(String licensePlate);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM vehicle WHERE license_plate = :licensePlate)", nativeQuery = true)
    boolean existsByLicensePlate(@Param("licensePlate") String licensePlate);
}

