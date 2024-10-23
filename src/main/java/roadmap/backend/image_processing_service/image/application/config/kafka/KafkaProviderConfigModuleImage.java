package roadmap.backend.image_processing_service.image.application.config.kafka;

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
public class KafkaProviderConfigModuleImage {
    @Value("${spring.kafka.bootstrap-servers}")//Host de Kafka
    private String bootstrapServers;
    //Producer Strings
    @NonNull
    private Map<String, Object> producerConfigsString() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    @Bean
    @Qualifier("producerFactoryModuleImage")
    public ProducerFactory<String, String> producerFactoryModuleImage() {
        return new DefaultKafkaProducerFactory<>(this.producerConfigsString());
    }
    @Bean
    @Qualifier("kafkaTemplateModuleImage")
    public KafkaTemplate<String, String> kafkaTemplateModuleImage(
            @Qualifier("producerFactoryModuleImage") ProducerFactory<String, String> producerFactoryModuleImage
    ){
        return new KafkaTemplate<>(producerFactoryModuleImage);
    }
    // Consumer
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
    @Bean
    @Qualifier("consumerFactoryModuleImage")
    public ConsumerFactory<String, String> consumerFactoryModuleImage() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }
    @Bean
    @Qualifier("kafkaListenerContainerFactoryModuleImage")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactoryModuleImage(
            @Qualifier("consumerFactoryModuleImage") ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }


}