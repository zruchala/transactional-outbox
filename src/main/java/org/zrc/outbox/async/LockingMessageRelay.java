package org.zrc.outbox.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.zrc.outbox.AuditorEvent;
import org.zrc.outbox.MessageAddedEvent;
import org.zrc.outbox.OutboxMessageEvent;

import static org.zrc.outbox.async.OutboxAutoConfiguration.OUTBOX_THREAD_POOL_NAME;

@Component
@Slf4j
public class LockingMessageRelay {

    @Autowired private OutboxMessageService outboxMessageService;
    @Autowired private ApplicationEventPublisher applicationEventPublisher;

    @TransactionalEventListener(value = MessageAddedEvent.class)
    @Async(OUTBOX_THREAD_POOL_NAME)
    public void handle(MessageAddedEvent event) {
        log.debug("MessageAddedEvent: Thread {} has started execution", Thread.currentThread().getId());
        outboxMessageService.process(event.getMessageId());
        log.debug("Thread {} has finished execution", Thread.currentThread().getId());
    }

    @EventListener(value = OutboxMessageEvent.class)
    public void handle(OutboxMessageEvent event) {
        log.debug("OutboxMessageEvent: Current thread: {}", Thread.currentThread().getId());
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalArgumentException("no active transaction ?");
        }

        var msg = new OutboxMessage()
                .setPayload(event.getSource())
                .setHeaders(event.getHeaders())
                .setProperties(event.getProperties());
        outboxMessageService.save(msg);
        // the message will be processed AFTER_COMMIT.
        applicationEventPublisher.publishEvent(new MessageAddedEvent(msg.getId()));
    }

    @EventListener(value = AuditorEvent.class)
    @Async(OUTBOX_THREAD_POOL_NAME)
    public void handle(AuditorEvent event) {
        while(outboxMessageService.processNext());
    }
}
