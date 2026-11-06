package com.mecanicadm.workorder_api.infra.messaging.consumer;

import com.mecanicadm.workorder_api.infra.messaging.inbox.InboxJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class MessageConsumerSupport {
    private final InboxJpaRepository inbox;
    private final TransactionTemplate transactionTemplate;

    public MessageConsumerSupport(InboxJpaRepository inbox,
                                  PlatformTransactionManager transactionManager) {
        this.inbox = inbox;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    InboxJpaRepository inbox() {
        return inbox;
    }

    TransactionTemplate transactionTemplate() {
        return transactionTemplate;
    }
}
