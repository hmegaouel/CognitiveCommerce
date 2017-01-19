package analysis;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;


public class Word2vec {

    private static Logger log = LoggerFactory.getLogger(Word2vec.class);
    private static Word2Vec word2vec;
    private String ModelName;

    public void train(String trainingFile) throws FileNotFoundException {
        String filePath = new ClassPathResource(trainingFile).getFile().getAbsolutePath();
        log.info("Load & Vectorize Sentences....");
        SentenceIterator iter = new BasicLineIterator(filePath);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        InMemoryLookupCache cache = new InMemoryLookupCache();
        WeightLookupTable<VocabWord> table = new InMemoryLookupTable.Builder<VocabWord>()
                .vectorLength(100)
                .useAdaGrad(false)
                .cache(cache)
                .lr(0.025f).build();
        log.info("Building model....");
        word2vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .epochs(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .lookupTable(table)
                .vocabCache(cache)
                .build();
        log.info("Fitting Word2Vec model....");
        word2vec.fit();
        WordVectorSerializer.writeFullModel(word2vec,ModelName);
    }

    public Word2vec(String ModelName, String trainingFile) {
        this.ModelName = ModelName;
        try {
            word2vec = WordVectorSerializer.loadFullModel(ModelName);
        } catch (FileNotFoundException e) {
            log.info("Needing to train");
            try {
                this.train(trainingFile);
            } catch (FileNotFoundException f) {
                log.error(f.toString());
            }
        }
    }

    public static double getScore(String word1, String word2) {
        double score = 0;
        try {
            score = word2vec.similarity(word1, word2);
        } catch (NullPointerException e) {
            log.info("Word hasnt been trained with");
        }
        return score;
    }


    public static void main(String[] args) throws Exception {
        Word2vec tt = new Word2vec("pathToSaveModel.txt","raw_sentences.txt");
        System.out.println(tt.getScore("day","game"));
    }


}

