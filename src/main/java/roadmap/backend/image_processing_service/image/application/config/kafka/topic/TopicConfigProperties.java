package roadmap.backend.image_processing_service.image.application.config.kafka.topic;

import org.apache.kafka.common.config.TopicConfig;

public class TopicConfigProperties {
    public static final String TOPIC_NAME_ImageProcessingService = "image-processing-service";//Topic name: Nombre del topic = "image-processing-service"
    public static final int PARTITIONS = 2; //Partitions: Numero de particiones
    public static final int REPLICATION_FACTOR = 1;//Replication factor: Factor de replicacion = 1
    public static final String CLEANUP_POLICY_CONFIG_VALUE = TopicConfig.CLEANUP_POLICY_DELETE ;//Cleanup policy: Politca de tratamiento de los datos = "delete"
    public static final String RETENTION_MS_CONFIG_VALUE = "3600000";//Retention time: Tiempo de retencion de los datos = "3600000" milisegundos = 1 hora
    public static final String SEGMENT_BYTES_CONFIG_VALUE = "1073741824";//Segment size: Tamaño de los segmentos = "1073741824" bytes = 1GB
    public static final String MAX_MESSAGE_BYTES_CONFIG_VALUE = "37500000";//Max message size: Tamaño maximo de los mensajes = "3750000" bytes = 150MB + 10%
}
