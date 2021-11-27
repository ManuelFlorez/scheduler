package colpatria.schedulermicroservice.domain.elk;

import colpatria.schedulermicroservice.domain.canonico.Canonico;
import colpatria.schedulermicroservice.domain.common.ErrorEnum;
import colpatria.schedulermicroservice.domain.common.StatusEnum;
import com.google.gson.Gson;

public interface ELKFactory {
    default TransactionELK buildObjectTransactionIn(Canonico canonical, String integrationName, String operation, String domainName, String status) {
        Message message = getMessage(status, 1, 0, 0);
        TransactionInformation information = getTransactionInformation("IN", integrationName, operation, domainName, "Successful transaction", status);
        return buildTransaction(canonical, message, information);
    }

    default <T> TransactionELK buildTransaction(Canonico canonico, Message message, TransactionInformation information) {
        return TransactionELK.builder()
                .transactionId(canonico.getHeader().getTransactionId())
                .integrationName(information.getIntegrationName())
                .domainName(information.getDomainName())
                .operation(information.getOperation())
                .type(getFixedType(information.getType()))
                .status(getFixedStatus(information.getStatus()))
                .trace(information.getTrace())
                .messagesIn(message.getMessagesIn())
                .messagesBlocks(message.getMessagesBlocks())
                .messagesOut(message.getMessagesOut())
                .messagesError(message.getMessagesError())
                .messagesFilter(message.getMessagesFilter())
                .mensaje_resultado(getFixedMessageResult(information.getStatus()))
                .response_time(information.getResponseTime())
                .timeStamp(canonico.getHeader().getTransactionDate())
                .event(Event.builder()
                        .header(Header.builder()
                                .transactionId(canonico.getHeader().getTransactionId())
                                .applicationId(canonico.getHeader().getApplicationId())
                                .transactionDate(canonico.getHeader().getTransactionDate())
                                .flexField(information.getFlexField())
                                .build())
                        .data(objectToJson(canonico))
                        .build())
                .build();
    }

    default String objectToJson(Object data){
        var gson = new Gson();
        return gson.toJson(data);
    }

    default Message getMessage(String status, int in, int out, int error) {
        return Message.builder()
                .messagesIn(in)
                .messagesOut(out)
                .messagesFilter(0)
                .messagesBlocks(out)
                .messagesError(error)
                .message_result(status)
                .build();
    }

    default TransactionInformation getTransactionInformation(String type, String integrationName, String operation, String domainName, String trace, String status) {
        return TransactionInformation.builder()
                .type(type)
                .integrationName(integrationName)
                .operation(operation)
                .domainName(domainName)
                .status(status)
                .trace(trace)
                .build();
    }


    default String getFixedStatus(String status) {
        return status.equals(StatusEnum.OK.getMessage())
                ? status
                : getFixedErrorStatus(status);
    }

    default String getFixedErrorStatus(String status) {
        return status.equals(StatusEnum.BRULE.getMessage())
                ? status
                : StatusEnum.ERROR.getMessage();
    }

    default String getFixedType(String type) {
        if (type.isEmpty()) {
            return null;
        }
        final String[] pattern = type.split("_");
        if (pattern.length == 2) {
            return (pattern[0].equalsIgnoreCase("IN"))
                    ? "IN"
                    : "OUT";
        }
        return type;
    }

    default String getFixedMessageResult(String status) {
        return status.equals(StatusEnum.OK.getMessage())
                ? ErrorEnum.Type.EXITOSO.getInformation()
                : getFixedErrorResult(status);
    }

    default String getFixedErrorResult(String status) {
        return status.equals(StatusEnum.ERROR.getMessage())
                ? ErrorEnum.Type.ERROR_TECNICO.getInformation()
                : ErrorEnum.Type.ERROR_NEGOCIO.getInformation();
    }
}
