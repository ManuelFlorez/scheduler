package colpatria.schedulermicroservice.domain.elk;

import colpatria.schedulermicroservice.domain.common.Event;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class TransactionToELK implements Event {
    public static final String EVENT_NAME = "traceability.queue";
    private final TransactionELK transaction;

    @Override
    public String name() {
        return EVENT_NAME;
    }

    @Override
    public Object getData() {
        return this.transaction;
    }
}
