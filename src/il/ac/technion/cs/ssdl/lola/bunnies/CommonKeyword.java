package il.ac.technion.cs.ssdl.lola.bunnies;


import java.util.ArrayList;
import java.util.List;

public abstract class CommonKeyword extends ConcreteKeyword {
    static protected class State {
        static public final int PythonArgument = 0;
        static public final int LolaArgument = 1;
        static public final int Elaborators = 2;
        static public final int End = 3;
    }


    protected final KeywordType type;
    protected PythonSnippet pythonArgument;
    protected List<Bunny> lolaArgument;
    protected List<Elaborator> elborators;
    protected int state;

    protected CommonKeyword(KeywordType type, String value, int line, int column) {
        super(value, line, column);
        this.type = type;
        pythonArgument = null;
        lolaArgument = new ArrayList<Bunny>();
        elborators = new ArrayList<Elaborator>();

        state = State.PythonArgument;
    }

    public boolean acceptsPythonSnippet(PythonSnippet snippet) {
        return true;
    }


    public boolean acceptsLolaArgumentObject(ConcreteBunny bunny) {
        return true;
    }

    public boolean acceptsElaborator(Keyword keyword) {
        return true;
    }

    public boolean accepts(ConcreteBunny bunny) {
        if (state == State.PythonArgument) {
            if (type.getPythonArgumentRequirement() == KeywordType.PythonArgumentRequirement.None) {
                state = State.LolaArgument;
            } else if (!(bunny instanceof PythonSnippet)){
                state = State.LolaArgument;
            } else if (!acceptsPythonSnippet((PythonSnippet)bunny)) {
                state = State.LolaArgument;
            } else {
                return true;
            }
        }

        if (state == State.LolaArgument) {
            assert (bunny.getLine() >= getLine());

            if (!(bunny instanceof Constructor) && !(bunny instanceof Token)) {
                state = State.Elaborators;
            } else if (bunny.getColumn() <= getColumn()) {
                state = State.Elaborators;
            } else if (!acceptsLolaArgumentObject(bunny)) {
                state = State.Elaborators;
            } else {
                return true;
            }
        }

        if (state == State.Elaborators) {
            if (!(bunny instanceof Elaborator)) {
                state = State.End;
            } else if (bunny.getColumn() < getColumn()) {
                state = State.End;
            } else if (!acceptsElaborator((Elaborator) bunny)) {
                state = State.End;
            } else {
                return true;
            }
        }

        return false;
    }

    public void append(ConcreteBunny bunny) {
        if (state == State.PythonArgument) {
            pythonArgument = (PythonSnippet) bunny;
            state = State.LolaArgument;
            return;
        }

        if (state == State.LolaArgument) {
            lolaArgument.add(bunny);
            return;
        }

        if (state == State.Elaborators) {
            elborators.add((Elaborator)bunny);
            return;
        }

        throw new IllegalStateException("A bunny was appended in an illegal state.");
    }

    /**
     * Does nothing.
     * An implementing class may throw a parsing error, e.g.,
     * "Keyword '@Switch' at line " + line +" column " + column + " must consist of at least one '@case' elaborator,
     *  but none were specified".
     */
    public void mature() {
        /* do nothing */
    }
}
