package plugins;

import interfaces.TFWords;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Words2  implements TFWords {

    @Override
    public List<String> extractWords(String path) throws IOException {
        List<String> returnWordList = new ArrayList<>();
        ArrayList<String> stopWordList = new ArrayList<String>(Arrays.asList((new BufferedReader(new FileReader("../stop_words.txt"))).readLine().trim().split(",")));
        File file = new File(path);
        byte[] fileContent = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(fileContent);
        fis.close();
        ArrayList<String> novelWordList = new ArrayList<String>(Arrays.asList((new String(fileContent, "UTF-8")).toLowerCase().split("[\\W_]+")));
        novelWordList.forEach(word -> {if(!word.equals("") && (word.length() >= 2) && !stopWordList.contains(word)) returnWordList.add(word);});
        return returnWordList;
    }
}
