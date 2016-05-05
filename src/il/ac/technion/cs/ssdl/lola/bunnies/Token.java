package il.ac.technion.cs.ssdl.lola.bunnies;

import il.ac.technion.cs.ssdl.lola.regex.automaton.TokenType;

/**
 * A class representing a token to be matched.
 * This value should be passed by value. Any reference passing should be handled
 * in the class (that is, if a value should be passed by reference, it should be
 * a pointer declared in this class).
 */
public class Token {
    /// <summary>
    /// A constant variable representing epsilon (for epsilon transition in
    /// non-deterministic finite automaton).
    /// </summary>
    public static final Token epsilon = new Token(TokenType.Epsilon);

    /// <summary>
    /// A constant variable representing any token (excluding epsilon).
    /// </summary>
    public static final Token any = new Token(TokenType.AnyToken);

    /// <summary>
    /// A constructor. For epsilon transitions (such as in NFA states),
    /// use token::epsilon.
    /// </summary>
    /// <remarks>
    /// An empty string ("") indicates that the token should be matched
    /// only by the type, and not by the value string.
    /// </remarks>
    /// <param name="type">The token type.</param>
    /// <param name="value">The string content.</param>
    /// <seealso cref="token::epsilon"/>
    public Token(int type) {
        this(type, "");
    }

    public Token(int type, String value) {
        this.type = type;
        this.value = value;
    }

    /// <summary>
    /// Gets the string value of the token.
    /// </summary>
    /// <remarks>
    /// This method is not very useful, except for debug purposes.
    /// </remarks>
    /// <returns>The string value of the token</returns>
    public String get_value() {
        return value;
    }

    public int get_type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (type != token.type) return false;
        return value != null ? value.equals(token.value) : token.value == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }

    /// <summary>
    /// The string of the token.
    /// </summary>
    String value;

    /// <summary>
    /// This value indicates the type of the token (identifier, number, etc.)
    /// </summary>
    int type;

}
