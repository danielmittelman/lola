package il.ac.technion.cs.ssdl.lola.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import il.ac.technion.cs.ssdl.lola.bunnies.ConcreteBunny;
import il.ac.technion.cs.ssdl.lola.bunnies.KeywordType;
import il.ac.technion.cs.ssdl.lola.bunnies.keywords.FindKeywordType;

public class LexerTest {
    private List<KeywordType> keywordTypes;

    @Before
    public void setUp() {
        keywordTypes = new ArrayList<KeywordType>();
        keywordTypes.add(new FindKeywordType());
    }

    @Test
    public void test1() throws IOException, ParseException {
        Reader stream = new StringReader("aaa");
        Lexer l = new Lexer(stream, keywordTypes);
        assertEquals("aaa", l.read().getText());
        assertEquals(null, l.read());
        assertEquals(null, l.read());
        assertEquals(null, l.read());
    }

    @Test
    public void test2() throws IOException, ParseException {
        Reader stream = new StringReader("aaa@Find ");
        Lexer l = new Lexer(stream, keywordTypes);
        assertEquals("aaa", l.read().getText());
        final ConcreteBunny b = l.read();
        assertEquals("@Find", b.getText());
        assertEquals(0, b.getLine());
        assertEquals(3, b.getColumn());
        assertEquals(" ", l.read().getText());
        assertEquals(null, l.read());
    }
    
    @Test
    public void testLines() throws IOException, ParseException {
        Reader stream = new StringReader("first@replace \nthird fourth");
        Lexer l = new Lexer(stream, keywordTypes);
        assertEquals("first", l.read().getText());
        final ConcreteBunny b = l.read();
        assertEquals("@replace", b.getText());
        assertEquals(0, b.getLine());
        assertEquals(5, b.getColumn());
        assertEquals(" \n", l.read().getText());
        final ConcreteBunny b2 = l.read();
        assertEquals("third", b2.getText());
        assertEquals(1, b2.getLine());
        assertEquals(0, b2.getColumn());
        assertEquals(" ", l.read().getText());
        assertEquals("fourth", l.read().getText());

    }
}