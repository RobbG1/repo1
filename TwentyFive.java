import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwentyFive {
    public static void createDBSchema(Connection conn) {

        try (Statement stmt = conn.createStatement()) {
            // create new tables
            stmt.addBatch("CREATE TABLE documents (id INTEGER PRIMARY KEY AUTOINCREMENT, name)");
            stmt.addBatch("CREATE TABLE words (id, doc_id, value)");
            stmt.addBatch("CREATE TABLE characters (id, word_id, value)");
            stmt.executeBatch();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loadFileIntoDatabase(String pathToFile, Connection conn) {
        List<String> words = new ArrayList<>();
        class FileLoadInnerClass {
            public List<String> extractWords(String pathToFile) {
                List<String> validWords = new ArrayList<>();
                String stopWordsLine = null;
                String[] stopWordsArray = null;
                List<String> wordList = new ArrayList<>();
                List<String> stopWordList;
                try{
                    // Takes a list of words and returns a copy with all stop words removed
                    BufferedReader stopWordsReader = new BufferedReader(new FileReader("../stop_words.txt"));
                    stopWordsLine = stopWordsReader.readLine();
                    stopWordsReader.close();

                    BufferedReader articleReader = new BufferedReader(new FileReader(pathToFile));
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

                if(stopWordsLine != null && !stopWordsLine.trim().equals("")) {
                    stopWordsArray = stopWordsLine.toLowerCase().split(",");
                }
                stopWordList = new ArrayList<>(Arrays.asList(stopWordsArray));

                for(String word : wordList) {
                    if(!word.trim().equals("") && word.length() >= 2 && !stopWordList.contains(word)) {
                        validWords.add(word.trim());
                    }
                }

                return validWords;
            }
        }
        FileLoadInnerClass fic = new FileLoadInnerClass();
        words = fic.extractWords(pathToFile);
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO documents (name) VALUES (?)");
            pstmt.setString(1, pathToFile);
            pstmt.executeUpdate();

            PreparedStatement pstmt2 = conn.prepareStatement("SELECT id from documents WHERE name=?");
            pstmt2.setString(1, pathToFile);
            ResultSet rs = pstmt2.executeQuery();
            Integer docId = rs.getInt("id");

            PreparedStatement pstmt3 = conn.prepareStatement("SELECT MAX(id) FROM words");
            ResultSet rs2 = pstmt3.executeQuery();
            Integer row[] = new Integer[5];
            row[0] = rs2.getInt(1);
            Integer wordId = row[0];
            if(null == wordId) {
                wordId = 0;
            }

            conn.setAutoCommit(false);
            PreparedStatement pstmt4 = conn.prepareStatement("INSERT INTO words VALUES (?, ?, ?)");
            for(String w : words) {
                pstmt4.setInt(1, wordId);
                pstmt4.setInt(2, docId);
                pstmt4.setString(3, w);
                pstmt4.addBatch();
                Integer charId = 0;
                char[] wChars = w.toCharArray();
                PreparedStatement pstmt5 = conn.prepareStatement("INSERT INTO characters VALUES (?, ?, ?)");
                for(char achar : wChars) {
                    pstmt5.setInt(1, charId);
                    pstmt5.setInt(2, wordId);
                    pstmt5.setString(3, String.valueOf(achar));
                    pstmt5.addBatch();
                    charId += 1;
                }
                pstmt5.executeBatch();
                wordId += 1;
            }
            pstmt4.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dbName = "tftg.db";
        File file = new File (dbName);
        Boolean fileExisted = file.exists();
        String url = "jdbc:sqlite:" + dbName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!fileExisted) {
            createDBSchema(conn);
            //String pathToFile = "/Users/Robert/Desktop/Desktop/Courses/TestCase/pride-and-prejudice.txt";
            String pathToFile = args[0];
            loadFileIntoDatabase(pathToFile, conn);
        }

        try {
            PreparedStatement pstmt6 = conn.prepareStatement("SELECT value, COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC");
            ResultSet rs6 = pstmt6.executeQuery();
            int i = 0;
            while(rs6.next() && i<25) {
                System.out.println(rs6.getString(1) + " - " + rs6.getInt(2));
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}