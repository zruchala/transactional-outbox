package org.zrc.outbox.reactive;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface OutboxMessageRepository extends ReactiveCrudRepository<OutboxMessage, Long> {

    @Query("select * from outbox_msg m where m.id = :id and finish_time is null for update of m nowait")
    Mono<OutboxMessage> findByIdAndFinishTimeIsNull(long id);

    @Query("select * from outbox_msg m where finish_time is null order by timestamp asc limit 1 for update of m skip locked")
    Mono<OutboxMessage> findFirstByFinishTimeIsNullOrderByTimestampAsc();

    @Modifying
    @Query("update outbox_msg m set finish_time = now() where id = :msgId")
    Mono<Boolean> markMessageAsFinished(@Param("msgId") long msgId);

}
