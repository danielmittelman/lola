package il.ac.technion.cs.ssdl.lola.parser;

import il.ac.technion.cs.ssdl.lola.bunnies.Bunny;
import il.ac.technion.cs.ssdl.lola.bunnies.ConcreteBunny;
import il.ac.technion.cs.ssdl.lola.bunnies.KeywordType;
import il.ac.technion.cs.ssdl.lola.bunnies.keywords.FindKeywordType;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
}