package us.codecraft.webmagic.selector.implementer;

/**
 * Object contains regex results.<br>
 * For multi group result extension.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
class RegexResult {

    private String[] groups;

    public static final RegexResult EMPTY_RESULT = new RegexResult();

    public RegexResult() {

    }

    /**
     * Creates a regex result with a table of groups.
     * @param groups the table of groups
     */
    public RegexResult(String[] groups) {
        this.groups = groups;
    }

    /**
     * Gets a group in the table.
     * @param groupId the index of the group
     * @return a group in the table.
     */
    public String get(int groupId) {
        if (groups == null) {
            return null;
        }
        return groups[groupId];
    }

}
