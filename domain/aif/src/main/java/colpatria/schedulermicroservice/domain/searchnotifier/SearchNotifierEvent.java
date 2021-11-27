package colpatria.schedulermicroservice.domain.searchnotifier;

import colpatria.schedulermicroservice.domain.common.Event;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class SearchNotifierEvent implements Event {

    public static final String EVENT_NAME = "col.search.api";
    private final String command;

    @Override
    public String name() {
        return EVENT_NAME;
    }

    @Override
    public Object getData() {
        return command;
    }
}
