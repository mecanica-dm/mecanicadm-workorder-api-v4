package com.mecanicadm.workorder_api.infra.messaging.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxJpaRepository extends JpaRepository<OutboxJpaEntity, UUID> {

    @Query(value = """
            SELECT * FROM outbox
            WHERE status = 'PENDING'
            ORDER BY sequence
            LIMIT :batchSize
            FOR UPDATE
            """, nativeQuery = true)
    List<OutboxJpaEntity> lockPendingBatch(@Param("batchSize") int batchSize);

    @Modifying
    @Query(value = "DELETE FROM outbox WHERE status = 'PUBLISHED' AND published_at < :threshold", nativeQuery = true)
    int deletePublishedBefore(@Param("threshold") Instant threshold);
}
