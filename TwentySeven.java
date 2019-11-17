import java.io.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

public class TwentySeven {

    public static void main(String args[]) {
        CountNSortClass countAndSort = new CountNSortClass(args[0]);

        while (countAndSort.hasNext()) {
            //List<Map.Entry<String, Integer>> word_freqs = countAndSort(args[0]).next();
            System.out.println("-----------------------------");
            List<Map.Entry<String, Integer>> word_freqs = countAndSort.next();
            //List<Map.Entry<String, Integer>> wordFreqsSorted = ;
            for(Map.Entry<String, Integer> entry : word_freqs.subList(0, 25)) {
                System.out.println(entry.getKey() + "  -  " + entry.getValue());
            }
        }

    }
}

class CountNSortClass implements Iterator<List<Map.Entry<String, Integer>>> {
    Map<String, Integer> freqs = new HashMap<>();
    int i = 1;
    public String pathToFile;
    NonStopWordsClass nonStopWords;
    List<Map.Entry<String, Integer>> sortedMapList;
    Boolean hasNextValue;

    public CountNSortClass(String pathToFile) {
        this.pathToFile = pathToFile;
        this.nonStopWords = new NonStopWordsClass(this.pathToFile);
        hasNextValue = nonStopWords.hasNext();
    }

   // NSWClass nonStopWords = new NSWClass(this.pathToFile);

    public boolean hasNext() {
        return hasNextValue;
    }

    public List<Map.Entry<String, Integer>> next() {
        while(nonStopWords.hasNext()) {
            String w = nonStopWords.next();
            Integer count = 0;
            if(!freqs.containsKey(w)) {
                count = 1;
            } else {
                count = freqs.get(w) + 1;
            }
            freqs.put(w, count);
            if(i % 5000 == 0) {
                sortedMapList = new ArrayList<>(freqs.entrySet());
                Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
                i = i + 1;
                return sortedMapList;
            }
            i = i + 1;
        }
        hasNextValue = false;
        sortedMapList = new ArrayList<>(freqs.entrySet());
        Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
        return sortedMapList;
    }

    public void remove() {
    }
}

class NonStopWordsClass implements Iterator<String> {
    protected String pathToFile;
    protected List<String> stopwords;
    AllWordsClass allWords;

    public NonStopWordsClass(String pathToFile) {
        this.pathToFile = pathToFile;
        loadStopWords();
        this.allWords = new AllWordsClass(this.pathToFile);
    }

    @Override
    public boolean hasNext() {
        return allWords.hasNext();
    }

    @Override
    public String next() {
        while(allWords.hasNext()) {
            String w = allWords.next();
            if(null != w && !w.trim().equals("") && w.length() >= 2 && !this.stopwords.contains(w)) {
                return w.trim();
            }
        }

        return null;
    }

    public void loadStopWords() {
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
        stopwords = new ArrayList<>(Arrays.asList(stopWords));
    }
}

class AllWordsClass implements Iterator<String> {
    protected String pathToFile;
    protected Boolean startChar = true;
    MyCharactersClass myCharacters;

    public AllWordsClass(String pathToFile) {
        this.pathToFile = pathToFile;
        myCharacters = new MyCharactersClass(this.pathToFile);
    }

    @Override
    public boolean hasNext() {
        return myCharacters.hasNext();
    }

    @Override
    public String next() {
        String word = "";
        while(myCharacters.hasNext()) {
            String c = myCharacters.next().toString();
            if(this.startChar) {
                //String word = "";
                if(c.matches("[A-Za-z0-9]+")) {
                    // We found the start of a word
                    word = c.toLowerCase();
                    this.startChar = false;
                }
            } else {
                //String word = "";
                if(c.matches("[A-Za-z0-9]+")) {
                    word = word + c.toLowerCase();
                } else {
                    startChar = true;
                    return word;
                }
            }
        }

        return null;
    }
}

class MyCharactersClass implements Iterator<String> {
    protected String pathToFile;
    protected List<Character> lineCharList;
    Iterator lineCharListIter;
    MyCharLineClass myCharLine;

    public MyCharactersClass(String pathToFile) {
        this.pathToFile = pathToFile;
        myCharLine = new MyCharLineClass(this.pathToFile);
        loadLineCharList();
    }

    public void loadLineCharList() {
        if(myCharLine.hasNext()) {
            lineCharList = myCharLine.next();
            lineCharListIter = lineCharList.iterator();
        }
    }

    @Override
    public boolean hasNext() {
        return lineCharListIter.hasNext();
    }

    @Override
    public String next() {
        if (lineCharListIter.hasNext()) {
            String returnStr = lineCharListIter.next().toString();
            if (!lineCharListIter.hasNext()) {
                loadLineCharList();
            }
            return returnStr;
        }
        return null;
    }

}

class MyCharLineClass implements Iterator<List<Character>> {
    protected String pathToFile;
    List<Character> lineCharacter = new ArrayList<>();
    String strLine = null;
    BufferedReader articleReader = null;
    Boolean hasNextLine = true;

    public MyCharLineClass(String pathToFile) {
        this.pathToFile = pathToFile;
        initFirstLine(this.pathToFile);
    }

    public void initFirstLine(String pathToFile) {
        try {
            articleReader = new BufferedReader(new FileReader(pathToFile));
            strLine = articleReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNextLine() {
        try {
            strLine = articleReader.readLine();
            while(null != strLine && strLine.equals("")) {
                strLine = articleReader.readLine();
            }
            if(null == strLine) {
                articleReader.close();
                hasNextLine = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean hasNext() {
        return hasNextLine;
    }

    @Override
    public List<Character> next() {
        if(strLine != null) {

            // prepare the char line
            if(!strLine.equals("")) {
                List<Character> lineChars = new ArrayList<>();
                String lineStr = " " + strLine.replaceAll("[\\W_]+", " ") + " ";
                for (char ch : lineStr.toCharArray()) {
                    lineChars.add(ch);
                }
                loadNextLine();
                return lineChars;
            }

        }

        return null;
    }
}