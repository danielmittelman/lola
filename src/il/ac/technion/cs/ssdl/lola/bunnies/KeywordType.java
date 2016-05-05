package il.ac.technion.cs.ssdl.lola.bunnies;

public interface KeywordType {
    class PythonArgumentRequirement {
        static public final int None = 0;
        static public final int Required = 1;

        /**
         * An optional Python argument must be wrapped in parenthesis, rather than any other type (because of the way the
         * lexer is implemented).
         */
        static public final int Optional = 2;
    }

    /**
     * Is the string describing a keyword of this type?
     * E.g., "@Find" indeed describes the Find keyword type.
     * Note that the string includes the '@' character.
     * @param name
     * @return
     */
    boolean isOfThisType(String name);

    /**
     *
     * @return one of the options listed as constants in {@link PythonArgumentRequirement}.
     */
    int getPythonArgumentRequirement();

    /**
     *
     * @param name
     * @param line
     * @param column
     * @return Either a {@link Constructor} or an {@link Elaborator} instance.
     */
    ConcreteKeyword create(String name, int line, int column);
}
