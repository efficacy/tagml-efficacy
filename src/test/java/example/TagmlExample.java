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
		DocumentFactory factory = new DagFactory();
		Parser parser = new Parser(factory);
		Reader reader = new FileReader("testdata/example.tagml");
		Document document = parser.parse(reader);
//		System.out.println("parsed to " + document);

		DocumentFilter filter = new DocumentFilter() {
			@Override
			public boolean accept(Chunk chunk) {
				return chunk.isOnLayer("f");
			}
		};

		String plain = Markup.spoolAsText(document, filter);
		System.out.println(plain);

		String markup = Markup.spoolAsMarkup(document, filter);
		System.out.println(markup);
	}
}
