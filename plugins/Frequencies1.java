package plugins;

import interfaces.TFFreqs;

import java.util.*;

public class Frequencies1 implements TFFreqs {
    public ArrayList<Map.Entry<String, Integer>> top25(List<String> wordList) {
        Map<String, Integer> wordMap = new HashMap<>();
        for (String word : wordList) {
            Integer fre = 1;
            if (wordMap.containsKey(word)) {
                fre = wordMap.get(word) + 1;
            }
            wordMap.put(word, fre);
        }

        ArrayList<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(((Map)wordMap).entrySet());
        Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
        return sortedMapList;
    }
}
