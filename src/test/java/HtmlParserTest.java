import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlParserTest {
    private String htmlTraining = "<datatype>Training</datatype><label>2</label>";
    private String htmlTesting = "<datatype>Testing</datatype><label>1</label>";

    @Test
    void getDataType() {
        assertEquals(NewsArticles.DataType.Training, HtmlParser.getDataType(htmlTraining));
        assertEquals(NewsArticles.DataType.Testing, HtmlParser.getDataType(htmlTesting));
    }

    @Test
    void getLabel() {
        assertEquals("2", HtmlParser.getLabel(htmlTraining));
        assertEquals("1", HtmlParser.getLabel(htmlTesting));
    }
}
