import java.io.*;
import java.util.*;

    interface IFunction{
        void call(Object arg, IFunction func) throws IOException;
    }

    class ReadFile implements IFunction{
        public void call(Object pathToFile, IFunction func) throws IOException {
            // Loads the novel file and returns the entire contents of the file as a string list
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
            //return lineList;
            func.call(lineList, new Normalize());
        }
    }

    class FilterChars implements IFunction {
        public void call(Object strList, IFunction func) throws IOException {
            // Takes a string list and returns a copy with  all nonalphanumeric chars replaced by white space
            List<String> returnStrList = new ArrayList<>();
            List<String> castlist = (List)strList;
            for(String strLine : castlist) {
                String wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase();
                returnStrList.add(wordsOfTheLine);
            }
            //return returnStrList;
            func.call(returnStrList, new Scan());
        }

    }

    class Normalize implements IFunction {
        public void call(Object strList, IFunction func) throws IOException {
            // Takes a string list and returns a copy with  all nonalphanumeric chars replaced by white space
            List<String> returnStrList = new ArrayList<>();
            List<String> castlist = (List)strList;
            for(String strLine : castlist) {
                String wordsOfTheLine = strLine.toLowerCase();
                returnStrList.add(wordsOfTheLine);
            }
            //return returnStrList;
            func.call(returnStrList, new RemoveStopWords());
        }

    }

    class Scan implements IFunction {
        public void call(Object strList, IFunction func) throws IOException {
            // Takes a string list and scans for words, returning a list of words.
            List<String> returnStrList = new ArrayList<>();
            List<String> castlist = (List)strList;
            for(String strLine : castlist) {
                String[] wordsOfTheLine = strLine.split(" ");
                returnStrList.addAll(Arrays.asList(wordsOfTheLine));
            }
            func.call(returnStrList, new Frequencies());
        }

    }

    class RemoveStopWords implements IFunction {
        public void call(Object wordList, IFunction func) throws IOException {
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

            func.call(returnWordList, new Sort());
        }
    }

    class Frequencies implements IFunction {
        public void call(Object wordList, IFunction func) throws IOException {
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
            func.call(wordMap, new PrintAll());
        }
    }

    class Sort implements IFunction {
        public void call(Object oldMap, IFunction func) throws IOException {
            // Takes a map of words and their frequencies and returns a list of pairs
            // where the entries are sorted by frequency
            ArrayList<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(((Map)oldMap).entrySet());
            Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
            //return sortedMapList;
            func.call(sortedMapList, new NoOp());
        }
    }

    class PrintAll implements IFunction {
        public void call(Object sortedList, IFunction func) {
            // Takes a list of pairs where the entries are sorted by frequency and print the first 25 elements.
            for (Map.Entry<String, Integer> entry : ((List<Map.Entry<String, Integer>>)sortedList).subList(0, 25)) {
                System.out.println(entry.getKey() + "  -  " + entry.getValue());
            }
        }
    }

    class NoOp implements IFunction {
        public void call(Object sortedList, IFunction func) {
            return;
        }
    }

    public class Eight{
        // The main function
        public static void main(String[] args) throws IOException {
            new ReadFile().call(args[0], new FilterChars());
        }
    }
