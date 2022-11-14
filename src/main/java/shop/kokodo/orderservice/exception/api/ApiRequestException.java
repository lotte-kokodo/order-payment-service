package shop.kokodo.orderservice.exception.api;

public class ApiRequestException extends RuntimeException {

    public Object result;

    public ApiRequestException(String message, Object result) {
        super(message);
        this.result = result;
    }

    public ApiRequestException(String message) {
        super(message);
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public Object getResult() {
        return result;
    }
}