The Efficacy TAGML Parser
=========================

About this Project
------------------
This code is a fresh attempt at creating a performant Java parser and in-memory model for the [TAGML markup language](https://www.balisage.net/Proceedings/vol21/print/HaentjensDekker01/BalisageVol21-HaentjensDekker01.html). It was started in 2020 following discussions with Ronald Haentjens Dekker and Bram Buitendijk as part of the [The 20th ACM Symposium on Document Engineering](https://doceng.org/doceng2020) in October 2020. As of now it mainly exists as a test bed to explore the both the performance of parsers and the usage and semantics of data marked up in this way.

Code Status
-----------
For detailed project status see [TODO.txt](TODO.txt)

Usage
--------------
The best place to look for usage examples is the unit test suite. However, usage of this parser is intended to be relatively simple.

To parse a text document, simply create a `com.efsol.tagml.TagmlParser` object then pass a Reader to the `parse(Reader input)` method. If the input text is well-formed, the `parse` method will return a `com.efsol.tagml.TagmlDocument` object. If there are parsing errors, the code will throw a `com.efsol.tagml.ParseException` with details of the error and the location it occurred in the input stream.

The resulting document consists of a sequence of `Node`s, each of which has text (which may be empty) and an optional set of layers, each of which contains one or more tags indicating the markup for this text chunk.

So for example, the TAGML text:

```
Stuart,[A|+f>John,[B>Paul,<A|f]George,<B]Ringo
```
Would result in five nodes:
`Stuart,` with no layers or tags
`John,` with a tag `A` on a layer `f`
`Paul,` with a tag `A` on a layer `f` and a tag `B` on the default layer
`George,` with a tag `B` on the default layer
`Ringo` with not layers or tags

Access to the nodes which form a document is currently provided using the Visitor pattern, allowing nodes to be included or excluded into a subset document.

For example, selecting just the text on layer `f` output as marked up text would produce the following:

```
[A|f>John,Paul,<A|f]
```

Example Code
------------

```Java
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
		reader.close();

		DocumentFilter filter = new DocumentFilter() {
			@Override
			public boolean accept(Node node) {
				return node.isOnLayer("f");
			}
		};

		String plain = document.spoolAsText(filter);
		System.out.println(plain);

		String markup = document.spoolAsMarkup(filter);
		System.out.println(markup);
	}
}
```