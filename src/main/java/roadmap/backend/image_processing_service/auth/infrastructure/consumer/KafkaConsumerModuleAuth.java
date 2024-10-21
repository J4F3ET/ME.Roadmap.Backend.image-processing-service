package roadmap.backend.image_processing_service.auth.infrastructure.consumer;

import io.micrometer.common.lang.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventByModuleImage;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.message.implement.KafkaMessageAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.auth.infrastructure.producer.KafkaProducerByModuleImageModuleAuth;
import roadmap.backend.image_processing_service.auth.infrastructure.producer.KafkaProducerByModuleTransformsModuleAuth;

@Slf4j
@Service
public class KafkaConsumerModuleAuth {
    private final KafkaEventByModuleImage kafkaEventByModuleImage;
    @Qualifier("kafkaProducerByModuleImageModuleAuth")
    private final KafkaProducerByModuleImageModuleAuth kafkaProducerByModuleImageModuleAuth;
    @Qualifier("kafkaProducerByModuleTransformsModuleAuth")
    private final KafkaProducerByModuleTransformsModuleAuth kafkaProducerByModuleTransformsModuleAuth;
    public KafkaConsumerModuleAuth(
            KafkaEventByModuleImage kafkaEventByModuleImage,
            KafkaProducerByModuleImageModuleAuth kafkaProducerByModuleImageModuleAuth,
            KafkaProducerByModuleTransformsModuleAuth kafkaProducerByModuleTransformsModuleAuth
    ){
        this.kafkaEventByModuleImage = kafkaEventByModuleImage;
        this.kafkaProducerByModuleImageModuleAuth = kafkaProducerByModuleImageModuleAuth;
        this.kafkaProducerByModuleTransformsModuleAuth = kafkaProducerByModuleTransformsModuleAuth;
    }
    @Nullable
    private KafkaMessageImage resolveMethodTypeByModuleImage(KafkaMessageAuth request) {
        switch (request.event()) {
            case SAVE_IMAGE -> {
                return kafkaEventByModuleImage.saveImage(request);
            }
            case UPDATE_IMAGE -> {
                return kafkaEventByModuleImage.updateImage(request);
            }
            case GET_ALL_IMAGES -> {
                return kafkaEventByModuleImage.getAllImages(request);
            }
            case GET_IMAGE -> {
                return kafkaEventByModuleImage.getImage(request);
            }
            case TRANSFORM_IMAGE -> {
                return kafkaEventByModuleImage.transformImage(request);
            }
        }
        return null;
    }
    @Nullable
    private KafkaMessage resolveMethodTypeByModuleTransform(KafkaMessageAuth request) {
        return null;
    }
    @Nullable
    private KafkaMessage resolveMethodType(KafkaMessageAuth request) {
        switch (request.destinationEvent()) {
            case IMAGE  -> {
                return resolveMethodTypeByModuleImage(request);
            }
            case TRANSFORMATION -> {
                 return resolveMethodTypeByModuleTransform(request);
            }
        }
        return null;
    }
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Auth,groupId = "")
    public void listen(@NonNull ConsumerRecord<String, String> record) {
        KafkaMessageAuth request = KafkaMessage.convertToObject(record.value(), KafkaMessageAuth.class);
        if (request == null) return;

        KafkaMessage result = resolveMethodType(request);
        if (result == null) return;

        String jsonResult = result.convertToJson();
        switch (result.destinationEvent()) {
            case IMAGE -> kafkaProducerByModuleImageModuleAuth.send(jsonResult, record.key());
            case TRANSFORMATION -> kafkaProducerByModuleTransformsModuleAuth.send(jsonResult);
        }
    }
}
