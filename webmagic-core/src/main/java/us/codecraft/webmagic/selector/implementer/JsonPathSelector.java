package us.codecraft.webmagic.selector.implementer;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;

import us.codecraft.webmagic.selector.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JsonPath selector.<br>
 * Used to extract content from JSON.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public class JsonPathSelector implements Selector {

    private String jsonPathStr;

    private JsonPath jsonPath;

    /**
     * Creates a Json path selector with a Json path string.
     * @param jsonPathStr the Json path string
     */
    public JsonPathSelector(String jsonPathStr) {
        this.jsonPathStr = jsonPathStr;
        this.jsonPath = JsonPath.compile(this.jsonPathStr);
    }

    @Override
    public String select(String text) {
        Object object = jsonPath.read(text);
        if (object == null) {
            return null;
        }
        if (object instanceof List) {
            List list = (List) object;
            if (list != null && list.size() > 0) {
                return toString(list.iterator().next());
            }
        }
        return object.toString();
    }

    @Override
    public List<String> selectList(String text) {
        List<String> list = new ArrayList<String>();
        Object object = jsonPath.read(text);
        if (object == null) {
            return list;
        }
        if (object instanceof List) {
            List<Object> items = (List<Object>) object;
            for (Object item : items) {
                list.add(toString(item));
            }
        } else {
            list.add(toString(object));
        }
        return list;
    }
    
    /**
     * Displays the string format of an object.
     * @param object the object
     * @return the string format of an object.
     */
    private String toString(Object object) {
        if (object instanceof Map) {
            return JSON.toJSONString(object);
        } else {
            return String.valueOf(object);
        }
    }
    
}
