import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Twelve {
    static List<String> lineList = new ArrayList<>();

    public static class DSOC {
        Map<String, Object> me;
        Map<String, Object> dataStorageObj = new HashMap<>();
        public DSOC() {
            me = this.dataStorageObj;
        }
    }

    public static class SWOC {
        Map<String, Object> me;
        Map<String, Object> stopWordsObj = new HashMap<>();
        public SWOC() {
            me = this.stopWordsObj;
        }
    }

    public static class WFOC {
        Map<String, Object> me;
        Map<String, Object> wordFreqsObj = new HashMap<>();
        public WFOC() {
            me = this.wordFreqsObj;
        }
    }

    public static void extractWords(Map<String, Object> obj, String pathToFile) {
        try {
            BufferedReader articleReader = null;
            articleReader = new BufferedReader(new FileReader(pathToFile));
            String strLine = null;
            strLine = articleReader.readLine();

            while(strLine != null) {
                if(!strLine.trim().equals("")) {
                    String[] wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase().split(" ");
                    lineList.addAll(Arrays.asList(wordsOfTheLine));
                }
                strLine = articleReader.readLine();
            }
            articleReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        obj.put("data", lineList);
    }

    public static void loadStopWords(Map<String, Object> obj) {
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
        obj.put("stopWords", stopWordList);
    }

    public static void incrementCount(Map<String, Object> obj, String w) {
        Map<String, Integer> freqMap = (Map<String, Integer>)obj.get("freqs");
        freqMap.put(w, freqMap.containsKey(w) ? (Integer)freqMap.get(w) + 1 : 1);
        obj.put("freqs", freqMap);
    }

    public static void main(String[] args) {
        DSOC dsoc = new DSOC();
        SWOC swoc = new SWOC();
        WFOC wfoc = new WFOC();

        // 1.assemble dataStorageObj
        dsoc.dataStorageObj.put("data", new ArrayList<>());
        dsoc.dataStorageObj.put("init", (Consumer<String>)(pathToFile) -> extractWords(dsoc.me, pathToFile));
        dsoc.dataStorageObj.put("words", (Supplier<Object>)() -> dsoc.me.get("data"));

        // 2.assemble stoWordsObj
        swoc.stopWordsObj.put("stopWords", new ArrayList<>());
        swoc.stopWordsObj.put("init", (Runnable)() -> loadStopWords(swoc.me));
        swoc.stopWordsObj.put("isStopWord", (Function<String, Boolean>)(word) -> ((List<String>)swoc.me.get("stopWords")).contains(word));

        // 3.assemble stoWordsObj
        wfoc.wordFreqsObj.put("freqs", new HashMap<>());
        wfoc.wordFreqsObj.put("incrementCount", (Consumer<String>)(w) -> incrementCount(wfoc.me, w));
        wfoc.wordFreqsObj.put("sorted", (Supplier<ArrayList<Map.Entry<String, Integer>>>)() -> {
            ArrayList<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(((Map<String, Integer>)wfoc.me.get("freqs")).entrySet());
            Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
            return sortedMapList;
        });

        // initialize dataStorageObj
        ((Consumer<String>)dsoc.dataStorageObj.get("init")).accept(args[0]);

        // initialize stopWordsObj
        ((Runnable)swoc.stopWordsObj.get("init")).run();

        // filter stop words and count frequency
        for(String w : (List<String>)((Supplier<Object>)dsoc.dataStorageObj.get("words")).get()) {
            // 1.filter out meaningless words
            if(w.trim().equals("") || w.trim().length() < 2) {
                continue;
            }
            // 2.filter out stop words
            if(!((Function<String, Boolean>)swoc.stopWordsObj.get("isStopWord")).apply(w)) {
                ((Consumer<String>)wfoc.wordFreqsObj.get("incrementCount")).accept(w);
            }
        }

        // dynamically add a method to sort and print
        wfoc.wordFreqsObj.put("top25", (Runnable)() -> {
            ArrayList<Map.Entry<String, Integer>> sortedList = ((Supplier<ArrayList<Map.Entry<String, Integer>>>)wfoc.me.get("sorted")).get();
            for(Map.Entry<String, Integer> entry : sortedList.subList(0, 25)) {
                System.out.println(entry.getKey() + "  -  " + entry.getValue());
            }
        });
        // call the method
        ((Runnable)wfoc.wordFreqsObj.get("top25")).run();
    }
}