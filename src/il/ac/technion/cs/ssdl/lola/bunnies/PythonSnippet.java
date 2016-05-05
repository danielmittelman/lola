package il.ac.technion.cs.ssdl.lola.bunnies;

/**
 * For now, we do not distinguish different Python snippets.
 */
public class PythonSnippet implements ConcreteBunny {
    String value;
    private final int line;
    private final int column;

    public PythonSnippet(String value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    public String getText() {
        return value;
    }
}
