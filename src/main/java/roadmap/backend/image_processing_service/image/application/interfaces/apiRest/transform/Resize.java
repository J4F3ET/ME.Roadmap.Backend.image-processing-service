package roadmap.backend.image_processing_service.image.application.interfaces.apiRest.transform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Resize{
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("height")
    private Integer height;
    @JsonCreator
    public Resize(
            @JsonProperty("width") Integer width,
            @JsonProperty("height") Integer height
    ) {
        this.width = width;
        this.height = height;
    }
}
