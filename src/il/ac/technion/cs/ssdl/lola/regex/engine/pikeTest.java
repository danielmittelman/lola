package il.ac.technion.cs.ssdl.lola.regex.engine;

import il.ac.technion.cs.ssdl.lola.regex.Parser;
import il.ac.technion.cs.ssdl.lola.regex.automaton.Nfa;
import il.ac.technion.cs.ssdl.lola.bunnies.Token;
import il.ac.technion.cs.ssdl.lola.regex.automaton.TokenType;
import org.junit.Test;

import java.util.HashSet;

public class pikeTest {
    @Test
    public void test() {

        HashSet<Token> input_characters_set = new HashSet<Token>();
        Nfa nondeterministic = new Nfa();
        Parser parser = new Parser(input_characters_set);

        nondeterministic = parser.create_nfa("a", nondeterministic);

        pike p = new pike();
        p.init_run(nondeterministic, 0);

        // actually, should use ConcreteToken, but never mind...
        Token t  = new Token(TokenType.___identifier___, "a");

        p.step(t, 0);

        int a = 5;
    }
}