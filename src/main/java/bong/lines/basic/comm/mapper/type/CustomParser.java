package bong.lines.basic.comm.mapper.type;

import bong.lines.basic.comm.mapper.ModelParam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomParser implements Parser<Object> {

    private final ModelParam modelParam;
    @Override
    public Object execute() {
        return modelParam
                .getMapping()
                .map();
    }
}
