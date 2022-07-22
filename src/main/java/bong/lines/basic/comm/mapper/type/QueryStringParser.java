package bong.lines.basic.comm.mapper.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryStringParser<ParamT> implements Parser<String> {

    private final ParamT paramT;
    @Override
    public String execute() {
        String url = ((String)paramT).split(" ")[1];
        // parameter return
        return url.split("\\?")[1];
    }
}
