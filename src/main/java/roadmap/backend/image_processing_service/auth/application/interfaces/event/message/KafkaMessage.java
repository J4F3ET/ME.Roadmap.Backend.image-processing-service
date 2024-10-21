package roadmap.backend.image_processing_service.auth.application.interfaces.event.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.Nullable;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.DestinationEvent;

import java.io.Serializable;

public interface KafkaMessage extends Serializable {
    DestinationEvent destinationEvent();
    default String convertToJson(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    };
    @Nullable
    static <T extends KafkaMessage> T convertToObject(String message, Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
