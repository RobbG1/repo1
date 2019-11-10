import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class TwentyEight extends ActiveWFObject{
    public static void main(String args[]) throws InterruptedException {
        StopWordManager stopWordManager = new StopWordManager();
        List<Object> tempMessage = new ArrayList<>();
        tempMessage.add("init");
        send(stopWordManager, tempMessage);

        DataStorageManager storageManager = new DataStorageManager();
        List<Object> tempMessage1 = new ArrayList<>();
        tempMessage1.add("init");
        tempMessage1.add(args[0]);
        tempMessage1.add(stopWordManager);
        send(storageManager, tempMessage1);

        WordFrequencyController wfController = new WordFrequencyController();
        List<Object> tempMessage2 = new ArrayList<>();
        tempMessage2.add("run");
        tempMessage2.add(storageManager);
        send(wfController, tempMessage2);

        stopWordManager.join();
        storageManager.join();
        wfController.join();

    }
}

abstract class ActiveWFObject extends Thread {
    protected String name;
    protected ArrayBlockingQueue<List<Object>> queue;
    protected Boolean stopMe;

    public ActiveWFObject() {
        super();
        this.name = this.getName();
        this.queue = new ArrayBlockingQueue<List<Object>>(100);
        this.stopMe = false;
        this.start();
    }

    @Override
    public void run() {
        while(!stopMe) {
            List<Object> message = null;
            try {
                message = this.queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.dispatch(message);
            if(message.get(0).toString().equals("die")) {
                this.stopMe = true;
            }
        }

    }

    protected void dispatch(List<Object> message) {
        return;
    }

    public static void send(ActiveWFObject objectMethod, List<Object> message) {
        try {
            objectMethod.queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class DataStorageManager extends ActiveWFObject {
    protected List<String> data = new ArrayList<>();
    protected StopWordManager stopWordManager;

    @Override
    protected void dispatch(List<Object> message) {
        if(message.get(0).toString().equals("init")) {
            this.init(message.subList(1, message.size()));
        } else if(message.get(0).toString().equals("send_word_freqs")) {
            this.processWords(message.subList(1, message.size()));
        } else {
            send(stopWordManager, message);
        }
    }

    protected void init(List<Object> message) {
        String pathToFile = message.get(0).toString();
        this.stopWordManager = (StopWordManager) message.get(1);
        List<String> wordList = new ArrayList<>();
        try {
            BufferedReader articleReader = new BufferedReader(new FileReader(pathToFile));
            String strLine = articleReader.readLine();
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
        this.data = wordList;
    }

    protected void processWords(List<Object> message) {
        ActiveWFObject recipient = (ActiveWFObject) message.get(0);
        List<String> words = this.data;
        for(String w : words) {
            List<Object> tempMessage = new ArrayList<>();
            tempMessage.add("filter");
            tempMessage.add(w);
            send(this.stopWordManager, tempMessage);
        }
        List<Object> tempMessage = new ArrayList<>();
        tempMessage.add("top25");
        tempMessage.add(recipient);
        send(this.stopWordManager, tempMessage);
    }
}

class StopWordManager extends ActiveWFObject {
    protected List<String> stopWords;
   // protected WordFrequencyManager wordFreqsManager;
    protected Map<String, Integer> wordFreqs = new HashMap<>();

    @Override
    protected void dispatch(List<Object> message) {
        if(message.get(0).toString().equals("init")) {
            this.init(message.subList(1, message.size()));
        } else if(message.get(0).toString().equals("filter")) {
            this.filter(message.subList(1, message.size()));
        } else if(message.get(0).toString().equals("top25")){
            //send(this.wordFreqsManager, message);
            this.top25(message.subList(1, message.size()));
        }
    }

    protected void init(List<Object> message) {
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
            stopWordsArray = stopWordsLine.toLowerCase().split(",");
        }
        List<String> stopWordList = new ArrayList<>(Arrays.asList(stopWordsArray));
        this.stopWords = stopWordList;
       // this.wordFreqsManager = (WordFrequencyManager) message.get(0);
    }

    protected void filter(List<Object> message) {
        String word = message.get(0).toString();
        if(!word.trim().equals("") && word.length() >= 2 && !stopWords.contains(word)) {
            if(this.wordFreqs.containsKey(word)) {
                this.wordFreqs.put(word, this.wordFreqs.get(word)+1);
            } else {
                this.wordFreqs.put(word, 1);
            }
        }
    }

    protected void top25(List<Object> message) {
        ActiveWFObject recipient = (ActiveWFObject) message.get(0);
        List<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(this.wordFreqs.entrySet());
        Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
        List<Object> tempMessage = new ArrayList<>();
        tempMessage.add("top25");
        tempMessage.add(sortedMapList);
        send(recipient, tempMessage);
    }
}

class WordFrequencyController extends ActiveWFObject {
    protected DataStorageManager storageManager;

    @Override
    protected void dispatch(List<Object> message) {
        if(message.get(0).toString().equals("run")) {
            this.runCurrent(message.subList(1, message.size()));
        } else if(message.get(0).toString().equals("top25")) {
            this.display(message.subList(1, message.size()));
        } else {
            try {
                throw new Exception("Message not understood " + message.get(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void runCurrent(List<Object> message) {
        this.storageManager = (DataStorageManager) message.get(0);
        List<Object> tempMessage = new ArrayList<>();
        tempMessage.add("send_word_freqs");
        tempMessage.add(this);
        send(this.storageManager, tempMessage);
    }

    protected void display(List<Object> message) {
        List<Map.Entry<String, Integer>> sortedMapList = (List<Map.Entry<String, Integer>>) message.get(0);
        for(Map.Entry<String, Integer> entry : sortedMapList.subList(0, 25)) {
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
        }
        List<Object> tempMessage = new ArrayList<>();
        tempMessage.add("die");
        send(this.storageManager, tempMessage);
        this.stopMe = true;
    }


}
