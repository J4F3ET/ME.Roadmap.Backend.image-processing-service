package roadmap.backend.image_processing_service.auth.application.interfaces.event.component;

public enum KafkaEvent {
    // Eventos que se emiten por modulo de Imagen
    SAVE_IMAGE,
    UPDATE_IMAGE,

    GET_ALL_IMAGES,
    GET_IMAGE,

    TRANSFORM_IMAGE
    // Eventos que se emiten por modulo de Auth

    // Eventos que se emiten por modulo de Transformacion
}
