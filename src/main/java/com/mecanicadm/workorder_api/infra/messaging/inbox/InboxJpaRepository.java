package com.mecanicadm.workorder_api.infra.messaging.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface InboxJpaRepository extends JpaRepository<InboxJpaEntity, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO inbox (message_id, message_type, work_order_id, status, processed_at)
            VALUES (:messageId, :messageType, :workOrderId, :status, :processedAt)
            ON CONFLICT (message_id) DO UPDATE
                SET message_type = EXCLUDED.message_type, work_order_id = EXCLUDED.work_order_id,
                    status = EXCLUDED.status, error = NULL, processed_at = EXCLUDED.processed_at
                WHERE inbox.status IN ('FAILED', 'REJECTED')
            """, nativeQuery = true)
    int register(@Param("messageId") UUID messageId,
                 @Param("messageType") String messageType,
                 @Param("workOrderId") UUID workOrderId,
                 @Param("status") String status,
                 @Param("processedAt") Instant processedAt);

    @Modifying
    @Query(value = """
            INSERT INTO inbox (message_id, message_type, work_order_id, status, error, processed_at)
            VALUES (:messageId, :messageType, :workOrderId, 'FAILED', :error, :processedAt)
            ON CONFLICT (message_id) DO UPDATE
                SET message_type = EXCLUDED.message_type, work_order_id = EXCLUDED.work_order_id,
                    status = 'FAILED', error = EXCLUDED.error, processed_at = EXCLUDED.processed_at
                WHERE inbox.status IN ('FAILED', 'REJECTED')
            """, nativeQuery = true)
    int registerFailure(@Param("messageId") UUID messageId,
                        @Param("messageType") String messageType,
                        @Param("workOrderId") UUID workOrderId,
                        @Param("error") String error,
                        @Param("processedAt") Instant processedAt);
}
