package bong.lines.basic.resourceviews;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceParam {
    private final String screen;
    private final String url;
}
