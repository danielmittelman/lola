package il.ac.technion.cs.ssdl.lola.parser;

import il.ac.technion.cs.ssdl.lola.bunnies.ConcreteBunny;
import il.ac.technion.cs.ssdl.lola.bunnies.GhostKeyword;
import il.ac.technion.cs.ssdl.lola.bunnies.Keyword;
import il.ac.technion.cs.ssdl.lola.bunnies.KeywordType;
import il.ac.technion.cs.ssdl.lola.bunnies.keywords.FindKeywordType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ParserTest {
    private static class TestGhostKeyword implements Keyword {
        int i = 0;
        String[] tokens;

        public TestGhostKeyword(String[] tokens) {
            this.tokens = tokens;
        }

        public String getText() {
            return "<TestGhostKeyword>";
        }

        public boolean accepts(ConcreteBunny bunny) {
            return true;
        }

        public void append(ConcreteBunny bunny) {

            final String token = tokens[i++];
            if (token.startsWith("@")) {
                assertTrue(bunny instanceof Keyword);
            }
            assertEquals(token, bunny.getText());
        }

        public void mature() {
            assertEquals(i, tokens.length);
        }
    }

    private List<KeywordType> keywordTypes;

    @Before
    public void setUp() {
        keywordTypes = new ArrayList<KeywordType>();
        keywordTypes.add(new FindKeywordType());
    }

    @Test
    public void test1() throws Exception {
        String[] tokens = {
            "hello"
        };

        Parser parser = Parser.fromString("hello", keywordTypes);
        parser.roll(new TestGhostKeyword(tokens));
    }

    @Test
    public void test2() throws Exception {
        String[] tokens = {
                "@Find"
        };

        Parser parser = Parser.fromString("@Find", keywordTypes);
        parser.roll(new TestGhostKeyword(tokens));
    }

    @Test
    public void test3() throws Exception {
        String[] tokens = {
                "@Find"
        };

        Parser parser = Parser.fromString("@Find aa", keywordTypes);
        parser.roll(new TestGhostKeyword(tokens));
    }

    @Test
    public void test4() throws Exception {
        String[] tokens = {
                "@Find","@Find"
        };

        Parser parser = Parser.fromString("@Find aa\n@Find", keywordTypes);
        parser.roll(new TestGhostKeyword(tokens));
    }

    @Test
    public void test5() throws Exception {
        String[] tokens = {
                "@Find"
        };

        Parser parser = Parser.fromString("@Find aa@Find", keywordTypes);
        parser.roll(new TestGhostKeyword(tokens));
    }
}