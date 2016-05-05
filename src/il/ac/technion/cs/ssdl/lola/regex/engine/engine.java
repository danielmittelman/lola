package il.ac.technion.cs.ssdl.lola.regex.engine;

import il.ac.technion.cs.ssdl.lola.regex.Parser;
import il.ac.technion.cs.ssdl.lola.regex.automaton.Dfa;
import il.ac.technion.cs.ssdl.lola.regex.automaton.Nfa;
import il.ac.technion.cs.ssdl.lola.regex.automaton.State;
import il.ac.technion.cs.ssdl.lola.bunnies.Token;

import java.util.*;

/// <summary>
/// This is the main class to use for regular expressions. It encapsulates the
/// functionality.
/// </summary>
public class engine
{
    /// <summary>
    /// A set to hold all input characters (from all created states).
    /// </summary>
    protected Set<Token> input_characters_set;

    /// <summary>
    /// List of current partially found patterns
    /// </summary>
    protected List<thread_state> threads_list;

    /// <summary>
    /// A vector to hold pointers to the states. States are added by the NFA.
    /// </summary>
    protected List<State> nfa_states_depot;

    /// <summary>
    /// The nondeterministic finite automaton. Created by set_regex.
    /// </summary>
    /// <see cref="engine::set_regex"/>
    protected Nfa nondeterministic;

    /// <summary>
    /// The deterministic finite automaton. Created by set_regex.
    /// </summary>
    /// <see cref="engine::set_regex"/>
    protected Dfa deterministic;

    /// <summary>
    /// The parser to build the automatons.
    /// </summary>
    protected Parser parser;

    /// <summary>
    /// Constructor.
    /// </summary>
    public engine() {
        input_characters_set = new HashSet<Token>();
        nondeterministic = new Nfa();
        deterministic = new Dfa();
        parser = new Parser(input_characters_set);
        nfa_states_depot = new ArrayList<State>();
        threads_list = new ArrayList<thread_state>();
    }

    /// <summary>
    /// Sets the regular expression to search using a string.
    /// </summary>
    /// <param name="str">The string describing the regular expression.</param>
    /// <returns>True if parsing the regular expression string succeeded, false otherwise.</returns>
    public boolean set_regex(String regex)	{
        cleanup();	// clean up old regular expression

        nondeterministic =parser.create_nfa(regex, nondeterministic);
        if (nondeterministic == null)
            return false;
        deterministic.initialize(nondeterministic);
        deterministic.reduce();

        return true;
    }

    /// <summary>
    /// Searches the text_to_search using the NFA.
    /// This enables creating sub-matches.
    /// </summary>
    /// <param name="text_to_search">The text to search.</param>
    /// <returns>A vector of matches.</returns>
    public List<match> find_using_nfa(List<Token> text_to_search)
    {
        List<match> matches = new ArrayList<match>();

        // Clean up for new search
        cleanup_threads();

        if (deterministic.empty())
            return matches;

        pike pk = new pike();

        // First, we add the first thread.
        pk.init_run(nondeterministic, 0);

        // Second, we go over the transitions.
        // We do not care about sub-matches now, so we pass 0 as the offset.
        for (Token tkn : text_to_search) {
        pk.step(tkn, 0);
    }

        if (pk.is_match())
            matches.add(new match(text_to_search, 0, 0));

        return matches;
    }

    /// <summary>
    /// Searches the text_to_search using the DFA.
    /// </summary>
    /// <param name="text_to_search">The text to search.</param>
    /// <returns>A vector of matches.</returns>
    public List<match> find_using_dfa(List<Token> text_to_search)
    {
        List<match> matches = new ArrayList<match>();

        // clean up for new search
        cleanup_threads();

        // if there is no DFA then there is no matching
        if (deterministic.empty())
            return matches;



        State starting_state = deterministic.get_stating_state();

        // Go through all input characters
        for (int i = 0; i < text_to_search.size(); ++i)
        {
            // Check it against state 1 of the DFA
            threads_list.add(new thread_state(starting_state, i));

            // If matched (on epsilon), add to matches
            if (starting_state.is_accepting_state)
                matches.add(new match(text_to_search, i, i));

            // Check all patterns states
            for (Iterator<thread_state> iterator = threads_list.iterator(); iterator.hasNext();)
            {
                thread_state curr = iterator.next();

                // must be at most one because this is DFA
                List<State> target_states = curr.current_state.get_transition(text_to_search.get(i));

                if (target_states.isEmpty())
                {
                    // Delete this pattern state
                    iterator.remove();
                }
                else
                {
                    // Update state
                    curr.current_state = target_states.get(0);

                    // If matched, add to matches
                    if (curr.current_state.is_accepting_state)
                        matches.add(new match(text_to_search, curr.start_index, i + 1));
                }
            }
        }

        return matches;
    }

    /// <summary>
    /// Cleanup all resources.
    /// </summary>
    private  void cleanup()	{
        nfa_states_depot.clear();

        input_characters_set.clear();
        deterministic.cleanup();
        parser.cleanup();
        cleanup_threads();
    }

    /// <summary>
    /// Clean up all pattern states.
    /// </summary>
    private void cleanup_threads(){
        threads_list.clear();
    }
}
