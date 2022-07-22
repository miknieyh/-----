package bong.lines.basic.handler.loginsucceshtml;

import bong.lines.basic.comm.mapper.ModelMapper;
import bong.lines.basic.comm.mapper.ModelParam;
import bong.lines.basic.comm.mapper.type.Parser;
import bong.lines.basic.dto.ResponseDTO;
import bong.lines.basic.dto.UserDto;
import bong.lines.basic.resourceviews.code.ResponseCode;
import bong.lines.basic.user.UserMapping;
import bong.lines.basic.comm.mapper.code.ParserType;
import bong.lines.basic.handler.getindexhtml.IndexHTMLHandler;
import bong.lines.basic.resourceviews.Resource;
import bong.lines.basic.resourceviews.ResourceParam;
import bong.lines.basic.resourceviews.code.ResourceType;
import bong.lines.basic.resourceviews.factory.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.io.*;
import java.net.Socket;
import java.util.Optional;

public class LoginSuccessHtml extends Thread{

    private static final Logger log = LoggerFactory.getLogger(IndexHTMLHandler.class);
    private static final ModelMapper<String,String> modelmapperForString = new ModelMapper<>();
    private static final ModelMapper<String,UserDto.Result> modelmapperForObject = new ModelMapper<>();

    private Socket connection;

    public LoginSuccessHtml(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}, Host Address : {}", connection.getInetAddress(), connection.getPort(), connection.getInetAddress().getHostAddress());

        try(InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            ResponseDTO responseDTO = getBytesFromRequest(bufferedReader);

            Optional.ofNullable(responseDTO)
                    .ifPresent(
                            responseItem -> {
                                byte[] body = responseItem.getBody();
                                DataOutputStream dos = new DataOutputStream(out);
                                response200Header(dos,body.length,responseDTO.getResponseCode());
                                responseBody(dos,body);
                            }
                    );

        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }

    private ResponseDTO getBytesFromRequest(BufferedReader bufferedReader) throws Exception{
        String line = bufferedReader.readLine();
        
        log.info("line - {}",line);
        
        if(isIco(line)) return null;
        
        ResponseDTO.ResponseDTOBuilder responseDTOBuilder;
        
        if(isGet(line)){
            responseDTOBuilder = getResponseDTOForGet(line);
        }else if(isPost(line,"POST")){
            responseDTOBuilder = getResponseDTOForPost(bufferedReader);
        }else {
            responseDTOBuilder = getResponseDTOForETC(line);
        }
        return responseDTOBuilder.build();
    }

    private ResponseDTO.ResponseDTOBuilder getResponseDTOForETC(String line) throws Exception {
        ResponseDTO.ResponseDTOBuilder responseDTOBuilder;
        String urlName = "";

        if(line.indexOf("/user/create")> -1) {
            line = line.split("\\?")[1];

            urlName = modelmapperForObject.parse(ParserType.CustomMapping,
                    ModelParam.builder()
                            .mapping(new UserMapping(line))
                            .build()).toString();
        }else {
            urlName = modelmapperForString.parse(ParserType.QueryString,line);
        }
        log.debug("urlName:{}",urlName);

        Resource resource = ResourceFactory.getResource(ResourceType.DATA,
                ResourceParam
                        .builder()
                        .url(urlName)
                        .build()
        );

        responseDTOBuilder =
                ResponseDTO.builder()
                        .body((byte[]) resource.call())
                        .responseCode(ResponseCode.HTTP_200);
        return responseDTOBuilder;
    }

    private ResponseDTO.ResponseDTOBuilder getResponseDTOForPost(BufferedReader bufferedReader) throws Exception {
        ResponseDTO.ResponseDTOBuilder responseDTOBuilder;

        String lineForPost = null;
        int contentLength = 0;

        while (!(lineForPost = bufferedReader.readLine()).equals("")){
            log.info("Result : {} {}", lineForPost, Optional.ofNullable(lineForPost).isPresent());

            if(lineForPost.contains("Content-Length")){
                contentLength = Integer.valueOf(lineForPost.replace("Content-Length:", "").trim());
                log.info("check content length : {}", contentLength );
            }
        }
        char[] charArray = new char[contentLength];
        bufferedReader.read(charArray,0,contentLength);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.insert(0,charArray);

        lineForPost = stringBuilder.toString();
        log.info("form data : {}",lineForPost );
        UserDto.Result userDtoResult  = modelmapperForObject.parse(
                ParserType.CustomMapping,
                ModelParam
                        .builder()
                        .mapping(new UserMapping(lineForPost))
                        .build()
        );
        log.info("status code : {}",
                userDtoResult.getStatusCode());

        Resource resource = null;

        switch (userDtoResult.getStatusCode()){
            case SUCCESS:
                resource = ResourceFactory.getResource(ResourceType.VIEW_HTML,
                        ResourceParam.builder()
                                .screen("/index.html")
                                .build());
                break;
            case ERROR:
                resource = ResourceFactory.getResource(ResourceType.VIEW_HTML,
                        ResourceParam.builder()
                                .screen("/user/login_failed.html")
                                .build());
                break;
            case FAILED:
                resource = ResourceFactory.getResource(ResourceType.VIEW_HTML,
                        ResourceParam.builder()
                                .screen("/user/login_failed.html")
                                .build());
                break;
        }

        responseDTOBuilder =
                ResponseDTO.builder()
                        .body((byte[]) resource.call())
                        .responseCode(ResponseCode.HTTP_302);
        return responseDTOBuilder;
    }

    private boolean isPost(String line, String post){
        return line != null && line.indexOf(post) > -1;
    }

    private boolean isGet(String line) {
        return line != null && line.indexOf("GET") > -1 && line.indexOf(".html") > -1;
    }

    private boolean isIco(String line) {
        return line != null && line.indexOf(".ico") > -1;
    }


    private ResponseDTO.ResponseDTOBuilder getResponseDTOForGet(String line) throws Exception {
        ResponseDTO.ResponseDTOBuilder responseDTOBuilder;
        String screenName = modelmapperForString.parse(ParserType.ViewString,line);

        log.debug("screenName : {}",screenName);

        Resource resource = ResourceFactory.getResource(ResourceType.VIEW_HTML,
                ResourceParam.builder()
                        .screen(screenName)
                        .build());

        responseDTOBuilder = ResponseDTO.builder()
                .body((byte[]) resource.call())
                .responseCode(ResponseCode.HTTP_200);

        return responseDTOBuilder;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent,ResponseCode responseCode){
        try{
            dos.writeBytes("HTTP/1.1 "+responseCode+" OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        }catch (Exception exception){
            log.error(exception.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body){
        try{
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
        }
    }
}
