package colpatria.schedulermicroservice.controller.searchnotifier;

import colpatria.schedulermicroservice.domain.canonical.CanonicoFactory;
import colpatria.schedulermicroservice.domain.common.CustomEventsGateway;
import colpatria.schedulermicroservice.domain.common.EventsGateway;
import colpatria.schedulermicroservice.domain.elk.ELKFactory;
import colpatria.schedulermicroservice.domain.elk.TransactionToELK;
import colpatria.schedulermicroservice.domain.searchnotifier.SearchNotifierEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public class SearchNotifierTriggerController implements CanonicoFactory, ELKFactory {
    private static final String DOMAIN = "col";
    private static final String OK_STATUS = "OK";

    private final CustomEventsGateway customEventsGateway;
    private final EventsGateway eventsGateway;

    public Mono<Void> execute() {
        return Mono.just(SearchNotifierEvent.builder()
                .command("SCHEDULER - run search api notifier")
                .build())
                .flatMap(this::sendTransactionComplete)
                .flatMap(x -> sendTransactionToELK(
                        "scheduler-microservice",
                        "search-api-rest",
                        DOMAIN,
                        OK_STATUS
                ))
                .then();
    }

    private Mono<SearchNotifierEvent> sendTransactionComplete(SearchNotifierEvent event) {
        return eventsGateway.emit(event).thenReturn(event);
    }

    private Mono<Void> sendTransactionToELK(String integrationName, String operation, String domainName, String status) {
        final String uuid = UUID.randomUUID().toString();
        return Mono.just(
                createGenericSchedulerCanonico("SCHEDULER - run search api notifier", uuid, "search-api-rest"))
                .doOnNext(canonico -> log.log(Level.INFO, "canonico: {0}", canonico))
                .map(canonico -> buildObjectTransactionIn(canonico, integrationName, operation, domainName, status))
                .map(TransactionToELK::new)
                .flatMap(customEventsGateway::emit);
    }
}
