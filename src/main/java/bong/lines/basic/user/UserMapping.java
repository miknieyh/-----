package bong.lines.basic.user;

import bong.lines.basic.comm.mapper.Mapping;
import bong.lines.basic.dto.UserDto;
import bong.lines.basic.resourceviews.code.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserMapping implements Mapping {

    private final String parameter;

    @Override
    public Object map() {

        UserDto.Result resultDto = null;

        try {
            String[] paramBySplit = parameter.split("&");
            User.UserBuilder userBuilder = User.builder();

            for(int i = 0; i < paramBySplit.length; i++){
                String[] parameters = paramBySplit[i].split("=");

                for (String parameter : parameters){
                    switch (parameter){
                        case "name" :
                            userBuilder.name(parameter);
                            break;
                        case "email" :
                            userBuilder.email(parameter);
                            break;
                        case "password":
                            userBuilder.password(parameter);
                            break;
                    }
                }
            }
            resultDto = UserDto.Result.builder()
                    .result(userBuilder.build())
                    .statusCode(StatusCode.SUCCESS)
                    .build();
        }catch (Exception e){
            log.error("Error:{}",e.getMessage());

            resultDto = UserDto.Result.builder()
                    .result(e.getMessage())
                    .statusCode(StatusCode.ERROR)
                    .build();
        }


        return resultDto;
    }
}
