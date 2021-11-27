package colpatria.schedulermicroservice.domain.common;

public enum StatusEnum {
    OK("OK"),
    ERROR("ERROR"),
    BRULE("BRULE"),
    ERROR_TECNICO("ERROR TECNICO"),
    ERROR_NEGOCIO("ERROR DE NEGOCIO");

    private String message;

    public String getMessage() {
        return this.message;
    }

    StatusEnum(String message) {
        this.message = message;
    }

}
