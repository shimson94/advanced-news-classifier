import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedNewsClassifierTest {
    private StopWatch stopWatch = new StopWatch();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Test
    void createGloveList() throws IOException {
        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();
        List<Glove> gloveList = classifier.createGloveList();

        assertEquals(38515, gloveList.size());
    }

    @Test
    void calculateEmbeddingSize() throws IOException {
        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();
        assertEquals(196, classifier.calculateEmbeddingSize(classifier.getArticleEmbeddings()));
    }

    @Test
    void populateEmbedding_Functional() throws Exception {
        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();
        classifier.embeddingSize = classifier.calculateEmbeddingSize(classifier.getArticleEmbeddings());
        classifier.populateEmbedding();
        boolean embeddingValid = true;
        for (ArticlesEmbedding embedding : classifier.getArticleEmbeddings()) {
            if (embedding.getEmbedding().isEmpty()) {
                embeddingValid = false;
                break;
            }
        }
        assertTrue(embeddingValid);
    }

    @Test
    void populateEmbedding_Performance() throws Exception {
        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();
        classifier.embeddingSize = classifier.calculateEmbeddingSize(classifier.getArticleEmbeddings());
        long totalTime = 0;
        for (int i = 0; i < 100; i++) {
            stopWatch.start();
            classifier.populateEmbedding();
            stopWatch.stop();
            totalTime += stopWatch.getTime();
            stopWatch.reset();
        }

        System.out.println("Average execution time: " + (totalTime / 100));
        assertTrue(totalTime / 100 < 100);
    }

    @Test
    void predictResult() throws Exception {
        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();

        classifier.embeddingSize = classifier.calculateEmbeddingSize(classifier.getArticleEmbeddings());
        classifier.populateEmbedding();
        classifier.setNeuralNetwork(classifier.buildNeuralNetwork(2));
        List<Integer> results = classifier.predictResult(classifier.getArticleEmbeddings());
        assertEquals("[1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0]", results.toString());
    }

    @Test
    void printResults() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();
        classifier.embeddingSize = classifier.calculateEmbeddingSize(classifier.getArticleEmbeddings());
        classifier.populateEmbedding();
        classifier.setNeuralNetwork(classifier.buildNeuralNetwork(2));
        classifier.predictResult(classifier.getArticleEmbeddings());
        classifier.printResults();

        String output = outContent.toString();
        assertEquals("Group 1\r\n" +
                "Boris Johnson asked if government 'believes in long COVID', coronavirus inquiry hears\r\n" +
                "COVID vaccine scientists win Nobel Prize in medicine\r\n" +
                "Long COVID risks are 'distorted by flawed research', study finds\r\n" +
                "Who is Sam Altman? The OpenAI boss and ChatGPT guru who became one of AI's biggest players\r\n" +
                "ChatGPT maker OpenAI agrees deal for ousted Sam Altman to return as chief executive\r\n" +
                "Sam Altman: Ousted OpenAI boss 'committed to ensuring firm still thrives' as majority of employees threaten to quit\r\n" +
                "Sam Altman: Sudden departure of ChatGPT guru raises major questions that should concern us all\r\n" +
                "ChatGPT creator Sam Altman lands Microsoft job after ousting by OpenAI board\r\n" +
                "Group 2\r\n" +
                "COVID inquiry: There could have been fewer coronavirus-related deaths with earlier lockdown, scientist says\r\n" +
                "Up to 200,000 people to be monitored for COVID this winter to track infection rates\r\n" +
                "Molnupiravir: COVID drug linked to virus mutations, scientists say\r\n" +
                "How the chaos at ChatGPT maker OpenAI has unfolded as ousted CEO Sam Altman returns - and why it matters\r\n", output);

        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}
