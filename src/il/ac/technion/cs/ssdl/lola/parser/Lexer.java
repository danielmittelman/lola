package il.ac.technion.cs.ssdl.lola.parser;

import il.ac.technion.cs.ssdl.lola.bunnies.*;
import il.ac.technion.cs.ssdl.lola.regex.automaton.TokenType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.text.ParseException;
import java.util.List;

public class Lexer {

    private final List<KeywordType> keywordTypes;
    ColLinePeekableReader reader;
    private int parsePythonArgument;

    public Lexer(Reader stream, List<KeywordType> keywordTypes) {
        this.keywordTypes = keywordTypes;
        this.parsePythonArgument = KeywordType.PythonArgumentRequirement.None;
        //new FileReader(filename)
        this.reader = new ColLinePeekableReader(stream);
    }

    private boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }



    public ConcreteBunny read() throws IOException, ParseException {
        int i = reader.peek();
        if (i == -1) {
            return null;
        }

        int column = reader.column;
        int line = reader.line;

        if (parsePythonArgument == KeywordType.PythonArgumentRequirement.Required) {
            parsePythonArgument = KeywordType.PythonArgumentRequirement.None;
            throw new NotImplementedException();
        } else if ((char) i == '(' && parsePythonArgument == KeywordType.PythonArgumentRequirement.Optional) {
            parsePythonArgument = KeywordType.PythonArgumentRequirement.None;
            throw new NotImplementedException();
        } else if ((char) i == '@') {
            String name = getKeyword();
            for (KeywordType keywordType: keywordTypes) {
                if (keywordType.isOfThisType(name)) {
                    parsePythonArgument = keywordType.getPythonArgumentRequirement();
                    return keywordType.create(name, line, column);
                }
            }
            throw new ParseException("Error parsing at line " + line + " column " + column + ": invalid keyword.",
                    column);
        } else if (isLetter((char) i)) {
             return new ConcreteToken(TokenType.___identifier___, getIdentifier(), line, column);
        } else { // assume everything else is trivia
            return new ConcreteToken(TokenType.___trivia___, getTrivia(), line, column);
        }
    }

    private String getIdentifier() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = reader.peek(); i != -1 && isLetter((char) i); i = reader.peek()) {
            reader.read();
            builder.append((char) i);
        }
        return builder.toString();
    }

    private String getKeyword() throws IOException {
        return ((char)reader.read()) + getIdentifier();
    }

    private String getTrivia() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = reader.peek(); i != -1 && !isLetter((char) i) && (char) i != '@'; i = reader.peek()) {
            reader.read();
            builder.append((char) i);
        }
        return builder.toString();
    }




    static class ColLinePeekableReader extends Reader {
        PushbackReader br;
        int column;
        int line;

        public ColLinePeekableReader(Reader reader) {
            this.br = new PushbackReader(reader);
            this.column = 0;
            this.line = 0;
        }

        public int read(char[] cbuf, int off, int len) throws IOException {
            int s = this.br.read(cbuf, off, len);
            int j = 0;

            // update column and line
            while (j < s) {
                char c = (char) cbuf[j++];
                switch (c) {
                    case '\n':
                        line++;
                        column = 0;
                        break;
                    default:
                        column++;
                        break;
                }
            }

            return s;
        }

        public int read() throws IOException {
            int i = br.read();

            // update column and line
            if (i != -1) {
                char c = (char) i;
                switch (c) {
                    case '\n':
                        line++;
                        column = 0;
                        break;
                    default:
                        column++;
                        break;
                }
            }

            return i;
        }

        public int peek() throws IOException {
            int i = br.read();
            if (i != -1) {
                br.unread(i);
            }

            return i;
        }

        public void close() throws IOException {
            this.br.close();
        }
    }
}
