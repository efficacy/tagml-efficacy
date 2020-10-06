package example;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.efsol.tagml.Parser;
import com.efsol.tagml.markup.Markup;
import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.DocumentFilter;
import com.efsol.tagml.model.dag.DagFactory;

public class TagmlExample {
    public static void main(String[] args) throws IOException {
        // use a Directed Acyclic Graph (DAG) model for the in-memory document
        DocumentFactory factory = new DagFactory();

        // Create a parser to create the specified model
        Parser parser = new Parser(factory);

        // parse an external file
        Reader reader = new FileReader("testdata/example.tagml");
        Document document = parser.parse(reader);
        reader.close();

        /*
         * Enable the statement below to see a diagnostic sketch of the document structure.
         * Note that two automatic layers are created during parsing:
         * null - this is the default "base" layer used for text not on an explicit layer
         * $ - this is a "global" layer which includes all text regardless of layer
         */
//      System.out.println("parsed to " + document);

        // An example filter predicate, this one only cares about text on layer "f"
        DocumentFilter filter = new DocumentFilter() {
            @Override
            public boolean accept(Chunk chunk) {
                return chunk.isOnLayer("f");
            }
        };

        // follow the graph to "spool" out the text which matches the filter predicate, in the original order
        String plain = Markup.spoolAsText(document, filter);
        System.out.println(plain);

        // follow the graph to "spool" out the marked-up text which matches the filter predicate, in the original order
        // Note that where multiple open or close tags were originally adjacent, their order may not be identical in this
        // output. This should not be semantically significant.
        String markup = Markup.spoolAsMarkup(document, filter);
        System.out.println(markup);
    }
}
