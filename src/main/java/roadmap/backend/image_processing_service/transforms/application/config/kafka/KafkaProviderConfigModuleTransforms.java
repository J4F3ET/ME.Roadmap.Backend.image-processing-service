package roadmap.backend.image_processing_service.transforms.application.config.kafka;

import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProviderConfigModuleTransforms {
    @Value("${spring.kafka.bootstrap-servers}")//Host de Kafka
    private String bootstrapServers;

    // Producer
    @NonNull
    private Map<String, Object> producerConfigsKafkaMessage() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    @Bean
    @Qualifier("producerFactoryModuleTransforms")
    public ProducerFactory<String, String> producerFactoryModuleTransforms() {
        return new DefaultKafkaProducerFactory<>(this.producerConfigsKafkaMessage());
    }

    @Bean
    @Qualifier("kafkaTemplateModuleTransforms")
    public KafkaTemplate<String, String> kafkaTemplateModuleTransforms(
            @Qualifier("producerFactoryModuleTransforms") ProducerFactory<String, String> producerFactoryModuleTransforms
    ){
        return new KafkaTemplate<>(producerFactoryModuleTransforms);
    }
    // Consumer que usa kafkaMessage
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    @Qualifier("consumerFactoryModuleTransforms")
    public ConsumerFactory<String, String> consumerFactoryModuleTransforms() {
        return new DefaultKafkaConsumerFactory<>(this.consumerConfigs());
    }

    @Bean
    @Qualifier("kafkaListenerContainerFactoryModuleTransforms")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactoryModuleTransforms(
            @Qualifier("consumerFactoryModuleTransforms") ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }


}