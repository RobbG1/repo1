import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


    interface IFunction {
        Object call(Object arg) throws IOException;
    }

    class TFTheOne {
        private Object value;
        TFTheOne(Object v) {
            value = v;
        }

        public TFTheOne bind(IFunction func) throws IOException {
           value = func.call(value);
           return this;
        }

        public void printme() {
            System.out.println(value);
        }
    }

    class ReadFile implements IFunction {
        public Object call(Object pathToFile) throws IOException {
            BufferedReader articleReader = new BufferedReader(new FileReader(pathToFile.toString()));
            String strLine = articleReader.readLine();
            List<String> lineList = new ArrayList<>();
            while(strLine != null) {
                if(!strLine.trim().equals("")) {
                    lineList.add(strLine);
                }
                strLine = articleReader.readLine();
            }
            articleReader.close();
            return lineList;
        }
    }

    class FilterChars implements IFunction {
        public Object call(Object strList) throws IOException {
            // Takes a string list and returns a copy with  all nonalphanumeric chars replaced by white space
            List<String> returnStrList = new ArrayList<>();
            List<String> castlist = (List) strList;
            for (String strLine : castlist) {
                String wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase();
                returnStrList.add(wordsOfTheLine);
            }
            return returnStrList;
        }
    }

    class Normalize implements IFunction {
        public Object call(Object strList) throws IOException {
            // Takes a string list and returns a copy with  all nonalphanumeric chars replaced by white space
            List<String> returnStrList = new ArrayList<>();
            List<String> castlist = (List)strList;
            for(String strLine : castlist) {
                String wordsOfTheLine = strLine.toLowerCase();
                returnStrList.add(wordsOfTheLine);
            }
            return returnStrList;
        }

    }

    class Scan implements IFunction {
        public Object call(Object strList) throws IOException {
            // Takes a string list and scans for words, returning a list of words.
            List<String> returnStrList = new ArrayList<>();
            List<String> castlist = (List)strList;
            for(String strLine : castlist) {
                String[] wordsOfTheLine = strLine.split(" ");
                returnStrList.addAll(Arrays.asList(wordsOfTheLine));
            }
            return returnStrList;
        }

    }

    class RemoveStopWords implements IFunction {
        public Object call(Object wordList) throws IOException {
            // Takes a list of words and returns a copy with all stop words removed
            BufferedReader stopWordsReader = new BufferedReader(new FileReader("../stop_words.txt"));
            String stopWordsLine = stopWordsReader.readLine();
            stopWordsReader.close();
            String[] stopWords = null;
            if (stopWordsLine != null && !stopWordsLine.trim().equals("")) {
                stopWords = stopWordsLine.split(",");
            }
            ArrayList<String> stopWordList = new ArrayList<String>(Arrays.asList(stopWords));
            List<String> returnWordList = new ArrayList<>();
            List<String> castlist = (List)wordList;
            for (String word : castlist) {
                if (word != null && !word.equals("")
                        && (word.length() >= 2) && !stopWordList.contains(word)) {
                    returnWordList.add(word);
                }
            }
            return returnWordList;
        }
    }

    class Frequencies implements IFunction {
        public Object call(Object wordList) throws IOException {
            // Takes a list of words and returns a map associating words with frequencies of occurrence
            Map<String, Integer> wordMap = new HashMap<>();
            List<String> castlist = (List)wordList;
            for (String word : castlist) {
                Integer fre = 1;
                if (wordMap.containsKey(word)) {
                    fre = wordMap.get(word) + 1;
                }
                wordMap.put(word, fre);
            }
            return wordMap;
        }
    }

    class Sort implements IFunction {
        public Object call(Object oldMap) throws IOException {
            // Takes a map of words and their frequencies and returns a list of pairs
            // where the entries are sorted by frequency
            ArrayList<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(((Map)oldMap).entrySet());
            Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
            return sortedMapList;
        }
    }

    class Top25Freq implements IFunction {
        public Object call(Object sortedList) {
            String result = "";
            // Takes a list of pairs where the entries are sorted by frequency and print the first 25 elements.
            for (Map.Entry<String, Integer> entry : ((List<Map.Entry<String, Integer>>)sortedList).subList(0, 25)) {
                result += entry.getKey() + "  -  " + entry.getValue() + "\n";
            }
            return result;
        }
    }

public class Nine {
    public static void main(String[] args) throws IOException {
        TFTheOne one = new TFTheOne(args[0]);
        one.bind(new ReadFile()).bind(new FilterChars()).bind(new Normalize()).bind(new Scan()).bind(new RemoveStopWords())
        .bind(new Frequencies()).bind(new Sort()).bind(new Top25Freq()).printme();
    }
}
