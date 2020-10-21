package export;

import java.io.IOException;
import java.io.OutputStream;

public interface Export {
    void export(OutputStream out, String comment) throws IOException;

    default void export(OutputStream out) throws IOException {
        export(out, null);
    }
}
