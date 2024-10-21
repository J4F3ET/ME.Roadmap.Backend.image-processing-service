package roadmap.backend.image_processing_service.transforms.application.config.kafka;

import lombok.NonNull;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import roadmap.backend.image_processing_service.transforms.application.config.kafka.topic.TopicConfigProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfigModuleTransforms {
    @NonNull
    public static Map<String, String> getTopicConfig() {
        Map<String, String> configs = new HashMap<>();
        configs.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfigProperties.CLEANUP_POLICY_CONFIG_VALUE);
        configs.put(TopicConfig.RETENTION_MS_CONFIG, TopicConfigProperties.RETENTION_MS_CONFIG_VALUE);
        configs.put(TopicConfig.SEGMENT_BYTES_CONFIG, TopicConfigProperties.SEGMENT_BYTES_CONFIG_VALUE);
        configs.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, TopicConfigProperties.MAX_MESSAGE_BYTES_CONFIG_VALUE);
        return configs;
    }
    @Bean
    @Qualifier("generateTopicModuleImageModuleTransforms")
    public NewTopic generateTopicModuleImageModuleTransforms() {
        return TopicBuilder
                .name(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.TOPIC_NAME_Image)
                .partitions(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.PARTITIONS)
                .replicas(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.REPLICATION_FACTOR)
                .configs(getTopicConfig())
                .build();
    }
    @Bean
    @Qualifier("generateTopicModuleAuthModuleTransforms")
    public NewTopic generateTopicModuleAuthModuleTransforms() {
        return TopicBuilder
                .name(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.TOPIC_NAME_Auth)
                .partitions(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.PARTITIONS)
                .replicas(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.REPLICATION_FACTOR)
                .configs(getTopicConfig())
                .build();
    }
    @Bean
    @Qualifier("generateTopicModuleTransformModuleTransforms")
    public NewTopic generateTopicModuleTransformModuleTransforms() {
        return TopicBuilder
                .name(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.TOPIC_NAME_Transform)
                .partitions(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.PARTITIONS)
                .replicas(roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties.REPLICATION_FACTOR)
                .configs(getTopicConfig())
                .build();
    }
}
