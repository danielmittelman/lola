package il.ac.technion.cs.ssdl.lola.regex.engine;

import il.ac.technion.cs.ssdl.lola.regex.automaton.State;

/// <summary>
/// Structure to track the pattern recognition.
/// </summary>
class thread_state
{
    /// <summary>
    /// Pointer to current state in DFA.
    /// </summary>
    public State current_state;

    /// <summary>
    /// 0-Based index of the starting position.
    /// </summary>
    public int start_index;

    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="current_state">The current state of the thread.</param>
    /// <param name="start_index">The index in the input in which the search was started.</param>
    public thread_state(State current_state, int start_index)
    {
        this.current_state = current_state;
        this.start_index=start_index;
    }
}
