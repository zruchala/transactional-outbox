package org.zrc.outbox.reactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ReactiveOutboxPublisher {

    @Autowired private ReactiveOutboxMessageService reactiveOutboxMessageService;

    public Mono<Void> publish(String payload, Map<String, String> headers) {
        return reactiveOutboxMessageService.save(payload, headers);
    }
}
