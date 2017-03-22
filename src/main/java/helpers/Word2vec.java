package helpers;

import helpers.vec.Word2VecModel;
import helpers.vec.Word2VecTrainer;
import helpers.vec.Word2VecTrainer.Word2VecTrainerBuilder;
import helpers.vec.WordWithSimilarity;
import helpers.vec.utils.SerializationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by hannamegaouel on 11/03/2017.
 */
public class Word2vec {

    private Word2VecModel model;
    private String french_train_path = "entrainement_fr_folded.txt";
    private String english_train_path = "entrainement_en_folded.txt";
    private String french_path = "model_fr.txt";
    private String english_path = "model_en.txt";

    public Word2vec(String language) throws ClassNotFoundException {

        String trainingFile = "";
        String outputFile = "";

        if (language == "fr") {
            trainingFile = french_train_path;
            outputFile = french_path;
        } else if (language == "en") {
            trainingFile = english_train_path;
            outputFile = english_path;
        }

        try {

            model = SerializationUtils.loadModel(outputFile);

        } catch (IOException e) {

            System.out.println("needing to train");

            try {

                this.train(trainingFile,outputFile);

            } catch (IOException f) {

                System.out.println(f);

            }

        }

    }

    public void train(String trainPath, String outPath) throws IOException {

        Word2VecTrainer trainer = new Word2VecTrainerBuilder()
                .minCount(0)
                .numWorker(Runtime.getRuntime().availableProcessors())
                .model(Word2VecTrainer.NeuralNetworkLanguageModel.SKIP_GRAM)
                .build();
        model = trainer.train(new File(trainPath));
        SerializationUtils.saveModel(model, outPath);

    }

    public double score(String word1, String word2) throws ClassNotFoundException, IOException {
        return model.similarity(word1, word2);
    }

    public static void main(String [] args) {
        try {
            Word2vec w = new Word2vec("fr");
            try {
                System.out.println(w.score("maman","princesse"));
            } catch (IOException e) {
                System.out.println(e);
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }

}
