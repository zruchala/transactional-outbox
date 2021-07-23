package org.zrc.outbox.reactive;

import org.zrc.outbox.AuditorEvent;
import org.zrc.outbox.OutboxSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReactiveOutboxMessageService {

    @Autowired private OutboxSource outboxSource;
    @Autowired private OutboxMessageRepository outboxMessageRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Mono<Void> save(String payload, Map<String, String> headers) {
        return outboxMessageRepository.save(new OutboxMessage().setHeaders(headers).setPayload(payload))
            .flatMap(apiKey ->
                TransactionSynchronizationManager.forCurrentTransaction()
                    .map(sync -> {
                        sync.registerSynchronization(new TransactionSynchronization() {
                            @Override
                            public Mono<Void> afterCommit() {
                                // schedule an independently outbox-message processing on finishing transaction.
                                Schedulers.single().schedule(() -> process(apiKey.getId()).subscribe());
                                return Mono.empty();
                            }
                        });
                        return sync;
                    })).then();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Mono<Boolean> process(long msgId) {
        return outboxMessageRepository.findByIdAndFinishTimeIsNull(msgId)
                .flatMap(this::processSingle);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Mono<Boolean> processNext() {
        return outboxMessageRepository.findFirstByFinishTimeIsNullOrderByTimestampAsc()
                .flatMap(this::processSingle)
                .flatMap(result -> result ? Mono.just(true) : Mono.empty());
    }

    private Mono<Boolean> processSingle(OutboxMessage message) {
        return send(message)
                .flatMap(msg -> outboxMessageRepository.markMessageAsFinished(msg.getId()));
    }

    private Mono<OutboxMessage> send(OutboxMessage outbox) {
        return Mono.fromCallable(() -> {
            var message = new GenericMessage(outbox.getPayload(), outbox.getHeaders());
            if (!outboxSource.output().send(message, 100)) {
                log.warn("OutboxMessage sending failed {}", outbox.getId());
                return null;
            }
            return outbox;
        });
    }

    @EventListener(value = AuditorEvent.class)
    public void handle(AuditorEvent event) {
        processNext().expand(r -> processNext()).subscribe();
    }

}
