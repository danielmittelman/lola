package il.ac.technion.cs.ssdl.lola.regex.automaton;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!NOT ALL CLASS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class TokenType {
    public static final int Invalid = 0;
    public static final int Keyword = 1;
    public static final int Epsilon = 2;
    public static final int AnyToken = 4;
    public static final int ___identifier___ = 5;
    public static final int ___opencurly___ = 6;
    public static final int ___closecurly___ = 7;
    public static final int ___trivia___ = 10;


    public static final int opencurly_balanced = 8;
    public static final int closecurly_balanced = 9;



    public static boolean is_default_balanced(int type){
        if (type == TokenType.___opencurly___ || type == TokenType.___closecurly___)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean is_require_balanced(int type)
    {
        if (type == TokenType.opencurly_balanced || type == TokenType.closecurly_balanced)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
