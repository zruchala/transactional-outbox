package org.zrc.outbox.async;

import org.zrc.outbox.async.hstore.HstoreType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "outbox_msg")
@TypeDefs({
    @TypeDef(name = "hstore", typeClass = HstoreType.class)
})
@Getter
@Setter
@Accessors(chain = true)
@DynamicUpdate
public class OutboxMessage {

    @Id
    @GeneratedValue(generator = "outbox-sequence-generator")
    @GenericGenerator(
            name = "outbox-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "prefer_sequence_per_entity", value = "true"),
                    @Parameter(name = "increment_size", value = "10")
            }
    )
    protected long id;

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore", updatable = false)
    private Map<String, String> headers = new HashMap<>();

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore", updatable = false)
    private Map<String, String> properties = new HashMap<>();

    @Column(columnDefinition = "text", updatable = false)
    private String payload;

    @CreationTimestamp
    @Column(insertable = false, updatable = false,
            columnDefinition = "timestamp not null default (now() at time zone 'utc')")
    private Timestamp timestamp;

    @Column(insertable = false)
    private Timestamp finishTime;

}
