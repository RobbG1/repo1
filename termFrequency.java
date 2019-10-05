import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class termFrequency {
    public static void main(String[] args) throws IOException {

        // Set the file paths
        String stopWordsPath = "../stop_words.txt";

        // Get the stop word list
        BufferedReader stopWordsReader = new BufferedReader(new FileReader(stopWordsPath));
        String stopWordsLine = stopWordsReader.readLine();
        stopWordsReader.close();
        String[] stopWords = null;
        if(stopWordsLine != null && !stopWordsLine.trim().equals("")) {
                stopWords = stopWordsLine.split(",");
        }
        ArrayList<String> stopWordList = new ArrayList<String>(Arrays.asList(stopWords));

        // Load the novel file using address from command line
        BufferedReader articleReader = new BufferedReader(new FileReader(new File(args[0])));
        Map<String, Integer> termFrequency = new HashMap<String, Integer>();

        String strLine = articleReader.readLine();
        while(strLine != null) {
            // if it's not an empty line
            if(!strLine.trim().equals("")) {
                String[] wordsOfTheLine = strLine.split("[\\W_]+");
                for(String word : wordsOfTheLine){
                    String lowerCaseWord = word.toLowerCase();
                    // Ignore stop words, empty words, meaningless words
                    if(stopWordList.contains(lowerCaseWord)
                            || lowerCaseWord.trim().equals("")
                            || lowerCaseWord.length() < 2) {
                        continue;
                    }
                    // If not existed, frequency is 1
                    Integer frequency = 1;
                    if(termFrequency.containsKey(lowerCaseWord)){
                        frequency = termFrequency.get(lowerCaseWord) + 1;
                    }
                    termFrequency.put(lowerCaseWord, frequency);
                }
            }
            strLine = articleReader.readLine();
        }
        articleReader.close();

        ArrayList<Map.Entry<String, Integer>> sortedList = sortTermFrequency(termFrequency);
        printSortedList(sortedList);
    }

    public static ArrayList<Map.Entry<String, Integer>> sortTermFrequency(Map<String, Integer> termFrequency) {
        ArrayList<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(termFrequency.entrySet());
        Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
        return sortedMapList;
    }

    public static void printSortedList(ArrayList<Map.Entry<String, Integer>> sortedList) {
        for(Map.Entry<String, Integer> entry : sortedList.subList(0, 25)) {
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
        }
    }
}
