import org.apache.commons.lang3.time.StopWatch;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedNewsClassifier {
    private Toolkit toolkit = null;
    private static List<NewsArticles> newsArticles = null;
    private static List<Glove> gloveEmbeddings = null;
    private List<ArticlesEmbedding> articleEmbeddings = null;
    private MultiLayerNetwork neuralNetwork = null;

    public final int BATCHSIZE = 10;

    public int embeddingSize = 0;
    private static StopWatch mySW = new StopWatch();

    public AdvancedNewsClassifier() throws IOException {
        toolkit = new Toolkit();
        toolkit.loadGlove();
        newsArticles = toolkit.loadNews();
        gloveEmbeddings = createGloveList();
        articleEmbeddings = loadData();
    }

    public static void main(String[] args) throws Exception {
        mySW.start();
        AdvancedNewsClassifier classifier = new AdvancedNewsClassifier();

        classifier.embeddingSize = classifier.calculateEmbeddingSize(classifier.articleEmbeddings);
        classifier.populateEmbedding();
        classifier.neuralNetwork = classifier.buildNeuralNetwork(2);
        classifier.predictResult(classifier.articleEmbeddings);
        classifier.printResults();
        mySW.stop();
        System.out.println("Total elapsed time: " + mySW.getTime());
    }

    public List<Glove> createGloveList() {
        List<Glove> listResult = new ArrayList<>();
        List<String> listVocabulary = Toolkit.getListVocabulary();
        List<double[]> listVectors = Toolkit.getlistVectors();
        for (int i = 0; i < listVocabulary.size(); i++){
            String word = listVocabulary.get(i);
            if (!ArticlesEmbedding.isStopWord(word,Toolkit.STOPWORDS)){
                double[] vectorArray = listVectors.get(i);
                Vector vector = new Vector(vectorArray);
                listResult.add(new Glove(word,vector));
            }
        }
        return listResult;
    }


    public static List<ArticlesEmbedding> loadData() {
        List<ArticlesEmbedding> listEmbedding = new ArrayList<>();
        for (NewsArticles news : newsArticles) {
            ArticlesEmbedding myAE = new ArticlesEmbedding(news.getNewsTitle(), news.getNewsContent(), news.getNewsType(), news.getNewsLabel());
            listEmbedding.add(myAE);
        }
        return listEmbedding;
    }

    public int calculateEmbeddingSize(List<ArticlesEmbedding> _listEmbedding) {
        int intMedian = -1;
        int[] docLengths = new int[_listEmbedding.size()];
        for (int i = 0; i < _listEmbedding.size(); i++){
            String[] words = _listEmbedding.get(i).getNewsContent().split("\\s+");
            int count = 0;
            for (String word:words){
                if (isWordInGloveList(word)){
                    count++;
                }
            }
            docLengths[i] = count;
        }
        int[] sortedList = docLengths.clone();
        Arrays.sort(sortedList);
        int n = sortedList.length;
        if (n % 2 == 0) {
            intMedian = (sortedList[n / 2] + sortedList[(n / 2) + 1]) / 2;
        }
        else {
            intMedian = sortedList[(n + 1) / 2];
        }
        return intMedian;
    }
    private static boolean isWordInGloveList(String word) {
        for (Glove glove : gloveEmbeddings) {
            if (glove.getVocabulary().equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }


    public void populateEmbedding() {
        for (int i = 0; i < articleEmbeddings.size(); i++){
            try{
                articleEmbeddings.get(i).getEmbedding();
            }
            catch (InvalidSizeException e){
                articleEmbeddings.get(i).setEmbeddingSize(embeddingSize);
                try {
                    articleEmbeddings.get(i).getEmbedding();
                } catch (Exception e1){
                    throw new RuntimeException(e1);
                }
            }
            catch (InvalidTextException e){
                articleEmbeddings.get(i).getNewsContent();
                try{
                    articleEmbeddings.get(i).getEmbedding();
                } catch (Exception e1){
                    throw new RuntimeException(e1);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public DataSetIterator populateRecordReaders(int _numberOfClasses) throws Exception {
        ListDataSetIterator myDataIterator = null;
        List<DataSet> listDS = new ArrayList<>();
        INDArray inputNDArray = null;
        INDArray outputNDArray = null;

        for (ArticlesEmbedding article : articleEmbeddings){
            if (article.getNewsType() == NewsArticles.DataType.Training){
                inputNDArray = article.getEmbedding();
                outputNDArray = Nd4j.zeros(1, _numberOfClasses);
                int index = Integer.parseInt(article.getNewsLabel()) - 1;
                outputNDArray.putScalar(0, index, 1);
                DataSet myDataSet = new DataSet(inputNDArray, outputNDArray);
                listDS.add(myDataSet);
            }
        }
        return new ListDataSetIterator(listDS, BATCHSIZE);
    }

    public MultiLayerNetwork buildNeuralNetwork(int _numOfClasses) throws Exception {
        DataSetIterator trainIter = populateRecordReaders(_numOfClasses);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(42)
                .trainingWorkspaceMode(WorkspaceMode.ENABLED)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(Adam.builder().learningRate(0.02).beta1(0.9).beta2(0.999).build())
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(embeddingSize).nOut(15)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.HINGE)
                        .activation(Activation.SOFTMAX)
                        .nIn(15).nOut(_numOfClasses).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        for (int n = 0; n < 100; n++) {
            model.fit(trainIter);
            trainIter.reset();
        }
        return model;
    }

    public List<Integer> predictResult(List<ArticlesEmbedding> _listEmbedding) throws Exception {
        List<Integer> listResult = new ArrayList<>();
        for (ArticlesEmbedding article : _listEmbedding){
            if (article.getNewsType() == NewsArticles.DataType.Testing){
                int[] result = neuralNetwork.predict(article.getEmbedding());
                String predictedLabel = String.valueOf(result[0]);
                article.setNewsLabel(predictedLabel);
                listResult.add(result[0]);
            }
        }
        return listResult;
    }

    public void printResults() {
        List<List<String>> groupedResults = new ArrayList<>();
        for (ArticlesEmbedding article : articleEmbeddings) {
            if (article.getNewsType() == NewsArticles.DataType.Testing) {
                String label = article.getNewsLabel();
                int incrementedLabel = Integer.parseInt(label) + 1;
                String title = article.getNewsTitle();
                addTitleToGroup(groupedResults, String.valueOf(incrementedLabel), title);
            }
        }
        for (int i = 0; i < groupedResults.size() - 1; i++) {
            for (int j = 0; j < groupedResults.size() - i - 1; j++) {
                int label1 = Integer.parseInt(groupedResults.get(j).get(0));
                int label2 = Integer.parseInt(groupedResults.get(j + 1).get(0));
                if (label1 > label2) {
                    List<String> temp = groupedResults.get(j);
                    groupedResults.set(j, groupedResults.get(j + 1));
                    groupedResults.set(j + 1, temp);
                }
            }
        }
        for (List<String> group : groupedResults){
            System.out.print("Group " + group.get(0) + "\r\n");
            for (int i = 1; i < group.size(); i++) {
                System.out.println(group.get(i));
            }
        }
    }

    private void addTitleToGroup(List<List<String>> groupedResults, String label, String title) {
        for (List<String> group : groupedResults) {
            if (group.get(0).equals(label)) {
                group.add(title);
                return;
            }
        }
        List<String> newGroup = new ArrayList<>();
        newGroup.add(label);
        newGroup.add(title);
        groupedResults.add(newGroup);
    }

    public static List<Glove> getGloveEmbeddings() {
        return gloveEmbeddings;
    }

    public List<ArticlesEmbedding> getArticleEmbeddings() {
        return articleEmbeddings;
    }

    public void setNeuralNetwork(MultiLayerNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }
}
