package colpatria.schedulermicroservice.domain.common;

public class ErrorEnum {
    public enum Type {
        EXITOSO("Ejecucion exitosa", "0"),
        EXITOSO_SIN_DATOS("Ejecucion exitosa sin datos retornados", "2"),
        ERROR_TECNICO("Error tecnico", "3"),
        ERROR_PARAMETROS_ENTRADA("Error en parametros de entrada", "4"),
        ERROR_NEGOCIO("Error de negocio", "5");

        private String message;
        private String code;

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }

        public String getInformation(){
            return code + "-" + message;
        }

        Type(String message, String code) {
            this.message = message;
            this.code = code;
        }
    }
}
