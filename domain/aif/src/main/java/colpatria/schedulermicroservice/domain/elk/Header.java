package colpatria.schedulermicroservice.domain.elk;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder(toBuilder = true)
public class Header<T> {
    private final String transactionId;
    private final String applicationId;
    private final Date transactionDate;
    private final T flexField;
}
