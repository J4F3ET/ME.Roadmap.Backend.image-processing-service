package roadmap.backend.image_processing_service.image.application.interfaces.apiRest.transform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
public class Resize implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
