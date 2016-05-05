package il.ac.technion.cs.ssdl.lola.bunnies;

import java.util.List;

/**
 * A class for any keyword instance in Lola (e.g., @Find). Should probably be
 * created by querying some KeywordType database.
 */
public abstract class ConcreteKeyword implements Keyword, ConcreteBunny {

    private final int line;
    private final int column;
    private final String value;

    public ConcreteKeyword(String value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getText() {
        return value;
    }
}
