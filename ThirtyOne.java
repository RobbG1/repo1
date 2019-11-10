import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class ThirtyOne {

    public static List<List<String>> partition(List<String> dataStr, Integer nLines) {
        List<List<String>> returnList = new ArrayList<>();
        for(int i = 0; i < dataStr.size(); i += nLines) {
            returnList.add(dataStr.subList(i, min((i + nLines), dataStr.size())));
        }
        return returnList;
    }

    public static List<Map<String, Integer>>  splitWords(List<String> dataStr) {
        List<Map<String, Integer>> result = new ArrayList<>();

        class InnerSWC {
            public List<String> scan(List<String> strData) {
                List<String> returnList = new ArrayList<>();
                for(String strLine : strData) {
                    String[] wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase().split(" ");
                    returnList.addAll(Arrays.asList(wordsOfTheLine));
                }
                return returnList;
            }

            public List<String> removeStopWords(List<String> wordList) {
                List<String> returnList = new ArrayList<>();
                String stopWordsLine = null;
                String[] stopWordsArray = null;
                try{
                    // Takes a list of words and returns a copy with all stop words removed
                    BufferedReader stopWordsReader = new BufferedReader(new FileReader("../stop_words.txt"));
                    stopWordsLine = stopWordsReader.readLine();
                    stopWordsReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(stopWordsLine != null && !stopWordsLine.trim().equals("")) {
                    stopWordsArray = stopWordsLine.split(",");
                }
                List<String> stopWordList = new ArrayList<>(Arrays.asList(stopWordsArray));
                for(String word : wordList) {
                    if(!word.trim().equals("") && word.length() >= 2 && !stopWordList.contains(word)) {
                        returnList.add(word);
                    }
                }
                return returnList;
            }
        }

        InnerSWC iswc = new InnerSWC();
        List<String> words = iswc.removeStopWords(iswc.scan(dataStr));
        for(String w : words) {
            Map<String, Integer> swlTMap = new HashMap<>();
            swlTMap.put(w, 1);
            result.add(swlTMap);
        }

        return result;
    }

    public static Map<String, List<Map<String, Integer>>> regroup(List<List<Map<String, Integer>>> pairsList) {
        Map<String, List<Map<String, Integer>>> mapping = new HashMap<>();
        for(List<Map<String, Integer>> tList : pairsList) {
            for(Map<String, Integer> pairs : tList) {
                //System.out.println("yes");
                for(Map.Entry p : pairs.entrySet()) {
                    if(mapping.containsKey(p.getKey())) {
                        List<Map<String, Integer>> pList = new ArrayList<>();
                        pList = mapping.get(p.getKey());
                        Map<String, Integer> tempMap = new HashMap<>();
                        tempMap.put((String)p.getKey(), (Integer)p.getValue());
                        pList.add(tempMap);
                        mapping.put((String)p.getKey(), pList);
                    } else {
                        List<Map<String, Integer>> pList = new ArrayList<>();
                        Map<String, Integer> tempMap = new HashMap<>();
                        tempMap.put((String)p.getKey(), (Integer)p.getValue());
                        pList.add(tempMap);
                        mapping.put((String)p.getKey(), pList);
                    }
                }
            }
        }

        return mapping;
    }

    public static Map<String, Integer> countWords(Map.Entry<String, List<Map<String, Integer>>> mapping) {
        Map<String, Integer> result = new HashMap<>();
        List<Integer> valueList = new ArrayList<>();
        for(Map<String, Integer> test : mapping.getValue()) {
            for(Map.Entry s : test.entrySet()) {
                valueList.add((Integer)s.getValue());
            }

        }
        Integer sum = valueList.stream().reduce(0, (element1, element2) -> element1 + element2);
        result.put(mapping.getKey(), sum);
        return result;
    }

    public static List<String> readFile(String pathToFile) {
        List<String> lineList = new ArrayList<>();
        try {
            BufferedReader articleReader = null;
            articleReader = new BufferedReader(new FileReader(pathToFile));
            String strLine = null;
            strLine = articleReader.readLine();

            while(strLine != null) {
                if(!strLine.trim().equals("")) {
                    //String[] wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase().split(" ");
                    //lineList.addAll(Arrays.asList(wordsOfTheLine));
                    lineList.add(strLine);
                }
                strLine = articleReader.readLine();
            }
            articleReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineList;
    }

    public static List<Map.Entry<String, Integer>> sort(List<Map<String, Integer>> wordFreqsList) {
        Map<String, Integer> wordFreqs = new HashMap<>();
        for(Map<String, Integer> m : wordFreqsList) {
            for(Map.Entry e : m.entrySet()) {
                wordFreqs.put((String)e.getKey(), (Integer)e.getValue());
            }
        }
        //List<Map.Entry<String, Integer>> result = new ArrayList<>();
        List<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(wordFreqs.entrySet());
        Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
        return sortedMapList;
    }

    public static void main(String args[]) {
        List<List<Map<String, Integer>>> splits = partition(readFile(args[0]), 200).stream().map((strList) -> splitWords(strList)).collect(Collectors.toList());
        Map<String, List<Map<String, Integer>>> splitsPerWord = regroup(splits);


        List<Map<String, Integer>> wordFreqs = splitsPerWord.entrySet().stream().map((argsC) -> countWords(argsC)).collect(Collectors.toList());

        List<Map.Entry<String, Integer>> wordFreqsSorted = sort(wordFreqs);
        for(Map.Entry<String, Integer> entry : wordFreqsSorted.subList(0, 25)) {
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
        }
    }
}
