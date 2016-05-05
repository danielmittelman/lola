package il.ac.technion.cs.ssdl.lola.regex.automaton;

import il.ac.technion.cs.ssdl.lola.bunnies.Token;
import il.ac.technion.cs.ssdl.lola.utils.IntegerRef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// <summary>
/// A class representing a non-deterministic finite automaton.
/// </summary>
public class Nfa
{
    /// <summary>
    /// Constructor.
    /// </summary>
    public Nfa(){
        states  = new ArrayList<State>();
    }

    /// <summary>
    /// Constructs basic NFA for single character (Thompson construction
    /// element).
    /// </summary>
    /// <param name="input"></param>
    /// <param name="next_state_id">
    /// The State id to assign to a created State. Incremented when used,
    /// so this variable can be used one time after the other.
    /// </param>
    public void from_token(Token input, IntegerRef next_state_id)
    {
        // Create 2 new states for the NFA
        begin = create_state(next_state_id);
        end   = create_state(next_state_id);

        // Add the transition from s0.s1 on input character
        begin.add_transition(input, end);

        // Add this character to the input character set
        input_characters_set.add(input);
    }

    public void from_token_union(List<Token> input, IntegerRef next_state_id)
{
    begin = create_state(next_state_id);
    end = create_state(next_state_id);

    for (Token i : input)
    {
        begin.add_transition(i, end);
    }

    // Add the characters to the input character set
    input_characters_set.addAll(input);
}

    /// <summary>
    /// Concatenate the other NFA to this NFA (other is after this). If this
    /// is empty, it sets this to be the other.
    /// </summary>
    /// <param name="other">The NFA to concatenate to this.</param>
    /// <param name="next_state_id">
    /// The State id to assign to a created State. Incremented when used,
    /// so this variable can be used one time after the other.
    /// </param>
    public void apply_concat(Nfa other, IntegerRef next_state_id)
    {
        acquire_states(other);

        // If there is nothing to concatenate to, set this NFA to be the same
        // as other. Check if this NFA is empty by comparing begin to NULL.
        // Otherwise, concatenate.
        if (begin == null)
        {
            begin = other.begin;
        }
        else
        {
            // Now evaluate this other
            // Basically take the last State from this
            // and add an epsilon transition to the
            // first State of other.
            end.add_transition(Token.epsilon, other.begin);
        }

        end = other.end;
    }

    /// <summary>
    ///
    /// </summary>
    /// <param name="next_state_id">
    /// The State id to assign to a created State. Incremented when used,
    /// so this variable can be used one time after the other.
    /// </param>
    public void apply_star(IntegerRef next_state_id)
    {
        // Now evaluate this*
        // Create 2 new states which will be inserted
        // at each end of dequeue. Also take A and make
        // a epsilon transition from last to the first
        // State in the queue. Add epsilon transition
        // between two new states so that the one inserted
        // at the begin will be the source and the one
        // inserted at the end will be the destination
        State start_state	= create_state(next_state_id);
        State end_state	= create_state(next_state_id);

        start_state.op = Opcode.save_start;
        end_state.op = Opcode.save_end;

        start_state.add_transition(Token.epsilon, end_state);

        // Add epsilon transition from start State to the first State of A
        start_state.add_transition(Token.epsilon, begin);

        // Add epsilon transition from A last State to end State
        end.add_transition(Token.epsilon, end_state);

        // From A last to A first State
        end.add_transition(Token.epsilon, begin);

        // Construct new DFA and store it onto the stack
        begin = start_state;
        end = end_state;
    }

    /// <summary>
    ///
    /// </summary>
    /// <param name="next_state_id">
    /// The State id to assign to a created State. Incremented when used,
    /// so this variable can be used one time after the other.
    /// </param>
    public void apply_neg(IntegerRef next_state_id){
        end.is_accepting_state = true;

        // Create a DFA, with its own states.
        Dfa deter = new Dfa();
        deter.initialize(this);

        //#ifdef VERBOSE
        //cout << "the deter (" << deter.states[0].get_state_id() << "):" << endl;
        //cout << deter.table_to_string(*a.input_characters_set) << endl;
        //cout << deter.graph_to_string(*a.input_characters_set) << endl;
        //#endif
        // move all states of the DFA into the NFA depot.
        deter.acquire_states(this.states);

        // start connecting the dots
        begin = deter.get_stating_state();

        // create the ending State
        end = create_state(next_state_id);

        // Iterate over the states of the DFA (that were added as our states).
        for (State dfa_state :this.states)
        {
            // if it was not accepting, then now we wish to
            // negate.
            // anyway, we want the is_accepting_state flag off.
            // it will be set on in the appropriate State later
            if (!dfa_state.is_accepting_state)
                dfa_state.add_transition(Token.epsilon, end);
            else
                dfa_state.is_accepting_state = false;
        }

        // move all states of the DFA into the NFA depot.
        deter.acquire_states(this.states);
        //#ifdef VERBOSE
        //cout << "the a:(" << a.begin.get_state_id() << "):" << endl;
        //cout << a.table_to_string() << endl;
        //cout << a.graph_to_string() << endl;
        //#endif
    }

    /// <summary>
    /// The result is in the right parameter. However, both finite State
    /// automaton are changed and used
    /// </summary>
    /// <param name="left"></param>
    /// <param name="right"></param>
    /// <param name="next_state_id">
    /// The State id to assign to a created State. Incremented when used,
    /// so this variable can be used one time after the other.
    /// </param>
    public void apply_union(Nfa other, IntegerRef next_state_id)
    {
        // Now evaluate A|B
        // Create 2 new states, a start State and
        // a end State. Create epsilon transition from
        // start State to the start states of A and B
        // Create epsilon transition from the end
        // states of A and B to the new end State
        acquire_states(other);

        State start_state	= create_state(next_state_id);
        State end_state	= create_state(next_state_id);

        start_state.op = Opcode.save_start;
        end_state.op = Opcode.save_end;

        start_state.add_transition(Token.epsilon, begin);
        start_state.add_transition(Token.epsilon, other.begin);
        this.end.add_transition(Token.epsilon, end_state);
        other.end.add_transition(Token.epsilon, end_state);

        // Create new NFA from A
        begin = start_state;
        end = end_state;
    }

    /// <summary>
    /// Wraps the current NFA with a sub-match indication with the
    /// specified name.
    /// </summary>
    /// <param name="name"></param>
    /// <param name="next_state_id">
    /// The State id to assign to a created State. Incremented when used,
    /// so this variable can be used one time after the other.
    /// </param>
    public void wrap(String name, IntegerRef next_state_id)
    {
        State start_state = create_state(next_state_id);
        State end_state = create_state(next_state_id);

        start_state.op_name = name;
        start_state.op = Opcode.save_start;
        end_state.op = Opcode.save_end;

        // Wrap.
        start_state.add_transition(Token.epsilon, this.begin);
        this.end.add_transition(Token.epsilon, end_state);

        begin = start_state;
        end = end_state;
    }

    /// <summary>
    /// Builds a string that describes the states in a table format.
    /// </summary>
    /// <returns>The built string.</returns>
    public String to_table_string()
        {
        return to_table_string(states, input_characters_set);
        }

    /// <summary>
    /// Builds a string that describes the states in a graph format.
    /// </summary>
    /// <param name="input_characters_set">The set of input tokens.</param>
    /// <returns>The built string.</returns>
    public String to_graph_string()
    {
        return to_graph_string(states, input_characters_set);
    }

    /// <summary>
    /// Gets the starting State.
    /// </summary>
    /// <returns>The starting State.</returns>
    public State get_stating_state()
        {
        return this.begin;
        }

    /// <summary>
    /// Sets the end State to be accepting.
    /// </summary>
    public void set_end_accepting()
        {
        this.end.is_accepting_state = true;
        }


    /// <summary>
    /// Acquires all the states of other NFA. This means it is now this NFA's responsibility to delete them.
    /// </summary>
    /// <param name="other"></param>
    private void acquire_states(Nfa other)
        {
        this.states.addAll(other.states);
        }

    /// <summary>
    /// Create a new State on the heap, and add it to states collection.
    /// </summary>
    /// <returns>The new State.</returns>
    private State create_state(IntegerRef next_state_id)
        {
        // Create the new State on the heap, incrementing the next_state_id
        State new_state = new State(next_state_id.val++);

        // Add the State to the states collection
        states.add(new_state);

        // Return the newly created State
        return new_state;
        }


    /// <summary>
    /// A static collection to hold the input character set.
    /// </summary>
    public static Set<Token> input_characters_set = new HashSet<Token>();


    /// <summary>
    /// A collection to hold the states of the non-deterministic finite automaton.
    /// TODO: they are never deleted! handle this later!
    /// </summary>
    private List<State> states;

    /// <summary>
    /// The beginning State.
    /// </summary>
    private State begin;

    /// <summary>
    /// The ending State.
    /// </summary>
    private State end;





private static String to_table_string(State subject, Token input)
        {
        List<State> target_states = subject.get_transition(input);

        StringBuilder result = new StringBuilder();
        if (!target_states.isEmpty())
        {
        result.append(target_states.get(0).get_decorated_state_id());
        for (int i = 1; i < target_states.size(); ++i)
        result.append("," ).append(target_states.get(i).get_decorated_state_id());
        }
        return result.toString();
        }

private static String to_graph_string(State subject, Token input)
        {
            List<State> target_states = subject.get_transition(input);

            StringBuilder result = new StringBuilder();
        for (int j = 0; j < target_states.size(); ++j)
        {
        // Record transition
        String strStateID1 = subject.get_state_id();
        String strStateID2 = target_states.get(j).get_state_id();
        result .append ("\t") .append(strStateID1 ).append(" . " ).append( strStateID2);
        result .append("\t[label=\"") .append( input ) .append( "\"];\n");
        }
        return result.toString();
        }


        /// <summary>
        /// Gets a string describing the provided steps. This method can be used
        /// for NFA states, as well as for DFA states, as long as it
        /// </summary>
        /// <param name="states">The states to describe.</param>
        /// <param name="input_characters_set">The input characters of which the transitions are performed on.</param>
        /// <returns>A string describing the provided steps.</returns>
        static  String to_table_string(List<State> states, Set<Token> input_characters_set)
        {
        StringBuilder result = new StringBuilder();

        // First line are input characters
        for (Token input : input_characters_set)
        result .append( "\t\t" ).append( input);
        // add epsilon
        result .append( "\t\tepsilon");
        result .append( "\n");

        // Now go through each State and record the transitions
        // we assume all the states in states are relevant
        // (it is probably all our states)
        for (State subject : states)
        {
        // Save the State id
        result .append( subject.get_decorated_state_id());

        // now write all transitions for each character
        //List<State> target_states;
        for (Token c : input_characters_set)
        {
        result .append( "\t\t" ).append( to_table_string(subject, c));
        }

        // Add all epsilon transitions
        result .append( "\t\t" ).append( to_table_string(subject, Token.epsilon) ).append( "\n");
        }

        return result.toString();
        }

        /// <summary>
        /// Gets a string describing the provided steps. This method can be used
        /// for NFA states, as well as for DFA states, as long as it
        /// </summary>
        /// <param name="states">The states to describe.</param>
        /// <param name="input_characters_set">The input characters of which the transitions are performed on.</param>
        /// <returns>A string describing the provided steps.</returns>
        static String to_graph_string(List<State> states, Set<Token> input_characters_set)
        {
            StringBuilder result = new StringBuilder();
        result .append( ("digraph{\n"));

        // Final states are double circled
        for (State subject : states)
        {
        if (subject.is_accepting_state)
        {
        result .append( "\t" ).append( subject.get_state_id() ).append( "\t[shape=doublecircle];" ).append( "\n");
        }
        }

        result .append( "\n");

        // Record transitions
        for (State subject : states)
        {
        result .append( to_graph_string(subject, Token.epsilon));

        for (Token input : input_characters_set)
        {
        result .append( to_graph_string(subject, input));
        }
        }

        result .append( "}");

        return result.toString();
        }
};

