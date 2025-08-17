import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewsArticlesTest {
    @Test
    void newsTest() {
        NewsArticles articles = new NewsArticles("Hi", "Hello world!", NewsArticles.DataType.Training, "-1");
        assertEquals("Hi", articles.getNewsTitle());
        assertEquals("Hello world!", articles.getNewsContent());
        assertEquals(NewsArticles.DataType.Training, articles.getNewsType());
        assertEquals("-1", articles.getNewsLabel());
    }
}
