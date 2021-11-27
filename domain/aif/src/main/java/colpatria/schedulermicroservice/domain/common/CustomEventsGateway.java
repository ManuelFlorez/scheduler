package colpatria.schedulermicroservice.domain.common;

import reactor.core.publisher.Mono;

/**
 * Interfaz propia del dominio para emitir eventos de trazabilidad a elk
 */
public interface CustomEventsGateway {
    Mono<Void> emit(Event event);
}
