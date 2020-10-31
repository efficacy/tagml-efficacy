package test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.dag.DotExport;

import export.Export;
import test.helper.TestUtils;

class ExportTest {
    private void generate(String text, String filename) throws IOException {
        Document doc = TestUtils.parse(text);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Export export = new DotExport(doc);
        export.export(buf, text);
        String ret = buf.toString();
        assertFalse(ret.isEmpty());
        System.out.println(ret);
        Files.writeString(Path.of(filename), ret);
    }

    @Test
    void testWithoutRoot() throws IOException {
        generate("Stuart[A|+f>John[B>Paul<A|f]George<B]Ringo", "testout/et1.dot");
    }

    @Test
    void testWithRoot() throws IOException {
        generate("[TAGML>Stuart[A|+f>John[B>Paul<A|f]George<B]Ringo<TAGML]", "testout/et2.dot");
    }

    @Test
    void testDoubleOverlap() throws IOException {
        generate("[A>[b>[c>[d>hello<b]<c]<d]<A]", "testout/et3.dot");
    }

}
