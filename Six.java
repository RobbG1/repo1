import java.io.*;
import java.util.*;
public class Six {
    public static void main(String[] args) throws IOException {
        // Load the stop word list
        ArrayList<String> stopWordList = new ArrayList<String>(Arrays.asList((new BufferedReader(new FileReader("/Users/Robert/Desktop/Desktop/Courses/TestCase/stop_words.txt"))).readLine().trim().split(",")));
        // Load the novel content string
        File file = new File("/Users/Robert/Desktop/Desktop/Courses/TestCase/pride-and-prejudice.txt");
        byte[] fileContent = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(fileContent);
        fis.close();
        // Get the novel word list
       // String[] novelContentWords = (new String(fileContent, "UTF-8")).toLowerCase().split("[\\W_]+");
        ArrayList<String> novelWordList = new ArrayList<String>(Arrays.asList((new String(fileContent, "UTF-8")).toLowerCase().split("[\\W_]+")));
        // Assemble word frequency map
        Map<String, Integer> termFrequencyMap = new HashMap<>();
        novelWordList.forEach(word -> {if(!word.equals("") && (word.length() >= 2) && !stopWordList.contains(word)) termFrequencyMap.put(word, termFrequencyMap.containsKey(word) ? termFrequencyMap.get(word) + 1 : 1) ;});
        // Sort by word frequency
        ArrayList<Map.Entry<String, Integer>> termFreqList = new ArrayList<>(termFrequencyMap.entrySet());
        Collections.sort(termFreqList, (o1, o2) -> o2.getValue() - o1.getValue());
        // Print the first 25 items
        termFreqList.subList(0, 25).forEach(termFreqEntry -> System.out.println(termFreqEntry.getKey() + "  -  " + termFreqEntry.getValue()));
    }
}
