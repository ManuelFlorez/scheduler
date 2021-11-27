package colpatria.schedulermicroservice.domain.canonical;

import colpatria.schedulermicroservice.domain.canonico.Canonico;
import colpatria.schedulermicroservice.domain.canonico.Header;

import java.util.Date;

public interface CanonicoFactory {
    default <T> Canonico createGenericSchedulerCanonico(T data, String uuid, String appId) {
        return Canonico.builder()
                .header(Header.builder()
                        .applicationId(appId)
                        .hostname("host-scheduler")
                        .transactionDate(new Date())
                        .transactionId(uuid)
                        .user("user-scheduler")
                        .build())
                .data(data)
                .build();
    }
}
