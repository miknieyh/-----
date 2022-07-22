package bong.lines.basic.resourceviews.view;

import bong.lines.basic.handler.loginsucceshtml.LoginSuccessHtml;
import bong.lines.basic.resourceviews.Resource;
import bong.lines.basic.resourceviews.ResourceParam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HTMLView implements Resource {
    private final ResourceParam resourceParam;
    @Override
    public Object call() throws Exception {
        return LoginSuccessHtml
                .class
                .getResourceAsStream("/templates"+resourceParam.getScreen())
                .readAllBytes();
    }
}
