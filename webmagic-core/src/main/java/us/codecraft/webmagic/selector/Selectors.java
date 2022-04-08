package us.codecraft.webmagic.selector;

import us.codecraft.webmagic.selector.extender.CssSelector;
import us.codecraft.webmagic.selector.extender.XpathSelector;
import us.codecraft.webmagic.selector.implementer.AndSelector;
import us.codecraft.webmagic.selector.implementer.OrSelector;
import us.codecraft.webmagic.selector.implementer.RegexSelector;
import us.codecraft.webmagic.selector.implementer.SmartContentSelector;

/**
 * Convenient methods for selectors.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public abstract class Selectors {

	/**
	 * Creates a new regex selector with an expression.
	 * @param expr the parameter for regex selector's constructor
	 * @return a new regex selector.
	 */
    public static RegexSelector regex(String expr) {
        return new RegexSelector(expr);
    }

    /**
     * Creates a new regex selector with an expression and a group number.
     * @param expr the expression
     * @param group the group number
     * @return a new regex selector.
     */
    public static RegexSelector regex(String expr, int group) {
        return new RegexSelector(expr,group);
    }

    /**
     * Creates a smart content selector.
     * @return a smart content selector.
     */
    public static SmartContentSelector smartContent() {
        return new SmartContentSelector();
    }

    /**
     * Creates a new css selector with an expression.
     * @param expr the expression
     * @return a new css selector.
     */
    public static CssSelector $(String expr) {
        return new CssSelector(expr);
    }

    /**
     * Creates a new css selector with an expression and an attribute name.
     * @param expr the expression
     * @param attrName the attribute name
     * @return a new css selector.
     */
    public static CssSelector $(String expr, String attrName) {
        return new CssSelector(expr, attrName);
    }

    /**
     * Creates a new Xpath selector with an expression.
     * @param expr the expression
     * @return a new Xpath selector.
     */
    public static XpathSelector xpath(String expr) {
        return new XpathSelector(expr);
    }

    /**
     * @see #xpath(String)
     * @param expr expr
     * @return new selector
     */
    @Deprecated
    public static XpathSelector xsoup(String expr) {
        return new XpathSelector(expr);
    }

    /**
     * Creates a new And selector from a bunch of selectors.
     * @param selectors the bunch of selectors
     * @return a new And selector.
     */
    public static AndSelector and(Selector... selectors) {
        return new AndSelector(selectors);
    }

    /**
     * Creates a new Or selector from a bunch of selectors.
     * @param selectors the bunch of selectors
     * @return a new Or selector.
     */
    public static OrSelector or(Selector... selectors) {
        return new OrSelector(selectors);
    }

}