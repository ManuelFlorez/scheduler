package colpatria.schedulermicroservice.domain.common;

public interface Event {
    String name();
    Object getData();
}
