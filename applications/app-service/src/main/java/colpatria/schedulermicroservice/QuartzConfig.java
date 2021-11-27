package colpatria.schedulermicroservice;

import colpatria.schedulermicroservice.reactive.scheduler.SearchNotifier;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class QuartzConfig {

    private static final String TIME_ZONE = "GMT-05:00";

    @Bean
    @Qualifier("jobSearchApiRestScheduler")
    public JobDetail jobSearchApiRestScheduler() {
        return JobBuilder.newJob(SearchNotifier.class)
                .withIdentity("SearchApiRestScheduler")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger jobTriggerSearchApiRestScheduler(
            @Qualifier("jobSearchApiRestScheduler") JobDetail jobSearchApiRestScheduler,
            @Value("${frequency.schedule.process.search.api.rest}") String searchApiRestScheduler) {
        return TriggerBuilder.newTrigger()
                .forJob(jobSearchApiRestScheduler)
                .withIdentity("jobSearchApiRestScheduler")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(searchApiRestScheduler).inTimeZone(TimeZone.getTimeZone(TIME_ZONE)))
                .build();
    }

}
