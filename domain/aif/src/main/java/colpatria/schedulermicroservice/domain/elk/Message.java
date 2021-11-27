package colpatria.schedulermicroservice.domain.elk;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Message {
    private final Integer messagesIn;
    private final Integer messagesBlocks;
    private final Integer messagesOut;
    private final Integer messagesError;
    private final Integer messagesFilter;
    private final String message_result;
}
