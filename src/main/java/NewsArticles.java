public class NewsArticles {
    public enum DataType {
        Training, Testing
    }

    private String newsTitle = "", newsContent = "";
    private DataType newsType = DataType.Testing;
    private String newsLabel = "-1";

    public NewsArticles(String _title, String _content, DataType _type, String _label) {
        newsTitle = _title;
        newsContent = _content;
        newsType = _type;
        newsLabel = _label;
    }

    public String getNewsLabel() {
        return newsLabel;
    }

    public DataType getNewsType() {
        return newsType;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsLabel(String _lable) {
        newsLabel = _lable;
    }

    public void setNewsType (DataType _type){
        newsType = _type;
    }
}
