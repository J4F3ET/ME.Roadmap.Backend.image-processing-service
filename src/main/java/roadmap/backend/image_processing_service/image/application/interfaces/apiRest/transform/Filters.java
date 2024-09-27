package roadmap.backend.image_processing_service.image.application.interfaces.apiRest.transform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Filters{
    @JsonProperty("grayscale")
    private Boolean grayscale;
    @JsonProperty("sepia")
    private Boolean sepia;
    @JsonCreator
    public Filters(
            @JsonProperty("grayscale") Boolean grayscale,
            @JsonProperty("sepia") Boolean sepia
    ) {
        this.grayscale = grayscale;
        this.sepia = sepia;
    }
}
