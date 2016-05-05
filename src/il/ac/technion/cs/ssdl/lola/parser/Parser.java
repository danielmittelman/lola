package il.ac.technion.cs.ssdl.lola.parser;

import il.ac.technion.cs.ssdl.lola.bunnies.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.Stack;

public class Parser {
    Lexer lexer;
    Stack<Keyword> stack;

    public Parser(Reader stream, List<KeywordType> keywordTypes) {
        lexer = new Lexer(stream, keywordTypes);
        stack = new Stack<Keyword>();
    }

    public static Parser fromString(String code, List<KeywordType> keywordTypes) {
       return new Parser(new StringReader(code), keywordTypes);
    }

    /**
     *
     *
     * @param base use a new {@link GhostKeyword} for this.
     * @throws IOException
     */
    public void roll(Keyword base) throws IOException, ParseException {
        stack.push(base);

        while (true) {
            Bunny b = lexer.read();
            if (b == null) {
                break;
            } else if (b instanceof Keyword) {
                stack.push((Keyword)b);
            } else {
                percolate(b);
            }
        }

        percolate();
    }

    /**
     * empties the stack.
     */
    private void percolate() {
        while (!stack.isEmpty()) {
            Keyword k = stack.pop();
            k.mature();
            percolate(k);
        }
    }

    private void percolate(Bunny _b) {
        while (!stack.isEmpty()) {
            // A ghost bunny may only be the first keyword on the stack. Therefore, it cannot reach here.
            assert (_b instanceof ConcreteBunny);
            ConcreteBunny b = (ConcreteBunny)_b;

            Keyword k = stack.peek();
            if (k.accepts(b)) {
                k.append(b);
                return;
            } else {
                stack.pop();
                k.mature();
                percolate(k);
            }
        }
    }
}
