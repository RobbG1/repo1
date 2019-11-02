package interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TFFreqs {
    ArrayList<Map.Entry<String, Integer>> top25(List<String> words);
}
