import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Fifteen {

    public static class EventManager {
        protected Map<String, List<Method>> subscriptions;

        public EventManager() {
            this.subscriptions = new HashMap<>();
        }

        public void subscribe(String eventType, Method handler) {
            if(this.subscriptions.keySet().contains(eventType)) {
                List<Method> newList = subscriptions.get(eventType);
                newList.add(handler);
                this.subscriptions.put(eventType, newList);
            } else {
                List<Method> newList = new ArrayList<>();
                newList.add(handler);
                this.subscriptions.put(eventType, newList);
            }
        }

        public void publish(List<String> event) throws InvocationTargetException, IllegalAccessException {
            String eventType = event.get(0);
            if(this.subscriptions.keySet().contains(eventType)) {
                for(Method h : this.subscriptions.get(eventType)) {
                    h.invoke(null, event);
                }
            }
        }
    }

    public static class DataStorage {
        protected static EventManager myEventManager;
        protected static List<String> data;

        public DataStorage(EventManager eventManager) throws NoSuchMethodException {
            this.myEventManager = eventManager;
            this.myEventManager.subscribe("load", this.getClass().getDeclaredMethod("load", List.class));
            this.myEventManager.subscribe("start", this.getClass().getDeclaredMethod("produceWords", List.class));
        }

        public static void load(List<String> event) {
            String pathToFile = event.get(1);
            List<String> wordList = new ArrayList<>();
            try {
                BufferedReader articleReader = null;
                articleReader = new BufferedReader(new FileReader(pathToFile));
                String strLine = null;
                strLine = articleReader.readLine();

                while(strLine != null) {
                    if(!strLine.trim().equals("")) {
                        String[] wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase().split(" ");
                        wordList.addAll(Arrays.asList(wordsOfTheLine));
                    }
                    strLine = articleReader.readLine();
                }
                articleReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //this.data = lineList;
            data = wordList;
        }

        public static void produceWords(List<String> event) throws InvocationTargetException, IllegalAccessException {
            List<String> dataStr = data;
            for(String w : dataStr) {
                List<String> sword = new ArrayList<>();
                sword.add("word");
                sword.add(w);
                myEventManager.publish(sword);
            }
            List<String> seof = new ArrayList<>();
            seof.add("eof");
            seof.add(null);
            myEventManager.publish(seof);
        }
    }

    public static class StopWordFilter {
        protected static EventManager myEventManager;
        protected static List<String> stopWords;
        public StopWordFilter(EventManager eventManager) throws NoSuchMethodException {
            this.stopWords = new ArrayList<>();
            this.myEventManager = eventManager;
            this.myEventManager.subscribe("load", this.getClass().getDeclaredMethod("load", List.class));
            this.myEventManager.subscribe("word", this.getClass().getDeclaredMethod("isStopWord", List.class));
        }

        public static void load(List<String> event) {
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
            stopWords = stopWordList;
        }

        public static void isStopWord(List<String> event) throws InvocationTargetException, IllegalAccessException {
            String word = event.get(1);
            if(!word.trim().equals("") && word.length() >= 2 && !stopWords.contains(word)) {
                List<String> strList = new ArrayList<>();
                strList.add("valid_word");
                strList.add(word);
                myEventManager.publish(strList);
            }
        }
    }

    public static class WordFrequencyCounter {
        protected EventManager myEventManager;
        protected static Map<String, Integer> wordFreqs;

        public WordFrequencyCounter(EventManager eventManager) throws NoSuchMethodException {
            this.wordFreqs = new HashMap<>();
            this.myEventManager = eventManager;
            this.myEventManager.subscribe("valid_word", this.getClass().getDeclaredMethod("incrementCount", List.class));
            this.myEventManager.subscribe("print", this.getClass().getDeclaredMethod("printFreqs", List.class));
        }

        public static void incrementCount(List<String> event) {
            String word = event.get(1);
            if(wordFreqs.keySet().contains(word)) {
                wordFreqs.put(word, ((Integer)wordFreqs.get(word)) + 1);
            } else {
                wordFreqs.put(word, 1);
            }
        }

        public static void printFreqs(List<String> event) {
            ArrayList<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(wordFreqs.entrySet());
            Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
            for(Map.Entry<String, Integer> entry : sortedMapList.subList(0, 25)) {
                System.out.println(entry.getKey() + "  -  " + entry.getValue());
            }
        }
    }

    public static class WordFrequencyApplication {
        protected static EventManager myEventManager;
        public WordFrequencyApplication(EventManager eventManager) throws NoSuchMethodException {
            this.myEventManager = eventManager;
            this.myEventManager.subscribe("run", this.getClass().getDeclaredMethod("run", List.class));
            this.myEventManager.subscribe("eof", this.getClass().getDeclaredMethod("stop", List.class));
        }

        public static void run(List<String> event) throws InvocationTargetException, IllegalAccessException {
            String pathToFile = event.get(1);
            List<String> strList = new ArrayList<>();
            strList.add("load");
            strList.add(pathToFile);
            myEventManager.publish(strList);
            //strList.clear();
            List<String> strList2 = new ArrayList<>();
            strList2.add("start");
            strList2.add(null);
            myEventManager.publish(strList2);
        }

        public static void stop(List<String> event) throws InvocationTargetException, IllegalAccessException {
            List<String> strList = new ArrayList<>();
            strList.add("print");
            strList.add(null);
            myEventManager.publish(strList);
        }
    }

    public static class NumberOfWordsWithZ {
        protected EventManager myEventManager;
        protected static Map<String, Integer> wordFreqs2;
        protected static Integer numberOfZ = 0;

        public NumberOfWordsWithZ(EventManager eventManager) throws NoSuchMethodException {
            this.wordFreqs2 = new HashMap<>();
            this.myEventManager = eventManager;
            this.myEventManager.subscribe("valid_word", this.getClass().getDeclaredMethod("increCountPlusZ", List.class));
            this.myEventManager.subscribe("print", this.getClass().getDeclaredMethod("printFrePlusZ", List.class));
        }

        public static void increCountPlusZ(List<String> event) {
            String word = event.get(1);
            if(wordFreqs2.keySet().contains(word)) {
                wordFreqs2.put(word, ((Integer)wordFreqs2.get(word)) + 1);
            } else {
                wordFreqs2.put(word, 1);
            }

            if(word.indexOf("z") >= 0 ) {
                numberOfZ += 1;
            }
        }

        public static void printFrePlusZ(List<String> event) {
            System.out.println("*WordsWithZ*" + "  -  " + numberOfZ);
        }
    }

    public static void main(String args[]) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        EventManager em = new EventManager();
        new DataStorage(em);
        new StopWordFilter(em);
        new WordFrequencyCounter(em);
        new WordFrequencyApplication(em);
        new NumberOfWordsWithZ(em);
        List<String> strList = new ArrayList<>();
        strList.add("run");
        strList.add(args[0]);
        em.publish(strList);
    }
}