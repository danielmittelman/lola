package il.ac.technion.cs.ssdl.lola.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class TokenizerTest {

	@Before
	public void setUp() {

	}

	@Test
	public void test1() throws IOException, ParseException {
		Reader stream = new StringReader("first second third");
		Tokenizer tokenizer = new Tokenizer(stream);
		assertEquals("first", tokenizer.next().getText());
		assertEquals(" ", tokenizer.next().getText());
		assertEquals("second", tokenizer.next().getText());
		assertEquals(" ", tokenizer.next().getText());
		assertEquals("third", tokenizer.next().getText());
		assertEquals(false, tokenizer.hasNext());

	}
}