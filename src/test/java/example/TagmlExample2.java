package example;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import com.efsol.tagml.markup.Markup;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.DocumentFilter;
import com.efsol.tagml.model.dag.DagFactory;
import com.efsol.tagml.model.helper.SingleLayerFilter;
import com.efsol.tagml.parser.Parser;

public class TagmlExample2 {
    public static void main(String[] args) throws IOException {
        // use a Directed Acyclic Graph (DAG) model for the in-memory document
        DocumentFactory factory = new DagFactory();

        // Create a parser to create the specified model
        Parser parser = new Parser(factory);

        // parse an external file
        Reader reader = new FileReader("testdata/ts-aladin3.tagml", Charset.forName("UTF-8"));
        Document document = parser.parse(reader);
        reader.close();

        /*
         * Enable the statement below to see a diagnostic sketch of the document structure.
         * Note that two automatic layers are created during parsing:
         * null - this is the default "base" layer used for text not on an explicit layer
         * $ - this is a "global" layer which includes all text regardless of layer
         */
//        System.out.println("parsed to " + document);

        DocumentFilter t = new SingleLayerFilter("T");
        // follow the graph to "spool" out the text which matches the filter predicate, in the original order
        System.out.println(Markup.spoolAsText(document, t));

        // follow the graph to "spool" out the marked-up text which matches the filter predicate, in the original order
        // Note that where multiple open or close tags were originally adjacent, their order may not be identical in this
        // output. This should not be semantically significant.
        System.out.println(Markup.spoolAsMarkup(document, t));

        DocumentFilter m = new SingleLayerFilter("M");
        // follow the graph to "spool" out the text which matches the filter predicate, in the original order
        System.out.println(Markup.spoolAsText(document, m));

        // follow the graph to "spool" out the marked-up text which matches the filter predicate, in the original order
        // Note that where multiple open or close tags were originally adjacent, their order may not be identical in this
        // output. This should not be semantically significant.
        System.out.println(Markup.spoolAsMarkup(document, m));
    }
}
