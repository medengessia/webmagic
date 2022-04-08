package us.codecraft.webmagic.selector.implementer;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.selector.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Selector in regex.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class RegexSelector implements Selector {

    private String regexStr;

    private Pattern regex;

    private int group = 1;

    /**
     * Creates a RegexSelector with a regular expression and a capture group.
     * @param regexStr the regular expression
     * @param group the group number
     */
    public RegexSelector(String regexStr, int group) {
        this.compileRegex(regexStr);
        this.group = group;
    }    

    /**
     * Creates a RegexSelector. When there is no capture group, the value is set to 0 else set to 1.
     * @param regexStr the regular expression.
     */
    public RegexSelector(String regexStr) {
        this.compileRegex(regexStr);
        if (regex.matcher("").groupCount() == 0) {
            this.group = 0;
        } else {
            this.group = 1;
        }
    }

    @Override
    public String select(String text) {
        return selectGroup(text).get(group);
    }

    @Override
    public List<String> selectList(String text) {
        List<String> strings = new ArrayList<String>();
        List<RegexResult> results = selectGroupList(text);
        for (RegexResult result : results) {
            strings.add(result.get(group));
        }
        return strings;
    }
    
    @Override
    public String toString() {
        return regexStr;
    }

    /**
     * Generates a regex result when the text is found by the matcher.
     * @param text the text
     * @return the regular expression result
     */
    public RegexResult selectGroup(String text) {
        Matcher matcher = regex.matcher(text);
        if (matcher.find()) {
            String[] groups = new String[matcher.groupCount() + 1];
            for (int i = 0; i < groups.length; i++) {
                groups[i] = matcher.group(i);
            }
            return new RegexResult(groups);
        }
        return RegexResult.EMPTY_RESULT;
    }

    /**
     * Generates a list of regex results while the text is found by the matcher.
     * @param text the text
     * @return the list of regular expression results.
     */
    public List<RegexResult> selectGroupList(String text) {
        Matcher matcher = regex.matcher(text);
        List<RegexResult> resultList = new ArrayList<RegexResult>();
        while (matcher.find()) {
            String[] groups = new String[matcher.groupCount() + 1];
            for (int i = 0; i < groups.length; i++) {
                groups[i] = matcher.group(i);
            }
            resultList.add(new RegexResult(groups));
        }
        return resultList;
    }
    
    /**
     * Compiles the regular expression given as parameter.
     * @param regexStr the regular expression
     */
    private void compileRegex(String regexStr) {
        if (StringUtils.isBlank(regexStr)) {
            throw new IllegalArgumentException("regex must not be empty");
        }
        try {
            this.regex = Pattern.compile(regexStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            this.regexStr = regexStr;
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("invalid regex "+regexStr, e);
        }
    }

}
