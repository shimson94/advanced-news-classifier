public class HtmlParser {
    /***
     * Extract the title of the news from the _htmlCode.
     * @param _htmlCode Contains the full HTML string from a specific news. E.g. 01.htm.
     * @return Return the title if it's been found. Otherwise, return "Title not found!".
     */
    public static String getNewsTitle(String _htmlCode) {
        String titleTagOpen = "<title>";
        String titleTagClose = "</title>";

        int titleStart = _htmlCode.indexOf(titleTagOpen) + titleTagOpen.length();
        int titleEnd = _htmlCode.indexOf(titleTagClose);

        if (titleStart != -1 && titleEnd != -1 && titleEnd > titleStart) {
            String strFullTitle = _htmlCode.substring(titleStart, titleEnd);
            return strFullTitle.substring(0, strFullTitle.indexOf(" |"));
        }

        return "Title not found!";
    }

    /***
     * Extract the content of the news from the _htmlCode.
     * @param _htmlCode Contains the full HTML string from a specific news. E.g. 01.htm.
     * @return Return the content if it's been found. Otherwise, return "Content not found!".
     */
    public static String getNewsContent(String _htmlCode) {
        String contentTagOpen = "\"articleBody\": \"";
        String contentTagClose = " \",\"mainEntityOfPage\":";

        int contentStart = _htmlCode.indexOf(contentTagOpen) + contentTagOpen.length();
        int contentEnd = _htmlCode.indexOf(contentTagClose);

        if (contentStart != -1 && contentEnd != -1 && contentEnd > contentStart) {
            return _htmlCode.substring(contentStart, contentEnd).toLowerCase();
        }

        return "Content not found!";
    }

    public static NewsArticles.DataType getDataType(String _htmlCode) {
        String datatypeTagOpen = "<datatype>";
        String datatypeTagClose = "</datatype>";

        int datatypeStart = _htmlCode.indexOf(datatypeTagOpen) + datatypeTagOpen.length();
        int datatypeEnd = _htmlCode.indexOf(datatypeTagClose);

        if (datatypeStart != -1 && datatypeEnd != -1 && datatypeEnd > datatypeStart) {
            String strFullDatatype = _htmlCode.substring(datatypeStart, datatypeEnd);
            for (NewsArticles.DataType type : NewsArticles.DataType.values()) {
                if (type.name().equalsIgnoreCase(strFullDatatype)) {
                    return type;
                }
            }
        }
        return NewsArticles.DataType.Testing;
    }

    public static String getLabel (String _htmlCode) {
        String labelTagOpen = "<label>";
        String labelTagClose = "</label>";

        int labelStart = _htmlCode.indexOf(labelTagOpen) + labelTagOpen.length();
        int labelEnd = _htmlCode.indexOf(labelTagClose);

        if (labelStart != -1 && labelEnd != -1 && labelEnd > labelStart) {
            String strFullTitle = _htmlCode.substring(labelStart, labelEnd);
            return strFullTitle.trim();
        }
        return "-1";
    }
}
