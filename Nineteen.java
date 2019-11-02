import interfaces.TFFreqs;
import interfaces.TFWords;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/*
interface TFWords {
    List<String> extractWords(String path);
}
interface TFFreqs {
    ArrayList<Map.Entry<String, Integer>> top25(List<String> words);
}


 */
public class Nineteen{
    //TFWords tfwords;
    //TFFreqs tffreqs;
    static String wordsPluginPath;
    static String freqPluginPath;

    public static void loadPlugins() {
        Properties config = new Properties();
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("./config.properties");
            config.load(fileInputStream);
            wordsPluginPath = config.getProperty("words");
            freqPluginPath = config.getProperty("frequencies");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        loadPlugins();

        ArrayList<Map.Entry<String, Integer>> wordFreqs = new ArrayList<Map.Entry<String, Integer>>();
        TFWords tfwords = (TFWords) Class.forName(wordsPluginPath).getDeclaredConstructor().newInstance();
        TFFreqs tffreqs = (TFFreqs) Class.forName(freqPluginPath).getDeclaredConstructor().newInstance();

        wordFreqs = tffreqs.top25(tfwords.extractWords(args[0]));

        for(Map.Entry<String, Integer> entry : wordFreqs.subList(0, 25)) {
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
        }

    }
}
