package bong.lines.basic.comm.mapper;

import bong.lines.basic.comm.mapper.code.ParserType;
import bong.lines.basic.comm.mapper.type.CustomParser;
import bong.lines.basic.comm.mapper.type.QueryStringParser;
import bong.lines.basic.comm.mapper.type.ViewParser;

public class ModelMapper<ParamT, ReturnT> {
    public ReturnT parse(ParserType parserType, ModelParam modelParam){
        switch (parserType){
            case CustomMapping :
                return (ReturnT) new CustomParser(modelParam).execute();
            default:
                return null;
        }
    }
    public ReturnT parse(ParserType parserType, ParamT paramT){
        switch (parserType){
            case QueryString:
                return (ReturnT) new QueryStringParser(paramT).execute();
            default:
                return (ReturnT) new ViewParser(paramT).execute();
        }
    }
}
