import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkSummaryTest {
    
    @Test
    void generateComprehensiveBenchmarks() throws Exception {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           ADVANCED NEWS CLASSIFIER BENCHMARKS");
        System.out.println("=".repeat(60));
        
        StopWatch stopWatch = new StopWatch();
        
        // 1. System Initialization Benchmark
        System.out.println("\n📊 SYSTEM INITIALIZATION");
        stopWatch.start();
        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();
        stopWatch.stop();
        long initTime = stopWatch.getTime();
        System.out.printf("Full System Initialization: %dms%n", initTime);
        stopWatch.reset();
        
        // 2. Core Performance Metrics
        System.out.println("\n⚡ CORE PERFORMANCE METRICS");
        
        // GloVe metrics
        int vocabularySize = Toolkit.getListVocabulary().size();
        System.out.printf("GloVe Vocabulary Size: %,d terms%n", vocabularySize);
        
        // Dataset metrics
        int totalArticles = classifier.getArticleEmbeddings().size();
        int trainingArticles = 0;
        int testingArticles = 0;
        for (ArticlesEmbedding article : classifier.getArticleEmbeddings()) {
            if (article.getNewsType() == NewsArticles.DataType.Training) {
                trainingArticles++;
            } else {
                testingArticles++;
            }
        }
        System.out.printf("Dataset: %d total articles (%d training, %d testing)%n", 
                         totalArticles, trainingArticles, testingArticles);
        
        // 3. Processing Pipeline Benchmark
        System.out.println("\n🔄 PROCESSING PIPELINE");
        
        // Embedding size calculation
        stopWatch.start();
        int embeddingSize = classifier.calculateEmbeddingSize(classifier.getArticleEmbeddings());
        stopWatch.stop();
        long embeddingSizeCalcTime = stopWatch.getTime();
        System.out.printf("Embedding Size Calculation: %dms (result: %d dimensions)%n", 
                         embeddingSizeCalcTime, embeddingSize);
        stopWatch.reset();
        
        // Embedding population
        classifier.embeddingSize = embeddingSize;
        stopWatch.start();
        classifier.populateEmbedding();
        stopWatch.stop();
        long populationTime = stopWatch.getTime();
        System.out.printf("Document Embedding Population: %dms (%d documents)%n", 
                         populationTime, totalArticles);
        stopWatch.reset();
        
        // 4. Neural Network Training
        System.out.println("\n🧠 NEURAL NETWORK TRAINING");
        stopWatch.start();
        classifier.setNeuralNetwork(classifier.buildNeuralNetwork(2));
        stopWatch.stop();
        long trainingTime = stopWatch.getTime();
        System.out.printf("Neural Network Training: %,dms (100 epochs)%n", trainingTime);
        stopWatch.reset();
        
        // 5. Prediction Performance
        System.out.println("\n🎯 PREDICTION PERFORMANCE");
        stopWatch.start();
        List<Integer> predictions = classifier.predictResult(classifier.getArticleEmbeddings());
        stopWatch.stop();
        long predictionTime = stopWatch.getTime();
        double avgPredictionTime = (double) predictionTime / testingArticles;
        System.out.printf("Batch Prediction: %dms total (%.1fms per article)%n", 
                         predictionTime, avgPredictionTime);
        
        // 6. Memory and Efficiency Metrics
        System.out.println("\n💾 MEMORY & EFFICIENCY");
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        System.out.printf("Memory Usage: %.1f MB used / %.1f MB total%n", 
                         usedMemory / 1024.0 / 1024.0, totalMemory / 1024.0 / 1024.0);
        
        // 7. Final Summary for CV/README
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           📋 SUMMARY FOR PORTFOLIO");
        System.out.println("=".repeat(60));
        System.out.printf("✅ Processed %,d vocabulary terms in %dms%n", vocabularySize, initTime);
        System.out.printf("✅ Trained neural network on %d articles in %,dms%n", trainingArticles, trainingTime);
        System.out.printf("✅ Average prediction time: %.1fms per article%n", avgPredictionTime);
        System.out.printf("✅ Memory efficient: %.1f MB for full pipeline%n", usedMemory / 1024.0 / 1024.0);
        System.out.printf("✅ End-to-end processing: %,dms total pipeline%n", 
                         initTime + embeddingSizeCalcTime + populationTime + trainingTime + predictionTime);
        System.out.println("=".repeat(60));
        
        // Assertions for CI/CD
        assertTrue(initTime < 5000, "System initialization should complete within 5 seconds");
        assertTrue(avgPredictionTime < 100, "Average prediction should be under 100ms");
        assertTrue(usedMemory < 1000 * 1024 * 1024, "Memory usage should be under 1GB");
    }
}