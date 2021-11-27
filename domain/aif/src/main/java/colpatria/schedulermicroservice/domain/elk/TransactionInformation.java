package colpatria.schedulermicroservice.domain.elk;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
public class TransactionInformation<F> implements Serializable {
    private final String type;
    private final String integrationName;
    private final String operation;
    private final String domainName;
    private final String status;
    private final String trace;
    private final Integer responseTime;
    private final F flexField;
}
