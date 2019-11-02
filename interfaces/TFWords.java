package interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface TFWords {
    List<String> extractWords(String path) throws IOException;
}
