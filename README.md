# transactional-outbox

An implementation of the transactional-outbox pattern built on rabbitMQ broker.

## Short description

The library delivers publisher service (you may use either: `org.zruc.outbox.async.OutboxPublisher` 
or `org.zruc.outbox.reactive.ReactiveOutboxPublisher`) which participate in application transaction to persist outbox message.
On finishing the main transaction, the library attempts to deliver the outbox message to the broker in a separate thread.

The independent auditor thread collects all outbox-messages that failed or were abandoned for any reason 
(eg. service went down, queue limit was exceeded) and retries delivering.

## Dependencies

* postgres extension: hstore,
* spring-data-jpa to persist messages transactional, 
* spring-stream to publish messages to the broker.
 
## Idea
https://microservices.io/patterns/data/transactional-outbox.html

## build and install

The following command will build and install the artifact in the local maven repository. 

`gradle build`
