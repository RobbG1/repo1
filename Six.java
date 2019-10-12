import java.io.*;
import java.util.*;
public class Six {
    public static void main(String[] args) throws IOException {
        ArrayList<String> stopWordList = new ArrayList<String>(Arrays.asList((new BufferedReader(new FileReader("../stop_words.txt"))).readLine().trim().split(",")));
        File file = new File(args[0]);
        byte[] fileContent = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(fileContent);
        fis.close();
        ArrayList<String> novelWordList = new ArrayList<String>(Arrays.asList((new String(fileContent, "UTF-8")).toLowerCase().split("[\\W_]+")));
        Map<String, Integer> termFrequencyMap = new HashMap<>();
        novelWordList.forEach(word -> {if(!word.equals("") && (word.length() >= 2) && !stopWordList.contains(word)) termFrequencyMap.put(word, termFrequencyMap.containsKey(word) ? termFrequencyMap.get(word) + 1 : 1) ;});
        ArrayList<Map.Entry<String, Integer>> termFreqList = new ArrayList<>(termFrequencyMap.entrySet());
        Collections.sort(termFreqList, (o1, o2) -> o2.getValue() - o1.getValue());
        termFreqList.subList(0, 25).forEach(termFreqEntry -> System.out.println(termFreqEntry.getKey() + "  -  " + termFreqEntry.getValue()));
    }
}
