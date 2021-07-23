package org.zrc.outbox.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.zrc.outbox.AuditorScheduler;
import org.zrc.outbox.OutboxPublisherProperties;
import org.zrc.outbox.OutboxSource;

@Configuration
@EnableScheduling
@EnableBinding(OutboxSource.class)
@EnableAsync
@AutoConfigurationPackage
@EnableConfigurationProperties(OutboxPublisherProperties.class)
@Slf4j
public class OutboxAutoConfiguration {

    static final String OUTBOX_THREAD_POOL_NAME = "outbox_thread";

    @Bean(name = OUTBOX_THREAD_POOL_NAME)
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("OutboxExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) ->
                log.warn("Task " + r.toString() + " rejected from " + executor1.toString()));
        executor.initialize();
        return executor;
    }

    @Bean
    public OutboxMessageService outboxMessageService() {
        return new OutboxMessageService();
    }

    @Bean
    public LockingMessageRelay messageRelay() {
        return new LockingMessageRelay();
    }

    @Bean
    public AuditorScheduler auditorScheduler() {
        return new AuditorScheduler();
    }

    @Bean
    public OutboxPublisher outboxPublisher() {
        return new OutboxPublisher();
    }

}
