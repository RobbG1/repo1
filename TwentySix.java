import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class TwentySix {
    static List<Object[]> allColumns = new ArrayList<>();

    public static void update() {
        for(Object[] c : allColumns) {
            if(null != c[1]) {
                c[0] = ((Supplier)c[1]).get();
            }
        }
    }

    public static List<String> loadStopWords() {
        String stopWordsLine = null;
        String[] stopWords = null;
        try{
            // Takes a list of words and returns a copy with all stop words removed
            BufferedReader stopWordsReader = new BufferedReader(new FileReader("../stop_words.txt"));
            stopWordsLine = stopWordsReader.readLine();
            stopWordsReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(stopWordsLine != null && !stopWordsLine.trim().equals("")) {
            stopWords = stopWordsLine.split(",");
        }
        List<String> stopWordList = new ArrayList<>(Arrays.asList(stopWords));
        return stopWordList;
    }

    public static List<String> loadNovelWords(String pathToFile) {
        List<String> wordList = new ArrayList<>();
        try {
            BufferedReader articleReader = null;
            articleReader = new BufferedReader(new FileReader(pathToFile));
            String strLine = null;
            strLine = articleReader.readLine();

            while(strLine != null) {
                if(!strLine.trim().equals("")) {
                    String[] wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase().split(" ");
                    for(String word : wordsOfTheLine) {
                        if(!word.trim().equals("") && word.length() >= 2) {
                            wordList.add(word.trim());
                        }
                    }
                }
                strLine = articleReader.readLine();
            }
            articleReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;

    }

    public static void main(String args[]) {
        Object[] allWords = new Object[2];
        List<String> awResultL = new ArrayList<>();
        allWords[0] = awResultL;
        allWords[1] = null;

        Object[] stopWords = new Object[2];
        List<String> swResultL = new ArrayList<>();
        stopWords[0] = swResultL;
        stopWords[1] = null;

        Object[] nonStopWords = new Object[2];
        List<String> nswResultL = new ArrayList<>();
        nonStopWords[0] = nswResultL;
        nonStopWords[1] = (Supplier<List<String>>)() -> ((List<String>)allWords[0]).stream().map((w) -> ((List<String>)stopWords[0]).contains(w) ? "" : w).collect(Collectors.toList());

        Object[] uniqueWords = new Object[2];
        List<String> uwResultL = new ArrayList<>();
        uniqueWords[0] = uwResultL;
        uniqueWords[1] = (Supplier<List<String>>)() ->
                (
                        (List<String>)((Set<String>) ((List<String>) nonStopWords[0]).stream().filter(w -> !w.equals("")).collect(Collectors.toSet()))
                                .stream().collect(Collectors.toList())
                );

        Object[] counts = new Object[2];
        List<String> csResultL = new ArrayList<>();

        counts[0] = csResultL;
        counts[1] = (Supplier<List<Integer>>)() -> {
            List<Integer> result = new ArrayList<>();
            for(String s : (List<String>) uniqueWords[0]) {
                result.add(Collections.frequency((List<String>)nonStopWords[0], s));
            }
            return result;
        };

        Object[] sortedData = new Object[2];
        List<Map<String, Integer>> sdResultL = new ArrayList<>();
        sortedData[0] = sdResultL;
        sortedData[1] = (Supplier<List<Map.Entry<String, Integer>>>)() -> {
            Map<String, Integer> wc = new HashMap<>();
            for(int i=0; i< ((List<String>)uniqueWords[0]).size(); i++) {
                wc.put(((List<String>)uniqueWords[0]).get(i), ((List<Integer>)counts[0]).get(i));
            }

            List<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(wc.entrySet());
            Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
            return sortedMapList;

        };

        allColumns.add(allWords);
        allColumns.add(stopWords);
        allColumns.add(nonStopWords);
        allColumns.add(uniqueWords);
        allColumns.add(counts);
        allColumns.add(sortedData);

        allWords[0] = loadNovelWords(args[0]);
        stopWords[0] = loadStopWords();

        update();

        List<Map.Entry<String, Integer>> sdL = ((Supplier<List<Map.Entry<String, Integer>>>)sortedData[1]).get();
        for(Map.Entry<String, Integer> entry : sdL.subList(0, 25)) {
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
        }

    }

}


