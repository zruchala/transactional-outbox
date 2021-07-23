package org.zrc.outbox;

import org.springframework.context.ApplicationEvent;

public class MessageAddedEvent extends ApplicationEvent {
    public MessageAddedEvent(Long msgId) {
        super(msgId);
    }

    public Long getMessageId() {
        return (Long) getSource();
    }
}
