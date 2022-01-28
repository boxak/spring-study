package template.callback;

import java.io.BufferedReader;
import java.io.IOException;

public interface BufferedReaderCallback {
    Integer workWithReader(BufferedReader br) throws IOException;
}
