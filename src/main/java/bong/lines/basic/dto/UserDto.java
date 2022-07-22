package bong.lines.basic.dto;

import bong.lines.basic.resourceviews.code.StatusCode;
import lombok.Builder;
import lombok.Getter;

public class UserDto {

    @Getter
    @Builder
    public static class Result{
        private final StatusCode statusCode;
        private final Object result;
    }
}
