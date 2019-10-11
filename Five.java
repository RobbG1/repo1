import java.io.*;
import java.util.*;

public class Five {
    public static List<String> readFile(String pathToFile) throws IOException {
        // Loads the novel file and returns the entire contents of the file as a string list
        BufferedReader articleReader = new BufferedReader(new FileReader(pathToFile));
        String strLine = articleReader.readLine().trim();
        List<String> lineList = new ArrayList<>();
        while(strLine != null) {
            if(!strLine.trim().equals("")) {
                lineList.add(strLine);
            }
            strLine = articleReader.readLine();
        }
        articleReader.close();
        return lineList;
    }

    public static List<String> filterCharsAndNormalize(List<String> strList) {
        // Takes a string list and returns a copy with  all nonalphanumeric chars replaced by white space
        List<String> returnStrList = new ArrayList<>();
        for(String strLine : strList) {
            String wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase();
            returnStrList.add(wordsOfTheLine);
        }
        return returnStrList;
    }

    public static List<String> scan(List<String> strList) {
        // Takes a string list and scans for words, returning a list of words.
        List<String> returnStrList = new ArrayList<>();
        for(String strLine : strList) {
            String[] wordsOfTheLine = strLine.split(" ");
            returnStrList.addAll(Arrays.asList(wordsOfTheLine));
        }
        return returnStrList;
    }

    public static List<String> removeStopWords(List<String> wordList) throws IOException {
        // Loads the stop word list
        String stopWordsPath = "../stop_words.txt";
        BufferedReader stopWordsReader = new BufferedReader(new FileReader(stopWordsPath));
        String stopWordsLine = stopWordsReader.readLine();
        stopWordsReader.close();
        String[] stopWords = null;
        if(stopWordsLine != null && !stopWordsLine.trim().equals("")) {
            stopWords = stopWordsLine.split(",");
        }
        ArrayList<String> stopWordList = new ArrayList<String>(Arrays.asList(stopWords));
        // Removes meaningless words and the stop words
        List<String> returnWordList = new ArrayList<>();
        for(String word : wordList) {
            if(word != null && !word.equals("")
                    && (word.length() >= 2) && !stopWordList.contains(word)) {
                returnWordList.add(word);
            }
        }

        return returnWordList;
    }

    public static Map<String, Integer> frequencies(List<String> wordList) {
        // Takes a list of words and returns a map associating words with frequencies of occurrence
        Map<String, Integer> wordMap = new HashMap<>();
        for(String word : wordList) {
            Integer fre = 1;
            if(wordMap.containsKey(word)) {
                fre = wordMap.get(word) + 1;
                wordMap.put(word, fre);
            } else {
                wordMap.put(word, fre);
            }
        }
        return wordMap;
    }

    public static ArrayList<Map.Entry<String, Integer>> sort(Map<String, Integer> oldMap) {
        // Takes a map of words and their frequencies
        //    and returns a list of pairs where the entries are
        //    sorted by frequency
        ArrayList<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(oldMap.entrySet());
        Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
        return sortedMapList;
    }

    public static void printAll(ArrayList<Map.Entry<String, Integer>> sortedList) {
        // Takes a list of pairs where the entries are sorted by frequency and print the first 25 elements.
        for(Map.Entry<String, Integer> entry : sortedList.subList(0, 25)) {
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
        }
    }

    // The main function
    public static void main(String[] args) throws IOException {
        printAll(sort(frequencies(removeStopWords(scan(filterCharsAndNormalize(readFile(args[0])))))));
    }
}