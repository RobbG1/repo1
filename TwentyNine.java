import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwentyNine extends SpaceClass{
    public static List<String> getStopWords() {
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

        return stopWordList;
    }

    public static List<String> getNovelWords(String path) {
        List<String> wordList = new ArrayList<>();
        try {
            BufferedReader articleReader = new BufferedReader(new FileReader(path));
            String strLine = articleReader.readLine();
            while(strLine != null) {
                if(!strLine.trim().equals("")) {
                    //wordList.add(strLine);
                    String[] wordsOfTheLine = strLine.replaceAll("[\\W_]+", " ").toLowerCase().split(" ");
                    wordList.addAll(Arrays.asList(wordsOfTheLine));
                }
                strLine = articleReader.readLine();
            }
            articleReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }


    public static void main(String args[]) throws InterruptedException {
        stopWords = getStopWords();
        //start to populate
        List<String> novelWords = getNovelWords(args[0]);
        for(String word : novelWords) {
            if(!word.trim().equals("") && word.length() >= 2) {
                try {
                    wordSpace.put(word.trim());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        ProcessWordsC pwc = new ProcessWordsC();
        ThreadedObject[] workers1 = new ThreadedObject[5];
        // create 5 threads to process words and launch them
        for(int i = 4; i >= 0; i--) {
            workers1[i] = new ThreadedObject(pwc);
        }
        // wait for the workers to finish
        for(ThreadedObject t : workers1) {
            t.join();
        }

        CountFreqsC cfc = new CountFreqsC();
        ThreadedObject2[] workers2 = new ThreadedObject2[5];
        // create 5 threads to count frequencies and launch them
        for(int i = 4; i >= 0; i--) {
            workers2[i] = new ThreadedObject2(cfc);
        }
        // wait for the workers to finish
        for(ThreadedObject2 t : workers2) {
            t.join();
        }

        Map<String, Integer> wordFreqs = new HashMap<>();
        wordFreqs = freqSpace.take();
        if(null != wordFreqs) {
            ArrayList<Map.Entry<String, Integer>> sortedMapList = new ArrayList<>(wordFreqs.entrySet());
            Collections.sort(sortedMapList, (o1, o2) -> o2.getValue() - o1.getValue());
            for(Map.Entry<String, Integer> entry : sortedMapList.subList(0, 25)) {
                System.out.println(entry.getKey() + "  -  " + entry.getValue());
            }
        } else {
            System.out.println("Error: wordFreqs space is null!");
        }

    }
}

abstract class SpaceClass {
    static ArrayBlockingQueue<String> wordSpace = new ArrayBlockingQueue<>(130000);
    static ArrayBlockingQueue<Map<String, Integer>> freqSpace = new ArrayBlockingQueue<>(130000);
    static List<String> stopWords = new ArrayList<>();
}

class ThreadedObject extends Thread {
    ProcessWordsC localPwc;
    public ThreadedObject(ProcessWordsC paramPwc) {
        super();
        this.localPwc = paramPwc;
        start();
    }

    @Override
    public void run() {
        try {
            localPwc.processWords();
            // Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.println("Error when running processWords method.");
            e.printStackTrace();

        }
    }
}

class ProcessWordsC extends SpaceClass{
    public void processWords() throws InterruptedException {
        Map<String, Integer> wordFreqs = new HashMap<>();
        while(true) {
            try {
                String word = wordSpace.poll(1, TimeUnit.SECONDS);
                if(null == word) {
                    break;
                }
                if(!stopWords.contains(word)) {
                    if(wordFreqs.containsKey(word)) {
                        wordFreqs.put(word, wordFreqs.get(word) + 1);
                    } else {
                        wordFreqs.put(word, 1);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        freqSpace.put(wordFreqs);
    }
}

class ThreadedObject2 extends Thread {
    CountFreqsC localCfc;
    public ThreadedObject2(CountFreqsC paramCfc) {
        super();
        this.localCfc = paramCfc;
        start();
    }

    @Override
    public void run() {
        try {
            localCfc.countFreqs();
            // Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.println("Error when running processWords method.");
            e.printStackTrace();

        }
    }
}

class CountFreqsC extends SpaceClass{
    public void countFreqs() throws InterruptedException {
        // merge the partial frequency results
        Map<String, Integer> wordFreqs = new HashMap<>();
        while(true) {
            Integer count = 0;
            Map<String, Integer> freqs = freqSpace.poll(1, TimeUnit.SECONDS);
            if(null == freqs) {
                break;
            }
            for(String k : freqs.keySet()) {
                if(wordFreqs.containsKey(k)) {
                    count = freqs.get(k) + wordFreqs.get(k);
                } else {
                    count = freqs.get(k);
                }
                wordFreqs.put(k, count);
            }
        }
        freqSpace.put(wordFreqs);
    }
}
