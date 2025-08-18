import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class StanfordCoreNLPBenchmarkTest {
    
    @Test
    void benchmarkStanfordCoreNLPProcessing() throws IOException {
        System.out.println("\n🔧 STANFORD CORENLP PROCESSING BENCHMARK");
        System.out.println("=".repeat(50));
        
        // Test with just a few articles to see dramatic difference
        String[] testContent = {
            "This is the first test article about artificial intelligence and machine learning developments.",
            "Second article discusses climate change impacts on global weather patterns and ecosystems.",
            "Third article covers financial markets and cryptocurrency trends in modern economics."
        };
        
        StopWatch stopWatch = new StopWatch();
        
        System.out.printf("Testing with %d sample articles%n", testContent.length);
        System.out.println("Processing each article 10 times...");
        
        // Benchmark fresh text processing
        stopWatch.start();
        
        int rounds = 10;
        for (int round = 0; round < rounds; round++) {
            System.out.printf("Round %d: ", round + 1);
            for (int i = 0; i < testContent.length; i++) {
                // Create fresh ArticlesEmbedding each time
                ArticlesEmbedding freshArticle = new ArticlesEmbedding(
                    "Test Title " + i, 
                    testContent[i], // Fresh content each time
                    NewsArticles.DataType.Training, 
                    "1"
                );
                
                StopWatch singleWatch = new StopWatch();
                singleWatch.start();
                String processedContent = freshArticle.getNewsContent();
                singleWatch.stop();
                
                System.out.printf("%.0fms ", (double)singleWatch.getTime());
            }
            System.out.println();
        }
        
        stopWatch.stop();
        long totalTime = stopWatch.getTime();
        int totalProcessingCalls = testContent.length * rounds;
        
        System.out.println("\n" + "=".repeat(50));
        System.out.printf("Total processing calls: %d%n", totalProcessingCalls);
        System.out.printf("Total time: %dms%n", totalTime);
        System.out.printf("Average per call: %.1fms%n", (double) totalTime / totalProcessingCalls);
        
        System.out.println("\nKey insight:");
        System.out.println("- WITHOUT static pipeline: Creates " + totalProcessingCalls + " Stanford CoreNLP instances");
        System.out.println("- WITH static pipeline: Creates only 1 Stanford CoreNLP instance");
    }
}