package colpatria.schedulermicroservice.domain.canonico;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Header {
    private final String transactionId;
    private final String applicationId;
    private final String hostname;
    private final String user;
    private final Date transactionDate;
    private final String esb;
    private final List<ErrorModel> errors;
}
