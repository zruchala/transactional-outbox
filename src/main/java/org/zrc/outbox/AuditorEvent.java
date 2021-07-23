package org.zrc.outbox;

import org.springframework.context.ApplicationEvent;

public class AuditorEvent extends ApplicationEvent {
    public AuditorEvent() {
        super("");
    }
}
