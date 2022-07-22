package bong.lines.basic.user;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class User {
    private final String userId;
    private final String password;
    private final String name;
    private final String email;
}
