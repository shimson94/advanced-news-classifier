import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.Properties;


public class ArticlesEmbedding extends NewsArticles {
    private int intSize = -1;
    private String processedText = "";

    private INDArray newsEmbedding = Nd4j.create(0);

    public ArticlesEmbedding(String _title, String _content, NewsArticles.DataType _type, String _label) {
        super(_title,_content,_type,_label);
    }

    public void setEmbeddingSize(int _size) {
        intSize = _size;
    }

    public int getEmbeddingSize(){
        return intSize;
    }

    @Override
    public String getNewsContent() {
        if (processedText.isEmpty()){
            String content = super.getNewsContent();
            String[] stopwords = Toolkit.STOPWORDS;
            processedText = processingText(textCleaning(content), stopwords).toLowerCase();
            return processedText.trim();
        }
        return processedText;
    }
    public String processingText(String text, String[] stopWords){
        StringBuilder mySB = new StringBuilder();
        Properties properties = new Properties();
        properties.setProperty("annotators","tokenize,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        CoreDocument document = pipeline.processToCoreDocument(text);

        for (CoreLabel token:document.tokens()){
            if(!isStopWord(token.lemma(),stopWords)){
                mySB.append(token.lemma()).append(" ");
            }
        }
        return mySB.toString().trim();
    }
    public static boolean isStopWord(String word, String[] stopWords) {
        for (String stopWord : stopWords) {
            if (word.equalsIgnoreCase(stopWord)) {
                return true;
            }
        }
        return false;
    }

    public INDArray getEmbedding() throws Exception {
        if (intSize == -1) {
            throw new InvalidSizeException("Invalid Size");
        }
        else if (processedText.isEmpty()) {
            throw new InvalidTextException("Invalid Text");
        }
        else if (newsEmbedding.isEmpty()){
                try{
                    intSize = getEmbeddingSize();
                    processedText = getNewsContent();
                    String[] words = processedText.split("\\s+");
                    int vectorLength = AdvancedNewsClassifier.getGloveEmbeddings().get(0).getVector().getVectorSize();
                    newsEmbedding = Nd4j.create(intSize, vectorLength);

                    int rowCount = 0;
                    for (int i = 0; i < words.length && rowCount < intSize; i++) {
                        Glove glove = findGloveWord(words[i]);
                        if (glove != null) {
                            double[] vector = glove.getVector().getAllElements();
                            newsEmbedding.putRow(rowCount++, Nd4j.create(vector));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        return Nd4j.vstack(newsEmbedding.mean(1));

    }
    private Glove findGloveWord(String word) {
        for (Glove glove : AdvancedNewsClassifier.getGloveEmbeddings()) {
            if (glove.getVocabulary().equalsIgnoreCase(word)) {
                return glove;
            }
        }
        return null;
    }

    /***
     * Clean the given (_content) text by removing all the characters that are not 'a'-'z', '0'-'9' and white space.
     * @param _content Text that need to be cleaned.
     * @return The cleaned text.
     */
    private static String textCleaning(String _content) {
        StringBuilder sbContent = new StringBuilder();

        for (char c : _content.toLowerCase().toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || Character.isWhitespace(c)) {
                sbContent.append(c);
            }
        }
        return sbContent.toString().trim();
    }
}