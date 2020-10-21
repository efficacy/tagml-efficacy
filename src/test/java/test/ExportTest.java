package test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.model.Document;

import export.DotExport;
import export.Export;
import test.helper.TestUtils;

class ExportTest {
    @Test
    void testLayers() throws IOException {
        String text = "Stuart[A|+f>John[B>Paul<A|f]George<B]Ringo";
        Document doc = TestUtils.parse(text);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Export export = new DotExport(doc);
        export.export(buf, text);
        String ret = buf.toString();
        assertFalse(ret.isEmpty());
        System.out.println(ret);
        Files.writeString(Path.of("testout/et1.dot"), ret);
    }
}
