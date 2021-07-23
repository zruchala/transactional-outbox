package org.zrc.outbox.async;

import org.zrc.outbox.OutboxSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class OutboxMessageService {

    @Autowired private OutboxSource outboxSource;
    @Autowired private OutboxMessageRepository outboxMessageRepository;

    public boolean process(long msgId) {
        return processSingle(outboxMessageRepository.findByIdAndFinishTimeIsNull(msgId));
    }

    public boolean processNext() {
        return processSingle(outboxMessageRepository.findFirstByFinishTimeIsNullOrderByTimestampAsc());
    }

    private boolean processSingle(Optional<OutboxMessage> optional) {
        if (optional.isPresent()) {
            doProcess(optional.get());
            return true;
        }
        return false;
    }

    private void doProcess(OutboxMessage message) {
        try {
            // there is always a negligible risks that between message delivery and commit some failure happen thus
            // messages have to be idempotent.
            if (send(message)) {
                outboxMessageRepository.markMessageAsFinished(message.getId());
            }
        } catch (Throwable throwable) {
            log.error("An unexpected error happened", throwable);
            throw throwable;
        }
    }

    private boolean send(OutboxMessage outbox) {
        var success = true;
        @SuppressWarnings("unchecked")
        var message = new GenericMessage(outbox.getPayload(), outbox.getHeaders());
        if (!outboxSource.output().send(message, 100)) {
            log.warn("OutboxMessage sending failed {}", outbox.getId());
            success = false;
        }
        return success;
    }

    public void save(OutboxMessage outboxMessage) {
        outboxMessageRepository.save(outboxMessage);
    }
}
