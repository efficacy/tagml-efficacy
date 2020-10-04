package example;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.efsol.tagml.DocumentFilter;
import com.efsol.tagml.Node;
import com.efsol.tagml.TagmlDocument;
import com.efsol.tagml.TagmlParser;

public class TagmlExample {
	public static void main(String[] args) throws IOException {
		TagmlParser parser = new TagmlParser();
		Reader reader = new FileReader("testdata/example.tagml");
		TagmlDocument document = parser.parse(reader);

		String result = document.spoolAsText(new DocumentFilter() {
			@Override
			public boolean accept(Node node) {
				return node.getLayers().containsKey("f");
			}
		});
		System.out.println(result);
		reader.close();
	}
}
