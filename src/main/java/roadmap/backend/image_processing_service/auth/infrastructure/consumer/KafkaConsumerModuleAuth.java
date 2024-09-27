package roadmap.backend.image_processing_service.auth.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.auth.application.interfaces.kafka.*;

import java.util.function.Supplier;

@Slf4j
@Service
public class KafkaConsumerModuleAuth {
    private final KafkaServiceModuleAuth kafkaAuthServices;
    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaConsumerModuleAuth(KafkaServiceModuleAuth kafkaAuthServices, @Qualifier("kafkaTemplateModuleAuth") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaAuthServices = kafkaAuthServices;
        this.kafkaTemplate = kafkaTemplate;
    }
    public AuthKafkaRequest jsonToObject(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, AuthKafkaRequest.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_ImageProcessingService,groupId = "")
    public void listen(String message) {
        log.info("Kafka consumer module image");
        System.out.println(message);
        AuthKafkaRequest request = jsonToObject(message);
        System.out.println(request);
        if (request == null) {
            log.info("Kafka consumer module image");
            return;
        }
        Supplier<String> supplier = getMethodType(request);
        if (supplier == null) {
            log.info("Kafka consumer module image");
            return;
        }
        String result = supplier.get();
        System.out.println(result);
        if (result == null) {
            log.info("Kafka consumer module image");
            return;
        }
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_ImageProcessingService, result);
    }
    @Nullable
    private Supplier<String> getMethodType(@NonNull AuthKafkaRequest request) {
        return switch (request.methodType()) {
            case USERID_USERNAME_TOKEN -> () -> kafkaAuthServices.userIdUsernameToken(request.args()[0]);
            case USERNAME_TOKEN -> () -> kafkaAuthServices.usernameToken(request.args()[0]);
            case USERID_TOKEN -> () -> kafkaAuthServices.userIdToken(request.args()[0]);
            default -> null;
        };
    }
}
