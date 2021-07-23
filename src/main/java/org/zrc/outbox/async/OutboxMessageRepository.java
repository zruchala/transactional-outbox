package org.zrc.outbox.async;

import org.hibernate.LockOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

@ConditionalOnBean(EntityManagerFactory.class)
public interface OutboxMessageRepository extends CrudRepository<OutboxMessage, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = LockOptions.NO_WAIT + "")})
    Optional<OutboxMessage> findByIdAndFinishTimeIsNull(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = LockOptions.SKIP_LOCKED + "")})
    Optional<OutboxMessage> findFirstByFinishTimeIsNullOrderByTimestampAsc();

    @Modifying
    @Query("update OutboxMessage m set m.finishTime = now() where m.id = :msgId")
    void markMessageAsFinished(@Param("msgId") long msgId);

}
