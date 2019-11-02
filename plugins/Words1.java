package plugins;

import interfaces.TFWords;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Words1 implements TFWords {
    public List<String> extractWords(String path) {
        List<String> wordList = new ArrayList<>();
        List<String> returnWordList = new ArrayList<>();
        String stopWordsLine = null;
        String[] stopWords = null;
        try {
            BufferedReader fileReader = null;
            fileReader = new BufferedReader(new FileReader(path));
            String strLine = null;
            strLine = fileReader.readLine();

            while(strLine != null) {
                if(!strLine.trim().equals("")) {
                    String[] wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase().split(" ");
                    wordList.addAll(Arrays.asList(wordsOfTheLine));
                }
                strLine = fileReader.readLine();
            }

            //fileReader = new BufferedReader(new FileReader("../stop_words.txt"));
            fileReader = new BufferedReader(new FileReader("../stop_words.txt"));
            stopWordsLine = fileReader.readLine();
            fileReader.close();

            if(stopWordsLine != null && !stopWordsLine.trim().equals("")) {
                stopWords = stopWordsLine.split(",");
            }
            List<String> stopWordList = new ArrayList<>(Arrays.asList(stopWords));
            for (String word : wordList) {
                if (word != null && !word.equals("")
                        && (word.length() >= 2) && !stopWordList.contains(word)) {
                    returnWordList.add(word);
                }
            }
        } catch (IOException e) {
            System.out.println("***Load files error: " + e);
        }

        return returnWordList;
    }
}
