package us.codecraft.webmagic.selector.extender;

import org.apache.commons.collections.CollectionUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.selector.BaseElementSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * CSS selector. Based on Jsoup.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class CssSelector extends BaseElementSelector {

    private String selectorText;

    private String attrName;

    /**
     * Creates a css selector with a selector text.
     * @param selectorText the selector text
     */
    public CssSelector(String selectorText) {
        this.selectorText = selectorText;
    }

    /**
     * Creates a selector text with a selector text and an attribute name.
     * @param selectorText the selector text
     * @param attrName the attribute name
     */
    public CssSelector(String selectorText, String attrName) {
        this.selectorText = selectorText;
        this.attrName = attrName;
    }    

    @Override
    public String select(Element element) {
        List<Element> elements = selectElements(element);
        if (CollectionUtils.isEmpty(elements)) {
            return null;
        }
        return getValue(elements.get(0));
    }

    @Override
    public List<String> selectList(Element doc) {
        List<String> strings = new ArrayList<String>();
        List<Element> elements = selectElements(doc);
        if (CollectionUtils.isNotEmpty(elements)) {
            for (Element element : elements) {
                String value = getValue(element);
                if (value != null) {
                    strings.add(value);
                }
            }
        }
        return strings;
    }

    @Override
    public Element selectElement(Element element) {
        Elements elements = element.select(selectorText);
        if (CollectionUtils.isNotEmpty(elements)) {
            return elements.get(0);
        }
        return null;
    }

    @Override
    public List<Element> selectElements(Element element) {
        return element.select(selectorText);
    }

    @Override
    public boolean hasAttribute() {
        return attrName != null;
    }
    
    /**
     * Gets the text from an element.
     * @param element the element
     * @return the text got from the element.
     */
    protected String getText(Element element) {
        StringBuilder accum = new StringBuilder();
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                accum.append(textNode.text());
            }
        }
        return accum.toString();
    }
    
    /**
     * Gets the value of an element.
     * @param element the element
     * @return the value of the element.
     */
    private String getValue(Element element) {
        if (attrName == null) {
            return element.outerHtml();
        } else if ("innerHtml".equalsIgnoreCase(attrName)) {
            return element.html();
        } else if ("text".equalsIgnoreCase(attrName)) {
            return getText(element);
        } else if ("allText".equalsIgnoreCase(attrName)) {
            return element.text();
        } else {
            return element.attr(attrName);
        }
    }
    
}
