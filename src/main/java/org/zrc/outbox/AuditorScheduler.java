package org.zrc.outbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuditorScheduler {
    @Autowired private ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(cron = "1 * * * * *")
    public void schedule() {
        applicationEventPublisher.publishEvent(new AuditorEvent());
    }
}
