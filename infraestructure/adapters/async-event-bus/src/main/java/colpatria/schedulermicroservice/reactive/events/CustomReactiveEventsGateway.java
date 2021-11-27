package colpatria.schedulermicroservice.reactive.events;

import colpatria.schedulermicroservice.domain.common.CustomEventsGateway;
import colpatria.schedulermicroservice.domain.common.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.logging.Level;

import static reactor.core.publisher.Mono.from;

@Log
@Component
@RequiredArgsConstructor
public class CustomReactiveEventsGateway implements CustomEventsGateway {

    @Value("${elk.rabbitmq.exchange}")
    private String elkExchange;

    private final CustomReactiveMessageSender customReactiveMessageSender;

    @Override
    public Mono<Void> emit(Event event) {
        log.log(Level.INFO, "Emitiendo evento de dominio: {0}: {1}", new String[]{event.name(), event.toString()});
        return from(customReactiveMessageSender.sendWithConfirm(event.getData(), elkExchange, event.name(), Collections.emptyMap())
                .onErrorMap(err -> new RuntimeException("Fallo enviando el evento: " + event.name(), err)));
    }
}
