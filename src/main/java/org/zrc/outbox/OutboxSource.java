package org.zrc.outbox;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OutboxSource {
    String OUTPUT = "outbox";

    @Output(OUTPUT)
    MessageChannel output();
}
