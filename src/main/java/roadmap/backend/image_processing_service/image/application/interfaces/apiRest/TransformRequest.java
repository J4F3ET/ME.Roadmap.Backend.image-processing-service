package roadmap.backend.image_processing_service.image.application.interfaces.apiRest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.transform.Transformations;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransformRequest{
    @JsonProperty("transformations")
    private Transformations transformations;
    @JsonCreator
    public TransformRequest(
            @JsonProperty("transformations")Transformations transform
    ){
        this.transformations = transform;
    }
}

