import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Toolkit {
    public static List<String> listVocabulary = null;
    public static List<double[]> listVectors = null;
    private static final String FILENAME_GLOVE = "glove.6B.50d_Reduced.csv";
    public static final String[] STOPWORDS = {"a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"};

    public void loadGlove() throws IOException {
        listVocabulary = new ArrayList<>(1000);
        listVectors = new ArrayList<>(1000);
        try(BufferedReader myReader = new BufferedReader(new FileReader(Toolkit.getFileFromResource(FILENAME_GLOVE)))){
            String data;
            while ((data = myReader.readLine()) != null){
                String[] dataArray = data.split(",");;
                listVocabulary.add(dataArray[0]);
                double[] tempArray = new double[dataArray.length - 1];
                for(int i = 0; i < dataArray.length - 1; i++){
                    tempArray[i] = Double.parseDouble(dataArray[i+1]);
                }
                listVectors.add(tempArray);
            }
        } catch(Exception e){
            throw new IOException("Failed to load GloVe file: " + e.getMessage(), e);
        }
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = Toolkit.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException(fileName);
        } else {
            return new File(resource.toURI()); // Production-ready resource loading
        }
    }

    public List<NewsArticles> loadNews() {
        List<NewsArticles> listNews = new ArrayList<>();
        try {
            URL newsUrl = Toolkit.class.getClassLoader().getResource("News");
            if (newsUrl == null) {
                throw new RuntimeException("News directory not found in resources");
            }
            File newsFolder = new File(newsUrl.toURI());
            File[] arrayOfFiles = newsFolder.listFiles();
        if (arrayOfFiles != null){
            // O(n log n) file sorting - consistent with performance optimization approach
            Arrays.sort(arrayOfFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));
            List <File> listOfFiles = new ArrayList<>();
            for (File file: arrayOfFiles){
                // Process HTML files (.htm, .html) and skip system/hidden files
                if (file.isFile() && 
                    !file.getName().startsWith(".") && 
                    (file.getName().toLowerCase().endsWith(".htm") || 
                     file.getName().toLowerCase().endsWith(".html"))) {
                    listOfFiles.add(file);
                }
            }
            for (File file : listOfFiles){
                String htmlContent;
                try {
                    htmlContent = new String(Files.readAllBytes(Paths.get(file.getPath())));
                } catch (IOException e){
                    System.err.println("Error finding file");
                    continue;
                }
                String title = HtmlParser.getNewsTitle(htmlContent);
                String content = HtmlParser.getNewsContent(htmlContent);
                NewsArticles.DataType type = HtmlParser.getDataType(htmlContent);
                String label = HtmlParser.getLabel(htmlContent);
                NewsArticles newsArticle = new NewsArticles(title,content,type,label);
                listNews.add(newsArticle);
            }
        }
        } catch (Exception e) {
            throw new RuntimeException("Error loading news files: " + e.getMessage(), e);
        }
        return listNews;
    }

    public static List<String> getListVocabulary() {
        return listVocabulary;
    }

    public static List<double[]> getlistVectors() {
        return listVectors;
    }
}
