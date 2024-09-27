package roadmap.backend.image_processing_service.image.application.interfaces.apiRest.transform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transformations{
    @JsonProperty("resize")
    final private Resize resize;
    @JsonProperty("crop")
    final private Crop crop;
    @JsonProperty("rotate")
    final private Integer rotate;
    @JsonProperty("format")
    final private FormatImage format;
    @JsonProperty("filters")
    final private Filters filters;

    @JsonCreator
    public Transformations(
            @JsonProperty("resize") Resize resize,
            @JsonProperty("crop") Crop crop,
            @JsonProperty("rotate") Integer rotate,
            @JsonProperty("format") FormatImage format,
            @JsonProperty("filters") Filters filters) {
        this.resize = resize;
        this.crop = crop;
        this.rotate = rotate;
        this.format = format;
        this.filters = filters;
    }
}
