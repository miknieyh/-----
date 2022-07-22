package bong.lines.basic.resourceviews.view;

import bong.lines.basic.resourceviews.Resource;
import bong.lines.basic.resourceviews.ResourceParam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataView implements Resource {
    private final ResourceParam resourceParam;
    @Override
    public Object call() throws Exception {
        return resourceParam.getUrl().getBytes();
    }
}
