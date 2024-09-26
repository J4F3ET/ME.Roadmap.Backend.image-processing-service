package roadmap.backend.image_processing_service.image.application.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import roadmap.backend.image_processing_service.image.application.config.topic.TopicConfigProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
   @Bean
   public NewTopic generateTopicImageProcessingService() {
       Map<String, String> configs = new HashMap<>();
       configs.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfigProperties.CLEANUP_POLICY_CONFIG_VALUE);
       configs.put(TopicConfig.RETENTION_MS_CONFIG, TopicConfigProperties.RETENTION_MS_CONFIG_VALUE);
       configs.put(TopicConfig.SEGMENT_BYTES_CONFIG, TopicConfigProperties.SEGMENT_BYTES_CONFIG_VALUE);
       configs.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, TopicConfigProperties.MAX_MESSAGE_BYTES_CONFIG_VALUE);
       return TopicBuilder
               .name(TopicConfigProperties.TOPIC_NAME)
               .partitions(TopicConfigProperties.PARTITIONS)
               .replicas(TopicConfigProperties.REPLICATION_FACTOR)
               .configs(configs)
               .build();
   }
}
