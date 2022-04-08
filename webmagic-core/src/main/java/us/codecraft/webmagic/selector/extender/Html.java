package us.codecraft.webmagic.selector.extender;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.selector.*;

import java.util.Collections;
import java.util.List;

/**
 * Selectable html.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class Html extends HtmlNode {

    private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Disable jsoup html entity escape. It can be set just before any Html instance is created.
     * @deprecated
	 */
	public static boolean DISABLE_HTML_ENTITY_ESCAPE = false;

    /**
     * Store parsed document for better performance when only one text exist.
     */
    private Document document;

    /**
     * Creates a html with a text and an url.
     * @param text the text
     * @param url the url
     */
    public Html(String text, String url) {
        try {
            this.document = Jsoup.parse(text, url);
        } catch (Exception e) {
            this.document = null;
            logger.warn("parse document error ", e);
        }
    }

    /**
     * Creates a html with a text.
     * @param text the text
     */
    public Html(String text) {
        try {
            this.document = Jsoup.parse(text);
        } catch (Exception e) {
            this.document = null;
            logger.warn("parse document error ", e);
        }
    }

    /**
     * Creates a html with a document.
     * @param document
     */
    public Html(Document document) {
        this.document = document;
    }

    /**
     * Gets the document of the html.
     * @return the document of the html.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param selector selector
     * @return result
     */
    public String selectDocument(Selector selector) {
        if (selector instanceof ElementSelector) {
            ElementSelector elementSelector = (ElementSelector) selector;
            return elementSelector.select(getDocument());
        } else {
            return selector.select(getFirstSourceText());
        }
    }

    /**
     * Selects a list of strings with a selector.
     * @param selector the selector
     * @return the list selected.
     */
    public List<String> selectDocumentForList(Selector selector) {
        if (selector instanceof ElementSelector) {
            ElementSelector elementSelector = (ElementSelector) selector;
            return elementSelector.selectList(getDocument());
        } else {
            return selector.selectList(getFirstSourceText());
        }
    }

    /**
     * Creates a html with a text.
     * @param text the text
     * @return a new html.
     */
    public static Html create(String text) {
        return new Html(text);
    }
    
    @Override
    protected List<Element> getElements() {
        return Collections.<Element>singletonList(getDocument());
    }

}
