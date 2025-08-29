import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

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
    
    @Test
    void analyzeTrainingLabelsToUnderstandClassification() throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TRAINING DATA ANALYSIS: DISCOVERING GROUND TRUTH CLASSIFICATION");
        System.out.println("=".repeat(80));
        
        URL newsUrl = HtmlParserTest.class.getClassLoader().getResource("News");
        if (newsUrl == null) {
            throw new RuntimeException("News directory not found");
        }
        
        File newsFolder = new File(newsUrl.toURI());
        File[] allFiles = newsFolder.listFiles();
        
        if (allFiles != null) {
            Arrays.sort(allFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));
            
            System.out.println("TRAINING FILES WITH THEIR LABELS:");
            System.out.println("=".repeat(80));
            
            for (File file : allFiles) {
                if (file.isFile() && file.getName().endsWith(".htm")) {
                    String htmlContent = new String(Files.readAllBytes(file.toPath()));
                    
                    // Show training files with their labels
                    if (htmlContent.contains("<datatype>Training</datatype>")) {
                        String title = HtmlParser.getNewsTitle(htmlContent);
                        String label = HtmlParser.getLabel(htmlContent);
                        
                        System.out.printf("File: %-8s | Label: %s%n", file.getName(), label);
                        System.out.printf("Title: %s%n", title);
                        System.out.println("-".repeat(60));
                    }
                }
            }
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("TEST FILES (NO LABELS - THESE NEED PREDICTION):");
            System.out.println("=".repeat(80));
            
            for (File file : allFiles) {
                if (file.isFile() && file.getName().endsWith(".htm")) {
                    String htmlContent = new String(Files.readAllBytes(file.toPath()));
                    
                    // Show test files without labels
                    if (!htmlContent.contains("<datatype>") && !htmlContent.contains("<label>")) {
                        String title = HtmlParser.getNewsTitle(htmlContent);
                        
                        System.out.printf("File: %-8s | Label: UNKNOWN (need to predict)%n", file.getName());
                        System.out.printf("Title: %s%n", title);
                        System.out.println("-".repeat(60));
                    }
                }
            }
        }
    }
}
