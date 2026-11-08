package com.mecanicadm.workorder_api.infra.features.client.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.client.persistence.entity.ClientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, UUID>, JpaSpecificationExecutor<ClientJpaEntity> {

    Optional<ClientJpaEntity> findByDocument(String document);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM clients WHERE document = :document)", nativeQuery = true)
    boolean existsByDocument(@Param("document") String document);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM clients WHERE email = :email)", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM clients WHERE document = :document AND id != :id)", nativeQuery = true)
    boolean existsByDocumentAndIdNot(@Param("document") String document, @Param("id") UUID id);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM clients WHERE email = :email AND id != :id)", nativeQuery = true)
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") UUID id);
}
