package roadmap.backend.image_processing_service.image.application.interfaces.apiRest.transform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class Crop{
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("x")
    private Integer x;
    @JsonProperty("y")
    private Integer y;

    @JsonCreator
    public Crop(
            @JsonProperty("width") Integer width,
            @JsonProperty("height") Integer height,
            @JsonProperty("x") Integer x,
            @JsonProperty("y") Integer y
    ) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
}


