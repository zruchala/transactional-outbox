package org.zrc.outbox.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zrc.outbox.OutboxMessageEvent;

import java.util.Map;

@Component
public class OutboxPublisher {

    @Autowired private ApplicationEventPublisher applicationEventPublisher;

    public void publish(String payload, Map<String, String> headers) {
        applicationEventPublisher.publishEvent(new OutboxMessageEvent(payload, headers));
    }
}
