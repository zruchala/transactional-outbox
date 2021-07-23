package org.zrc.outbox;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

@ConfigurationProperties(prefix = "outbox.publisher")
@Data
public class OutboxPublisherProperties {

    private Map<Class<?>, String> headers = Collections.emptyMap();

}
