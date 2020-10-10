package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.dag.DagFactory;
import com.efsol.tagml.parser.ParseException;
import com.efsol.tagml.parser.Parser;

class InvalidTest {

    Document parse(String input) throws IOException {
        DocumentFactory factory = new DagFactory();
        Parser parser = new Parser(factory);
        StringReader reader = new StringReader(input);
        Document document = parser.parse(reader);
//      System.out.println("parsed(" + input + ") to " + document);
        return document;
    }

    void assertInvalid(String text, String messagePattern) throws IOException {
        try {
            parse(text);
            fail("invalid text should throw: " + text);
        } catch (ParseException e) {
            String message = e.getMessage();
            if (!message.matches(".*" + messagePattern + ".*")) {
                fail("expected message matching '" + messagePattern + "' was '" + message + "'");
            }
            // otherwise, everything is fine
        }
    }

    void assertInvalid(String text) throws IOException {
        try {
            parse(text);
            fail("invalid text should throw: " + text);
        } catch (ParseException e) {
            // otherwise, everything is fine
        }
    }

    @Test
    void testInvalidCharacter() throws IOException {
        assertInvalid("[-a>", "unexpected character - in tag name");
        assertInvalid("[a->", "unexpected character - in tag name");
        assertInvalid("[?a>", "unexpected character \\? in tag name");
        assertInvalid("[a?>", "unexpected character \\? in tag name");
        assertInvalid("[%a>", "unexpected character % in tag name");
        assertInvalid("[a%>", "unexpected character % in tag name");
        assertInvalid("<a<", "unexpected character < in tag name");
        assertInvalid("<a>", "unexpected character > in tag name");
        assertInvalid("<+a]", "unexpected character \\+ in tag name");
        assertInvalid("<a+]", "unexpected character \\+ in tag name");

        assertInvalid("[a|->", "unexpected character - in layer name");
        assertInvalid("[a|?>", "unexpected character \\? in layer name");
        assertInvalid("[a|%>", "unexpected character % in layer name");
        assertInvalid("<a|>", "unexpected character > in layer name");
    }

    @Test
    void testInvalidMarkup() throws IOException {
        assertInvalid("[>hello<a]", "open tag name cannot be empty");
        assertInvalid("[a>hello<]", "close tag name cannot be empty");

        assertInvalid("[a|>hello<a|x]", "layer name cannot be empty");
        assertInvalid("[a|x>hello<a|]", "layer name cannot be empty");
        assertInvalid("[a|,y>hello<a|x,y]", "layer name cannot be empty");
        assertInvalid("[a|x,y>hello<a|,y]", "layer name cannot be empty");
        assertInvalid("[a|x,>hello<a|x,y]", "layer name cannot be empty");
        assertInvalid("[a|x,y>hello<a|x,]", "layer name cannot be empty");
    }

    @Test
    void testIncompleteMarkup() throws IOException {
        assertInvalid("hello[a", "open tag never completed");
        assertInvalid("hello[a|b", "open tag never completed");
        assertInvalid("[a>hello<a", "close tag never completed");
        assertInvalid("[a>hello<a|b", "close tag never completed");

        assertInvalid("[!hello", "comment never completed");
        assertInvalid("[!hello!", "comment never completed");
        assertInvalid("[!hello!bc", "comment must end with !]");
        assertInvalid("[!]", "comment never completed");
    }

    @Test
    void testInvalidStructure() throws IOException {
        assertInvalid("hello<b]", "unopened tag");
        assertInvalid("[a>hello", "unclosed tag");

        assertInvalid("[a|x>hello<b|x]", "unopened tag");
        assertInvalid("[a|x>hello<a|b]", "unopened tag");
        assertInvalid("[a|x>[a|y>hello<a|y]", "unclosed tag");
        assertInvalid("[a|x,y>hello<a|y]", "unclosed tag");

        assertInvalid("[a|x>hello<a]", "unopened tag"); // official TAGML seems to allow this ?
    }

    @Test
    void testInvalidComments() throws IOException {
        assertInvalid("[a!hello!]", "unexpected character ! in tag name");
        assertInvalid("[!hello! ]", "comment must end with !]");
        assertInvalid("[!hello!a]", "comment must end with !]");
        assertInvalid("[!hello!>", "comment must end with !]");
        assertInvalid("<!hello!]", "unexpected character ! in tag name");

//        assertInvalid("[ !hello!]", "X"); // this may be valid depending on interpretation of whitespace rules?
    }

}
