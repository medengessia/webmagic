package us.codecraft.webmagic.selector;

import org.apache.commons.collections.CollectionUtils;

import us.codecraft.webmagic.selector.implementer.RegexSelector;
import us.codecraft.webmagic.selector.implementer.ReplaceSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.2
 */
public abstract class AbstractSelectable implements Selectable {

    protected abstract List<String> getSourceTexts();

    @Override
    public Selectable css(String selector) {
        return $(selector);
    }

    @Override
    public Selectable css(String selector, String attrName) {
        return $(selector, attrName);
    }

    @Override
    public List<String> all() {
        return getSourceTexts();
    }

    @Override
    public Selectable jsonPath(String jsonPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get() {
        if (CollectionUtils.isNotEmpty(all())) {
            return all().get(0);
        } else {
            return null;
        }
    }

    @Override
    public Selectable select(Selector selector) {
        return select(selector, getSourceTexts());
    }

    @Override
    public Selectable selectList(Selector selector) {
        return selectList(selector, getSourceTexts());
    }

    @Override
    public Selectable regex(String regex) {
        RegexSelector regexSelector = Selectors.regex(regex);
        return selectList(regexSelector, getSourceTexts());
    }

    @Override
    public Selectable regex(String regex, int group) {
        RegexSelector regexSelector = Selectors.regex(regex, group);
        return selectList(regexSelector, getSourceTexts());
    }

    @Override
    public Selectable replace(String regex, String replacement) {
        ReplaceSelector replaceSelector = new ReplaceSelector(regex,replacement);
        return select(replaceSelector, getSourceTexts());
    }

    /**
     * Gets the first source text.
     * @return the first source text.
     */
    public String getFirstSourceText() {
        if (getSourceTexts() != null && getSourceTexts().size() > 0) {
            return getSourceTexts().get(0);
        }
        return null;
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public boolean match() {
        return getSourceTexts() != null && getSourceTexts().size() > 0;
    }
    
    /**
     * Selects some strings among a list to return them as a plain text.
     * @param selector the selector of strings
     * @param strings the list of strings
     * @return the plain text containing the selected strings.
     */
    protected Selectable select(Selector selector, List<String> strings) {
        List<String> results = new ArrayList<String>();
        for (String string : strings) {
            String result = selector.select(string);
            if (result != null) {
                results.add(result);
            }
        }
        return new PlainText(results);
    }

    /**
     * Selects some lists of strings among a list to return them as a plain text.
     * @param selector the selector of strings
     * @param strings the list of strings
     * @return the plain text containing the selected lists of strings.
     */
    protected Selectable selectList(Selector selector, List<String> strings) {
        List<String> results = new ArrayList<String>();
        for (String string : strings) {
            List<String> result = selector.selectList(string);
            results.addAll(result);
        }
        return new PlainText(results);
    }
    
}
