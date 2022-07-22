package bong.lines.basic.resourceviews.factory;

import bong.lines.basic.resourceviews.Resource;
import bong.lines.basic.resourceviews.ResourceParam;
import bong.lines.basic.resourceviews.code.ResourceType;
import bong.lines.basic.resourceviews.view.DataView;
import bong.lines.basic.resourceviews.view.HTMLView;

public class ResourceFactory {
    public static Resource getResource(ResourceType resourceType, ResourceParam resourceParam) throws Exception {
        switch (resourceType){
            case DATA:
                return new DataView(resourceParam);
            case VIEW_HTML:
                return new HTMLView(resourceParam);
            default:
                throw new Exception();
        }
    }
}
