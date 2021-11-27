package colpatria.schedulermicroservice.reactive.events;

import colpatria.schedulermicroservice.domain.common.Event;
import colpatria.schedulermicroservice.domain.common.EventsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Level;

import static reactor.core.publisher.Mono.from;

@Log
@Component
@EnableDomainEventBus
@RequiredArgsConstructor
public class ReactiveEventsGateway implements EventsGateway {

    private final DomainEventBus domainEventBus;

    @Override
    public Mono<Void> emit(Event event) {
        log.log(Level.INFO, "Emitiendo evento de dominio: {0}: {1}", new String[]{event.name(), event.toString()});
        return from(domainEventBus.emit(new DomainEvent<>(event.name(), UUID.randomUUID().toString(), event.getData())));
    }
}
