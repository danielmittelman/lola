package il.ac.technion.cs.ssdl.lola.regex.automaton;

import il.ac.technion.cs.ssdl.lola.bunnies.Token;

import java.util.*;

/// <summary>
/// A class representing a deterministic finite automaton (DFA).
/// An instance can be constructed from a non-deterministic finite automaton (NFA).
/// </summary>
/// <remarks>
/// The constructor does not acquire resources, and the destructor does not release resources.
/// This are the responsibilities of the initialize and the cleanup methods, respectively.
/// </remarks>
/// <seealso cref="Dfa::initialize"/>
/// <seealso cref="Dfa::cleanup"/>
public  class Dfa
{
    /// <summary>
    /// Constructor
    /// </summary>
    public Dfa(){
        states = new ArrayList<State>();
    }

    /// <summary>
    /// Creates a deterministic finite automaton from a non-deterministic finite automaton.
    /// </summary>
    /// <param name='nondeterministic'>The a non-deterministic finite automaton to convert from.</param>
    public  void initialize(Nfa nondeterministic){
        // Clean up the DFA Table first
        cleanup();

        // Check is NFA table empty
        if (nondeterministic.get_stating_state() == null)
            return;

        // Reset the State id for new naming
        int next_state_id = 0;

        // Create the starting State
        State dfa_start_state = create_dfa_start_state(nondeterministic.get_stating_state(), ++next_state_id);

        // Add the start State to the DFA
        states.add(dfa_start_state);

        // The states in todo_states are the unprocessed DFA states. Add the starting State to it.
        Stack<State> todo_states  = new Stack<State>();
        todo_states.add(dfa_start_state);

        // While there are more states to be processed
        while (!todo_states.isEmpty())
        {
            // process an unprocessed State
            State current_dfa_state = todo_states.peek();
            todo_states.pop();

            // for each input signal
            for (Token input : Nfa.input_characters_set)
            {
                Set<State> move_result = State.move(input, current_dfa_state.get_nfa_states());
                Set<State> epsilon_closure_result = State.epsilon_closure(move_result);

                State s = find_dfa_state_by_nfa_states(epsilon_closure_result, states);

                if (s == null) /* not found */
                {
                    s = new State(epsilon_closure_result, ++next_state_id);
                    todo_states.add(s);
                    states.add(s);
                }

                // If the State did not exist, than add transition from
                // current_dfa_state to new State on the current character.
                // Otherwise,
                // this State already exists so add transition from
                // processingState to already processed State
                current_dfa_state.add_transition(input, s);
            }
        }

        starting_state = states.get(0);
    }

    /// <summary>
    /// Cleanup the resources acquired by the deterministic finite automaton.
    /// </summary>
    public void cleanup()	{
        states.clear();
    };

    /// <summary>
    /// Optimizes the DFA. This function scans DFA and checks for states that are not
    /// accepting states and there is no transition from that State
    /// to any other State. Then, after deleting this State, we need to
    /// go through the DFA and delete all transitions from other states
    /// to this one.
    /// </summary>
    public void reduce()
    {
        // Get the set of all dead end states in DFA
        Set<State> dead_end_set = new HashSet<State>();
        for (State s : states)
        if (s.is_dead_end())
            dead_end_set.add(s);

        // If there are no dead ends then there is nothing to reduce.
        // Remove all transitions to these states
        for (State target_state : dead_end_set)
        {
            // Remove all transitions to this State
            for (State s : states)
            s.remove_transition(target_state);

            // Remove this State from the DFA Table
            // Erase element from the table
            // (this erases all elements with this value, though there is only one)
            states.remove(target_state);

            // Now free the memory used by the element
            //delete target_state;
        }
    }

    /// <summary>
    /// Return whether the DFA is empty.
    /// </summary>
    /// <returns>True if there are no states in the DFA, false otherwise.</returns>
    public  boolean empty()	{
        return states.isEmpty();
    }

    /// <summary>
    /// Gets the starting State for the DFA.
    /// </summary>
    /// <returns>The starting State of the DFA.</returns>
    public State get_stating_state()	{
        return starting_state;

    }

    /// <summary>
    /// Gets ownership of the states of the this DFA. This means their lifetime is no longer
    /// bound to this object, in particular, the DFA relinquishes any references to these states
    /// in its repository (the states variable is cleared). It is added to the end of the
    /// repository parameter.
    /// This method is meant to be used by the NFA class.
    /// </summary>
    /// <param name="repository">The repository to move the references to.</param>
    public void acquire_states(List<State> repository)	{
        repository.addAll(states);

        states.clear();

    }

    /// <summary>
    /// Builds a string that describes the states in a table format.
    /// Since the input characters set is not saved per DFA instance, it must be provided.
    /// </summary>
    /// <param name="input_characters_set">The set of input tokens.</param>
    /// <returns>The built string.</returns>
    public String table_to_string(Set<Token> input_characters_set)	{
        return Nfa.to_table_string(states, input_characters_set);
    }

    /// <summary>
    /// Builds a string that describes the states in a graph format.
    /// Since the input characters set is not saved per DFA instance, it must be provided.
    /// </summary>
    /// <param name="input_characters_set">The set of input tokens.</param>
    /// <returns>The built string.</returns>
    public String graph_to_string(Set<Token> input_characters_set)	{
        return Nfa.to_graph_string(states, input_characters_set);
    }


    /// <summary>
    /// The starting State of the DFA.
    /// </summary>
    private State starting_state;

    /// <summary>
    /// A collection to hold the states of the deterministic finite automaton.
    /// </summary>
    private List<State> states;






    /// <summary>
    /// Search for a State in the states collection that is created from the
    /// set of states defined by state_set.
    /// Or in pseudo-code:
    ///		is U in states such that already (U = state_set)
    /// </summary>
    /// <param name="state_set"></param>
    /// <param name="states"></param>
    /// <returns></returns>
    private static State find_dfa_state_by_nfa_states(Set<State> state_set, List<State> states)
{
    for (State curr : states) {
    if (curr.get_nfa_states().equals(state_set)) {
        return curr;
    }
}

    return null;
}

    /// <summary>
    /// Create a starting State from the given NFA. The starting State of a DFA is the epsilon
    /// closure of starting State of NFA State (set of states).
    /// </summary>
    /// <param name="nfa_start_state">The NFA start State.</param>
    /// <param name="next_state_id">The value to be set as the new State id.</param>
    /// <returns>The newly created State.</returns>
    private State create_dfa_start_state(State nfa_start_state, int state_id)
{
    Set<State> nfa_start_state_set = new HashSet<State>();
    nfa_start_state_set.add(nfa_start_state);

    Set<State> dfa_start_state_set =  State.epsilon_closure(nfa_start_state_set);

    // Create new DFA State (start State) from the NFA states
    return new State(dfa_start_state_set, state_id);
}
};