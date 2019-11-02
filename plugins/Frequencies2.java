package plugins;
import interfaces.TFFreqs;
import java.util.*;

public class Frequencies2 implements TFFreqs {
    public ArrayList<Map.Entry<String, Integer>> top25(List<String> wordList) {
        Map<String, Integer> termFrequencyMap = new HashMap<>();
        wordList.forEach(word -> {termFrequencyMap.put(word, termFrequencyMap.containsKey(word) ? termFrequencyMap.get(word) + 1 : 1) ;});
        ArrayList<Map.Entry<String, Integer>> termFreqList = new ArrayList<>(termFrequencyMap.entrySet());
        Collections.sort(termFreqList, (o1, o2) -> o2.getValue() - o1.getValue());
        return termFreqList;
    }
}
