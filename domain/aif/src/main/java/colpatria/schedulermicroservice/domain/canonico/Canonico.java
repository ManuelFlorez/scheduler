package colpatria.schedulermicroservice.domain.canonico;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Canonico<T> {
    private final Header header;
    private final T data;
}
