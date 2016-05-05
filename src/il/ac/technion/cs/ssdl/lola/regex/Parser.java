package il.ac.technion.cs.ssdl.lola.regex;

import il.ac.technion.cs.ssdl.lola.utils.IntegerRef;
import il.ac.technion.cs.ssdl.lola.regex.automaton.Nfa;
import il.ac.technion.cs.ssdl.lola.bunnies.Token;
import il.ac.technion.cs.ssdl.lola.regex.automaton.TokenType;

import java.util.Set;
import java.util.Stack;

public class Parser
{
    /// <summary>
    /// The depots hold the
    /// </summary>
    public Set<Token> input_characters_set;

    /// <summary>
    /// Keeps track of state IDs, to assign.
    /// </summary>
    public IntegerRef next_state_id;

    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="input_characters_set"></param>
    public Parser(Set<Token> input_characters_set)
{
    operand_stack = new Stack<Nfa>();
    operator_stack = new Stack<Character>();

    this.input_characters_set = input_characters_set;
    this.next_state_id = new IntegerRef();
    this.next_state_id.val =0;
}

    /// <summary>
    /// Operand Stack
    /// If we use the Thompson's Algorithm then we realize
    /// that each operand is a NFA automaton on its own!
    /// </summary>
    public Stack<Nfa> operand_stack;

    /// <summary>
    /// Operator Stack
    /// Operators are simple characters like "*" & "|"
    /// </summary>
    public Stack<Character> operator_stack;

    /// <summary>
    /// Pops an element from the operand stack
    /// The return value is true if an element
    /// was popped successfully, otherwise it is
    /// false (syntax error).
    /// </summary>
    /// <returns>The pepped NFA.</returns>
    public Nfa pop()	{
        return  operand_stack.pop();
    }

    /// <summary>
    /// Checks is a specific character and operator.
    /// </summary>
    /// <param name="ch">The character to check.</param>
    /// <returns>True if is operator, false otherwise.</returns>
    public boolean is_operator(char ch){ return((ch == 42) || (ch == 124) || (ch == 40) || (ch == 41) || (ch == 8) || (ch == '?') || (ch == '+') || (ch == '~')); }

    /// <summary>
    /// Returns operator precedence.
    /// Returns true if precedence of left <= right.
    ///		Kleene's Closure	- highest
    /// 	Concatenation - middle
    /// 	Union - lowest
    /// </summary>
    /// <param name="left"></param>
    /// <param name="right"></param>
    /// <returns></returns>
    public boolean presedence(char left, char right){
        if (left == right)
            return true;

        if (left == '*')
            return false;

        if (right == '*')
            return true;

        if (left == '~')
            return false;

        if (right == '~')
            return true;

        if (left == 8)
            return false;

        if (right == 8)
            return true;

        if (left == '|')
            return false;

        return true;
    }

    /// <summary>
    /// Checks if the specific character is input character
    /// </summary>
    /// <param name="ch">The character to check.</param>
    /// <returns>True if is input, false otherwise.</returns>
    public boolean is_input(char ch) { return(!is_operator(ch)); }

    /// <summary>
    /// Evaluates the next operator from the operator stack
    /// </summary>
    /// <param name="result">The input and the result NFA.</param>
    /// <returns>True if succeeded, false otherwise.</returns>
    public boolean eval(Nfa result){
        if (operand_stack.empty())
            return false;

        // First pop the operator from the stack
        char op = operator_stack.pop();

        // Check which operator it is
        switch (op)
        {
            case  42:
                return operator_star(result);
            //case  '+':
            //	???
            //	if (!star())
            //		return false;
            //	if (!star())
            //		return false;
            //	return concat();
            //case  '?':
            //	???
            //	return star();
            case '~':
                return operator_neg(result);
            case 124:
                return operator_or(result);
            case   8:
                return operator_concat(result);
        }

        return false;
    }

    /// <summary>
    /// Evaluates the concatenation operator.
    /// This function pops two operands from the stack
    /// and evaluates the concatenation on them, pushing
    ///	the result back on the stack.
    /// </summary>
    /// <param name="result">The input and the result NFA.</param>
    /// <returns>True if succeeded, false otherwise.</returns>
    public boolean operator_concat(Nfa result){
        // Pop 2 elements
        if (operand_stack.empty())
            return false;
        Nfa b = pop();

        if (operand_stack.empty())
            return false;
        Nfa a = pop();

        a.apply_concat(b, next_state_id);

        // Push the result onto the stack
        operand_stack.push(a);

        // TRACE("CONCAT\n");

        return true;
    }

    /// <summary>
    /// Evaluates the Kleene's closure - star operator
    /// Pops one operator from the stack and evaluates
    /// the star operator on it. It pushes the result
    /// on the operand stack again.
    /// </summary>
    /// <param name="result">The input and the result NFA.</param>
    /// <returns>True if succeeded, false otherwise.</returns>
    public boolean operator_star(Nfa result){
        // Pop 1 element
        if (operand_stack.empty())
            return false;
        Nfa a = pop();

        a.apply_star(next_state_id);

        // Push the result onto the stack
        operand_stack.push(a);

        // TRACE("STAR\n");

        return true;
    }

    /// <summary>
    /// Negates the
    /// </summary>
    /// <param name="result">The input and the result NFA.</param>
    /// <returns>True if succeeded, false otherwise.</returns>
    public  boolean operator_neg(Nfa result)	{
        if (operand_stack.empty())
            return false;

        Nfa a = pop();

        a.apply_neg(next_state_id);

        // Push the result onto the stack
        operand_stack.push(a);

       // TRACE("NEG\n");

        return true;
    }

    /// <summary>
    /// Evaluates the union operator
    /// Pops 2 operands from the stack and evaluates
    /// the union operator pushing the result on the
    /// operand stack.
    /// </summary>
    /// <param name="result">The input and the result NFA.</param>
    /// <returns>True if succeeded, false otherwise.</returns>
    public boolean operator_or(Nfa result)	{
        // Pop 2 elements
        if (operand_stack.empty())
            return false;
        Nfa b = pop();

        if (operand_stack.empty())
            return false;
        Nfa a = pop();

        a.apply_union(b, next_state_id);

        // Push the result onto the stack
        operand_stack.push(a);

        // TRACE("UNION\n");

        return true;
    }

    /// <summary>
    /// Creates a non-deterministic finite automaton from a regular expression
    /// </summary>
    /// <param name="regex">The regular expression to create the NFA from.</param>
    /// <param name="result">The resulting NFA.</param>
    /// <returns>True if succeeded, false otherwise.</returns>
    public Nfa create_nfa(String regex, Nfa result){
        // Parse regular expresion using similar
        // method to evaluate arithmetic expressions
        // But first we will detect concatenation and
        // insert char(8) at the position where
        // concatenation needs to occur
        regex = expand(regex);

        for (int i = 0; i < regex.length(); ++i)
        {
            // get the charter
            char c = regex.charAt(i);

            if (is_input(c))
            {
                Nfa new_nfa = new Nfa();
                new_nfa.from_token(new Token(TokenType.___identifier___, String.valueOf(c)), next_state_id);

                // push it onto the operand stack
                operand_stack.push(new_nfa);
            }
            else
            {
                if (operator_stack.empty())
                {
                    operator_stack.push(c);
                }
                else if (c == '(')
                {
                    operator_stack.push(c);
                }
                else if (c == ')')
                {
                    // Evaluate everything in parenthesis
                    while (operator_stack.peek() != '(')
                    {
                        if (!eval(result))
                            return null;
                    }

                    // Remove left parenthesis after the evaluation
                    operator_stack.pop();
                }
                else
                {
                    while (!operator_stack.empty() && presedence(c, operator_stack.peek()))
                    {
                        if (!eval(result))
                            return null;
                    }
                    operator_stack.push(c);
                }
            }
        }

        // Evaluate the rest of operators
        while (!operator_stack.empty())
            if (!eval(result))
                return null;

        // Pop the result from the stack
        if (operand_stack.empty())
            return null;
        result = pop();

        // Last NFA state is always accepting state
        result.set_end_accepting();

        return result;
    }

    public void cleanup()	{
        // Clear both stacks
        while (!operand_stack.empty())
            operand_stack.pop();
        while (!operator_stack.empty())
            operator_stack.pop();

        // Reset the id tracking
        next_state_id.val = 0;
    }

    /// <summary>
    /// Inserts char 8 where the concatenation needs to occur
    /// The method used to parse regular expression here is
    /// similar to method of evaluating the arithmetic expressions.
    /// A difference here is that each arithmetic expression is
    /// denoted by a sign. In regular expressions the concatenation
    /// is not denoted by any sign so I will detect concatenation
    /// and insert a character 8 between operands.
    /// </summary>
    /// <param name="regex">The string to expand.</param>
    /// <returns>The expanded string.</returns>
    private String expand(String regex){
        String strRes = new String();

        for (int i=0; i < regex.length() - 1; ++i)
        {
            char cLeft	= regex.charAt(i);
            char cRight = regex.charAt(i + 1);
            strRes	   += cLeft;
            if ((is_input(cLeft)) || (cLeft == ')') || (cLeft == '*') || (cLeft == '?') || (cLeft == '+') || (cLeft == '~'))
                if ((is_input(cRight)) || (cRight == '('))
                    strRes += (char)(8);
        }
        strRes += regex.charAt(regex.length() - 1);

        return strRes;
    }
};
