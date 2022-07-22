package bong.lines.basic.dto;

import bong.lines.basic.resourceviews.code.ResponseCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDTO {

    private final byte[] body;
    private final ResponseCode responseCode;
}
