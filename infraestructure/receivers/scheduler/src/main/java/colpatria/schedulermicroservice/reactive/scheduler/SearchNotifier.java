package colpatria.schedulermicroservice.reactive.scheduler;

import colpatria.schedulermicroservice.controller.searchnotifier.SearchNotifierTriggerController;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchNotifier implements Job {

    private final SearchNotifierTriggerController controller;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        controller.execute().subscribe(t -> {}, Throwable::printStackTrace);
    }

}
