package org.zrc.outbox;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.Map;

@Getter
public class OutboxMessageEvent extends ApplicationEvent {
    private final Map<String, String> headers;
    private final Map<String, String> properties;

    public OutboxMessageEvent(String payload) {
        this(payload, Collections.emptyMap());
    }

    public OutboxMessageEvent(String payload, Map<String, String> headers) {
        this(payload, headers, Collections.emptyMap());
    }

    public OutboxMessageEvent(String payload, Map<String, String> headers, Map<String, String> properties) {
        super(payload);
        this.headers = headers;
        this.properties = properties;
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }
}
