package shop.kokodo.orderservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaMessageParser {

    private final ObjectMapper objectMapper;

    public KafkaMessageParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readMessageValue(String message, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(message, typeRef);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("");
        }
    }

}
