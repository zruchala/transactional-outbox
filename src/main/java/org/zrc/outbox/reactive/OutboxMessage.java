package org.zrc.outbox.reactive;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Table("outbox_msg")
@Getter
@Setter
@Accessors(chain = true)
public class OutboxMessage {

    @Id private long id;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> properties = new HashMap<>();
    private String payload;
    private Timestamp timestamp;
    private Timestamp finishTime;

}
