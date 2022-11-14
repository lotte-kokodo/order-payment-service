package shop.kokodo.orderservice.exception;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.kokodo.orderservice.dto.response.Response;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Response handleIllegalArgumentException(final IllegalArgumentException e) {
        log.debug("[GlobalExceptionHandler] handle exception (cause = {})",
            Arrays.toString(e.getStackTrace()));

        return Response.failure(500, e.getMessage());
    }
}
