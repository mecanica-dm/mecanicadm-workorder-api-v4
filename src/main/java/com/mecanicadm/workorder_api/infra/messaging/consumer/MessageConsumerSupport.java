package com.mecanicadm.workorder_api.infra.messaging.consumer;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.infra.messaging.inbox.InboxJpaRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class MessageConsumerSupport {
    private final InboxJpaRepository inbox;
    private final MessagePublisher publisher;
    private final MessageSource messageSource;
    private final TransactionTemplate transactionTemplate;

    public MessageConsumerSupport(InboxJpaRepository inbox,
                                  MessagePublisher publisher,
                                  MessageSource messageSource,
                                  PlatformTransactionManager transactionManager) {
        this.inbox = inbox;
        this.publisher = publisher;
        this.messageSource = messageSource;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    InboxJpaRepository inbox() {
        return inbox;
    }

    MessagePublisher publisher() {
        return publisher;
    }

    MessageSource messageSource() {
        return messageSource;
    }

    TransactionTemplate transactionTemplate() {
        return transactionTemplate;
    }
}
