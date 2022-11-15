package shop.kokodo.orderservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Success<T> implements Result {
    private T data;
}