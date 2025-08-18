import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class ArticlesEmbeddingTest {
    private ArticlesEmbedding embedding = new ArticlesEmbedding("What is COVID-19?", "Coronavirus disease 2019 (COVID-19) is a contagious disease caused by the virus SARS-CoV-2. The first known case was identified in Wuhan, China, in December 2019.[6] The disease quickly spread worldwide, resulting in the COVID-19 pandemic.\n" +
            "\n" +
            "The symptoms of COVID‑19 are variable but often include fever,[7] cough, headache,[8] fatigue, breathing difficulties, loss of smell, and loss of taste.[9][10][11] Symptoms may begin one to fourteen days after exposure to the virus. At least a third of people who are infected do not develop noticeable symptoms.[12][13] Of those who develop symptoms noticeable enough to be classified as patients, most (81%) develop mild to moderate symptoms (up to mild pneumonia), while 14% develop severe symptoms (dyspnea, hypoxia, or more than 50% lung involvement on imaging), and 5% develop critical symptoms (respiratory failure, shock, or multiorgan dysfunction).[14] Older people are at a higher risk of developing severe symptoms. Some people continue to experience a range of effects (long COVID) for years after infection, and damage to organs has been observed.[15] Multi-year studies are underway to further investigate the long-term effects of the disease.[16]\n" +
            "\n" +
            "COVID‑19 transmits when infectious particles are breathed in or come into contact with the eyes, nose, or mouth. The risk is highest when people are in close proximity, but small airborne particles containing the virus can remain suspended in the air and travel over longer distances, particularly indoors. Transmission can also occur when people touch their eyes, nose or mouth after touching surfaces or objects that have been contaminated by the virus. People remain contagious for up to 20 days and can spread the virus even if they do not develop symptoms.[17]\n" +
            "\n" +
            "Testing methods for COVID-19 to detect the virus's nucleic acid include real-time reverse transcription polymerase chain reaction (RT‑PCR),[18][19] transcription-mediated amplification,[18][19][20] and reverse transcription loop-mediated isothermal amplification (RT‑LAMP)[18][19] from a nasopharyngeal swab.[21]\n" +
            "\n" +
            "Several COVID-19 vaccines have been approved and distributed in various countries, which have initiated mass vaccination campaigns. Other preventive measures include physical or social distancing, quarantining, ventilation of indoor spaces, use of face masks or coverings in public, covering coughs and sneezes, hand washing, and keeping unwashed hands away from the face. While work is underway to develop drugs that inhibit the virus, the primary treatment is symptomatic. Management involves the treatment of symptoms through supportive care, isolation, and experimental measures. ", NewsArticles.DataType.Training, "1");
    private StopWatch stopWatch = new StopWatch();

    @Test
    void ArticlesEmbeddingConstructor() {
        assertEquals("What is COVID-19?", embedding.getNewsTitle());
        assertEquals(NewsArticles.DataType.Training, embedding.getNewsType());
    }

    @Test
    void setEmbeddingSize() {
        embedding.setEmbeddingSize(10);
        assertEquals(10, embedding.getEmbeddingSize());
    }

    @Test
    void getNewsContent_Functional() {
        assertEquals("coronavirus disease 2019 covid19 contagious disease cause virus sarscov2 first know case identify wuhan china december 20196 disease quickly spread worldwide result covid19 pandemic symptom covid19 variable include fever7 cough headache8 fatigue breathing difficulty loss smell loss taste91011 symptom begin one fourteen day exposure virus third person infect develop noticeable symptoms1213 develop symptom noticeable enough classify patient 81 develop mild moderate symptom up mild pneumonia 14 develop severe symptom dyspnea hypoxia more 50 lung involvement imaging 5 develop critical symptom respiratory failure shock multiorgan dysfunction14 old person high risk develop severe symptom person continue experience range effect long covid year infection damage organ observed15 multiyear study underway further investigate longterm effect disease16 covid19 transmit infectious particle breathe come contact eye nose mouth risk high person close proximity small airborne particle contain virus remain suspend air travel over long distance particularly indoors transmission occur person touch eye nose mouth touch surface object contaminate virus person remain contagious up 20 day spread virus even develop symptoms17 testing method covid19 detect viruss nucleic acid include realtime reverse transcription polymerase chain reaction rtpcr1819 transcriptionmediate amplification181920 reverse transcription loopmediate isothermal amplification rtlamp1819 nasopharyngeal swab21 several covid19 vaccine approve distribute various country initiate mass vaccination campaign preventive measure include physical social distancing quarantine ventilation indoor space use face mask covering public covering cough sneeze hand washing keep unwashed hand away face work underway develop drug inhibit virus primary treatment symptomatic management involve treatment symptom through supportive care isolation experimental measure", embedding.getNewsContent());
    }

    @Test
    void getNewsContent_Performance() {
        long totalTime = 0;
        for (int i = 0; i < 100; i++) {
            stopWatch.start();
            embedding.getNewsContent();
            stopWatch.stop();
            totalTime += stopWatch.getNanoTime();
            stopWatch.reset();
        }
        long avgTimeNanos = totalTime / 100;
        double avgTimeMs = avgTimeNanos / 1_000_000.0;
        System.out.printf("[BENCHMARK] Text Processing: %.2fms average per document%n", avgTimeMs);
        assertTrue(avgTimeNanos < 13000000);
    }

    @Test
    void getEmbedding_Functional() throws Exception {
        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();
        assertThrows(InvalidSizeException.class, () -> {
            embedding.getEmbedding();
        });
        embedding.setEmbeddingSize(100);
        assertThrows(InvalidTextException.class, () -> {
            embedding.getEmbedding();
        });
        embedding.getNewsContent();
        
        assertNotNull(embedding.getEmbedding(), "Embedding should not be null");
        
        long[] shape = embedding.getEmbedding().shape();
        assertEquals(2, shape.length, "Embedding should be 2D array");
        
        assertTrue(shape[0] > 0 && shape[1] > 0, "Both dimensions should be positive");
        assertTrue((shape[0] == 1 && shape[1] <= 500) || (shape[1] == 1 && shape[0] <= 500), 
                  "Should have reasonable document embedding dimensions, got: [" + shape[0] + ", " + shape[1] + "]");
        
        double[] flatEmbedding = embedding.getEmbedding().toDoubleVector();
        assertTrue(flatEmbedding.length > 0, "Embedding should contain values");
        
        boolean hasNonZeroValues = false;
        boolean hasReasonableValues = true;
        for (double value : flatEmbedding) {
            if (value != 0.0) hasNonZeroValues = true;
            if (Double.isInfinite(value) || Double.isNaN(value)) hasReasonableValues = false;
        }
        
        assertTrue(hasNonZeroValues, "Embedding should contain non-zero values");
        assertTrue(hasReasonableValues, "Embedding should contain finite values");
    }

    @Test
    void getEmbedding_Performance() throws Exception {
        long totalTime = 0;
        for (int i = 0; i < 50; i++) {
            AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();
            embedding.setEmbeddingSize(100);
            embedding.getNewsContent();
            stopWatch.start();
            embedding.getEmbedding();
            stopWatch.stop();
            totalTime += stopWatch.getTime();
            stopWatch.reset();
        }
        long avgTime = totalTime / 50;
        System.out.printf("[BENCHMARK] Embedding Generation: %dms average per document%n", avgTime);
        assertTrue(avgTime < 8);
    }
}
