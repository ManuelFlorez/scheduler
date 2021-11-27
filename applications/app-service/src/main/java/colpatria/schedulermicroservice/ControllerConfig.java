package colpatria.schedulermicroservice;

import colpatria.schedulermicroservice.controller.searchnotifier.SearchNotifierTriggerController;
import colpatria.schedulermicroservice.domain.common.CustomEventsGateway;
import colpatria.schedulermicroservice.domain.common.EventsGateway;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log
@Configuration
public class ControllerConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public SearchNotifierTriggerController searchNotifierTriggerController(CustomEventsGateway customEventsGateway,
                                                                           EventsGateway eventsGateway) {
        return new SearchNotifierTriggerController(customEventsGateway, eventsGateway);
    }

}
