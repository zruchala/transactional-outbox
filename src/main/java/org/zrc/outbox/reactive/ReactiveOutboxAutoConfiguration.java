package org.zrc.outbox.reactive;

import org.zrc.outbox.AuditorScheduler;
import org.zrc.outbox.OutboxPublisherProperties;
import org.zrc.outbox.OutboxSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableBinding(OutboxSource.class)
@AutoConfigurationPackage
@EnableConfigurationProperties(OutboxPublisherProperties.class)
@Slf4j
public class ReactiveOutboxAutoConfiguration {

    @Bean
    public ReactiveOutboxPublisher reactiveOutboxPublisher() {
        return new ReactiveOutboxPublisher();
    }

    @Bean
    public ReactiveOutboxMessageService reactiveOutboxMessageService() {
        return new ReactiveOutboxMessageService();
    }

    @Bean
    public AuditorScheduler auditorScheduler() {
        return new AuditorScheduler();
    }
}
