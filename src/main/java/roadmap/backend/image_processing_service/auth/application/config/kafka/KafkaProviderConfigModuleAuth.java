package roadmap.backend.image_processing_service.auth.application.config.kafka;

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
public class KafkaProviderConfigModuleAuth {
    @Value("${spring.kafka.bootstrap-servers}")//Host de Kafka
    private String bootstrapServers;

    @NonNull
    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    //Producer

    @Bean
    @Qualifier("producerFactoryModuleAuth")
    public ProducerFactory<String, String> producerFactoryModuleAuth() {
        return new DefaultKafkaProducerFactory<>(this.producerConfigs());
    }

    @Bean
    @Qualifier("kafkaTemplateModuleAuth")
    public KafkaTemplate<String, String> kafkaTemplateModuleAuth(
    @Qualifier("producerFactoryModuleAuth") ProducerFactory<String, String> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    //Consumer

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    @Qualifier("consumerFactoryModuleAuth")
    public ConsumerFactory<String, String> consumerFactoryModuleAuth() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    @Qualifier("kafkaListenerContainerFactoryModuleAuth")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactoryModuleAuth(
            @Qualifier("consumerFactoryModuleAuth") ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}

