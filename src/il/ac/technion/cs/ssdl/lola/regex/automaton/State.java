package il.ac.technion.cs.ssdl.lola.regex.automaton;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import il.ac.technion.cs.ssdl.lola.bunnies.Token;

import java.util.*;

/// <summary>
/// Represents either an NFA State and a DFA State.
/// Each State object has a multi-map of transitions where the key is the
/// input character and values are the references to states to which it
/// transfers.
/// </summary>
public class State
{
    /// <summary>
    /// Transitions from this State to other. Epsilon transitions are using the
    /// token::epsilon value.
    /// </summary>
    /// <seealso cref="token::epsilon"/>
    //token::less
    public Multimap<Token, State> transitions;

    /// <summary>
    /// State id. initialized as -1 for error detection.
    /// </summary>
    public int state_id;

    /// <summary>
    /// Set of NFA states from which this State is constructed.
    /// </summary>
    public Set<State> nfa_states;

    /// <summary>
    /// True if this State is an accepting State, false otherwise.
    /// </summary>
    public boolean is_accepting_state;

    /// <summary>
    /// For now, let's have a opcode to store the SaveStart, SaveEnd
    /// </summary>
    public Opcode op;

    /// <summary>
    /// Also, have the name for now.
    /// </summary>
    public String op_name;

    /// <summary>
    /// Parameterized constructor.
    /// </summary>
    /// <param name="id">The id of the new State.</param>
    public State(int id)
    {
        transitions = ArrayListMultimap.create();
        this.state_id = id;
        is_accepting_state = false;
        op = Opcode.none;
    }

    /// <summary>
    /// Constructs new State from the set of other states.
    /// This is necessary for subset construction algorithm
    /// because there a new DFA State is constructed from
    /// one or more NFA states.
    /// </summary>
    /// <param name="nfa_states">The states that the new State is constructed from.</param>
    /// <param name="id">The id of the new State.</param>
    public State(Set<State> nfa_states, int id)
    {
        transitions = ArrayListMultimap.create();
        this.state_id = id;
        op = Opcode.none;
        this.nfa_states=nfa_states;

        // DFA State is accepting State if it is constructed from
        // an accepting NFA State
        is_accepting_state = false;
        for (State target_state : this.nfa_states)
        if (target_state.is_accepting_state)
            is_accepting_state = true;
    }

    /// <summary>
    /// Adds a transition from this State to the other
    /// </summary>
    /// <param name="input">The input token that the transition refers to.</param>
    /// <param name="target_state">The target State.</param>
    public void add_transition(Token input, State target_state){
        assert(target_state != null);
        transitions.put(input, target_state);
    }

    /// <summary>
    /// Removes all transition that go to the provided State.
    /// </summary>
    /// <param name="target_state">The target State that transitions to it should be removed.</param>
    public void remove_transition(State target_state)
    {
        Collection<Map.Entry<?, ?>> c = new ArrayList(transitions.entries());
        for (Map.Entry<?, ?> iter : c) {
            if (iter.getValue().equals(target_state))
                transitions.remove(iter.getKey(), iter.getValue());
        }
    }

    /// <summary>
    /// Returns all transitions from this State on specific input.
    /// </summary>
    /// <param name="input">The input token that the transition refers to.</param>
    /// <param name="target_states">The target states.</param>
    public List<State> get_transition(Token input)
    {
        // clear the array first
        List<State> target_states = new ArrayList<State>();

        if (input.get_type() != TokenType.Epsilon)
        {
            // Iterate through all values with the key input

            for (State target_state : transitions.get(Token.any))
            {
                assert(target_state != null);
                target_states.add(target_state);
            }
        }

        // Iterate through all values with the key input
        for (State target_state : transitions.get(input))
        {
            assert(target_state != null);
            target_states.add(target_state);
        }

        return  target_states;
    }

    /// <summary>
    /// Returns the State id in form of string. Accepting states are decorated
    /// with curly braces.
    /// </summary>
    /// <returns>The State id in form of string.</returns>
    public  String get_decorated_state_id()
    {
        StringBuilder result = new StringBuilder();
        if (is_accepting_state)
            result.append("{").append(state_id).append("}");
        else
            result.append(state_id);
        return result.toString();
    }

    /// <summary>
    /// Returns the State id in form of string.
    /// </summary>
    /// <returns>The State id in form of string.</returns>
    public String get_state_id() 	{
        StringBuilder result = new StringBuilder();
        result.append(state_id);
        return result.toString();
    }

    /// <summary>
    /// Returns the set of NFA states from which this State was constructed.
    /// </summary>
    /// <returns>The set of NFA states from which this State was constructed.</returns>
    public Set<State> get_nfa_states()
    {
        return nfa_states;
    }

    /// <summary>
    /// Returns true if this State is a dead end. A dead end is a State that is not
    /// an accepting State and there are no transitions leading away from this State.
    /// This function is used for reducing the DFA.
    /// </summary>
    /// <returns>True if this is a dead end. False otherwise.</returns>
    public boolean is_dead_end()	{
        if (is_accepting_state)
            return false;
        if (transitions.isEmpty())
            return true;

        for (State target_state : transitions.values())
        {
            if (target_state != this)
                return false;
        }

        //TRACE("State %d is dead end.\n", state_id);

        return true;
    }

    /// <summary>
    /// Overrides the comparison operator.
    /// </summary>
    /// <param name="other">the other State to compare to.</param>
    /// <returns>True if states are equal, false otherwise.</returns>

    @Override
    public boolean equals(Object obj) {
        State other = (State) obj;
        // in case this is a nfa state
        if (nfa_states == null||nfa_states.isEmpty())
            return (state_id == other.state_id);
        // in case this is a dfa state
        else
            return (nfa_states == other.nfa_states);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /// <summary>
    /// Calculates the epsilon closure. Returns the epsilon closure of all
    /// states given by the parameter.
    /// </summary>
    /// <param name="states">The states to epsilon closure on.</param>
    /// <param name="result">The resulting epsilon closure.</param>
    public static Set<State> epsilon_closure(Set<State> states) {
        Set<State> result = new HashSet<State>();

        // Initialize result with states because each State
        // has epsilon closure to itself
        result.addAll(states);

        // Push all states onto the stack
        Stack<State> todo_stack = new Stack<State>();
        todo_stack.addAll(states);

        // While the unprocessed stack is not empty
        while (!todo_stack.empty())
        {
            // Pop t, the top element from unprocessed stack
            State t = todo_stack.peek();
            todo_stack.pop();

            // Get all epsilon transition for this State
            List<State> epsilon_states = t.get_transition(Token.epsilon);

            // For each State u with an edge from t to u labeled epsilon
            for (State u : epsilon_states)
            {
                // if u not in epsilon-closure(states)
                if (!result.contains(u))
                {
                    result.add(u);
                    todo_stack.push(u);
                }
            }
        }

        return  result;
    }

    /// <summary>
    /// Calculates all transitions on specific input char.
    /// </summary>
    /// <param name="input">The input token to transition on.</param>
    /// <param name="states">The starting states.</param>
    /// <param name="result">All states reachable from this set of states on an input character.</param>
    public static Set<State> move(Token input, Set<State> states)
    {
        Set<State> result = new HashSet<State>();

        for (State s : states)
        {
            List<State> target_states=           s.get_transition(input);

            for (State target_state : target_states)
            result.add(target_state);
        }

        return result;
    }
};
