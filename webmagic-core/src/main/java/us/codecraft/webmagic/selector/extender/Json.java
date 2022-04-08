package us.codecraft.webmagic.selector.extender;

import com.alibaba.fastjson.JSON;

import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.implementer.JsonPathSelector;
import us.codecraft.xsoup.XTokenQueue;

import java.util.List;

/**
 * parse json
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public class Json extends PlainText {

	/**
	 * Creates a Json with a list of strings.
	 * @param strings the list of strings
	 */
    public Json(List<String> strings) {
        super(strings);
    }

    /**
     * Creates a Json with a text.
     * @param text the text
     */
    public Json(String text) {
        super(text);
    }

    /**
     * remove padding for JSONP
     * @param padding padding
     * @return json after padding removed
     */
    public Json removePadding(String padding) {
        String text = getFirstSourceText();
        XTokenQueue tokenQueue = new XTokenQueue(text);
        tokenQueue.consumeWhitespace();
        tokenQueue.consume(padding);
        tokenQueue.consumeWhitespace();
        String chompBalanced = tokenQueue.chompBalancedNotInQuotes('(', ')');
        return new Json(chompBalanced);
    }

    /**
     * Creates an object from the first source text.
     * @param <T> a container of T-type objects
     * @param clazz a class containing T-type objects
     * @return the parsed T-type object.
     */
    public <T> T toObject(Class<T> clazz) {
        if (getFirstSourceText() == null) {
            return null;
        }
        return JSON.parseObject(getFirstSourceText(), clazz);
    }

    /**
     * Creates a list from the first source text.
     * @param <T> a container of T-type objects
     * @param clazz a class containing T-type objects
     * @return the parsed list of T-type objects.
     */
    public <T> List<T> toList(Class<T> clazz) {
        if (getFirstSourceText() == null) {
            return null;
        }
        return JSON.parseArray(getFirstSourceText(), clazz);
    }

    @Override
    public Selectable jsonPath(String jsonPath) {
        JsonPathSelector jsonPathSelector = new JsonPathSelector(jsonPath);
        return selectList(jsonPathSelector,getSourceTexts());
    }
}
