package il.ac.technion.cs.ssdl.lola.regex.engine;

import com.google.common.collect.Multimap;
import il.ac.technion.cs.ssdl.lola.bunnies.Token;
import il.ac.technion.cs.ssdl.lola.regex.automaton.*;

import java.util.*;

/// <summary>
/// A class implementing the Pike algorithm for finding sub-matches in a
/// matched text of a regular expression.
/// </summary>
public class pike
{
    static public class thread
    {
        public State pc;
        public  flat_submatches submatches;
        /// <summary>
        /// A negative value means no tracking.
        /// </summary>
        public   Stack<Integer> balanced_counter;

        public   thread(State pc, flat_submatches submatches, Stack<Integer> balanced_counter)
        {
            this.pc
                     = pc;
            this.submatches = submatches;
            this.balanced_counter = (Stack<Integer>)balanced_counter.clone();
        }
        public   thread(thread other){
            this(other.pc, other.submatches, other.balanced_counter);
        }
       //public   boolean lessthan(thread other){
       //    if (this->pc == other.pc)
       //    return this->submatches < other.submatches;
       //
       //    return this->pc < other.pc;
       //}


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            thread thread = (thread) o;

            if (pc != null ? !pc.equals(thread.pc) : thread.pc != null) return false;
            if (submatches != null ? !submatches.equals(thread.submatches) : thread.submatches != null) return false;
            return balanced_counter != null ? balanced_counter.equals(thread.balanced_counter) : thread.balanced_counter == null;

        }

        @Override
        public int hashCode() {
            int result = pc != null ? pc.hashCode() : 0;
            result = 31 * result + (submatches != null ? submatches.hashCode() : 0);
            result = 31 * result + (balanced_counter != null ? balanced_counter.hashCode() : 0);
            return result;
        }
    };

    public pike(){
        clist = new HashSet<thread>(); /*current list*/
        nlist = new HashSet<thread>(); /*next list*/

        matches =  new ArrayList<flat_submatches>();
    }

    /// <summary>
    /// Initialize the run.
    /// </summary>
    /// <param name="prog">The NFA to run.</param>
    /// <param name="token_index">
    /// The Pike algorithm tracks sub-matches. To relate a sub-match with
    /// the appropriate token(s), we use the token index.
    /// </param>
    /// <example>
    /// This sample shows how to call the <see cref="init_run"/> and <see cref="step"/> methods.
    /// <code>
    /// // first, we add the first thread
    /// init_run(prog);
    ///
    /// // second, we go over the transitions
    /// for (const token &tkn : input) {
    /// 	step(tkn);
    /// }
    /// </code>
    /// </example>
    public  void init_run(Nfa prog, int token_index)	{
        //#ifdef VERBOSE
        //cout << "starting at " << prog.get_stating_state()->get_decorated_state_id() << endl;
        //#endif
        // first, we add the first thread
        flat_submatches  fs = flat_submatches.get_new();
        fs.start(token_index, "");
        add_new_thread(prog.get_stating_state(), fs, token_index, new Stack<Integer>());

        swapLists();
    }


    private void swapLists(){
        final Set<thread> tmp = nlist;
        nlist = clist;
        clist = tmp;
    }

    /// <summary>
    /// Step over the input token. Must be called after <see cref="init_run"/>.
    /// </summary>
    /// <param name="tkn">The input token to step over.</param>
    /// <param name="token_index">
    /// The Pike algorithm tracks sub-matches. To relate a sub-match with
    /// the appropriate token(s), we use the token index.
    /// </param>
    /// <returns>
    /// true if there are remaining threads (that might match), false otherwise.
    /// </returns>
    /// <seealso cref="init_run"/>
    public boolean step(Token tkn, int token_index){
        //#ifdef VERBOSE
        //print_clist();
        //#endif

        for (thread thrd : clist) {
            advance(thrd, tkn, token_index);
        }

        // swap
        swapLists();

        nlist.clear();

        return !clist.isEmpty();
    }

    public boolean is_match() {
        return matches.size() > 0;
    }

    public List<flat_submatches> matches;

    /// <summary>
    /// <see cref="clist"/> are the current threads, <see cref="nlist"/> are the next threads.
    /// </summary>
    private Set<thread> clist, nlist;

    /// <summary>
    /// The index to the string being inspected.
    /// </summary>
    private int now;

    private flat_submatches get_updated_submatches(State pc, flat_submatches submatches, int offset){
    if (pc.op != Opcode.none)
    {
        submatches = submatches.clone();
        if (pc.op == Opcode.save_start)
        {
            submatches.start(offset, pc.op_name);
        }
        else if (pc.op == Opcode.save_end)
        {
            // The end offset is non-inclusive.
            submatches.done(offset);
        }
    }

    // here we also increment the reference counter
    submatches.inc_ref();

    return submatches;
}
    private  void add_new_thread(State pc, flat_submatches submatches, int offset, Stack<Integer> balanced_counter)
    {
        submatches = get_updated_submatches(pc, submatches, offset);

        nlist.add(new thread(pc, submatches, balanced_counter));
        thread t =  new thread(pc, submatches, balanced_counter);
        nlist.add(t);
        on_new_thread_added(t, offset);

        // when adding a new thread, also add the epsilon closure
        List<State> target_states =        pc.get_transition(Token.epsilon);
        for (State target : target_states) {
        add_new_thread(target, submatches, offset, balanced_counter);
    }
    }

    // on thrd.pc == require_balanced open than push a new counter to stack
    // on thrd.pc == require_balanced close than pop the counter from stack
    // on thrd.pc != require_balanced open than inc the counter on stack
    // on thrd.pc != require_balanced close than dec the counter on stack
    // The count starts at zero
    private  boolean can_advance_on_balanced(Token tkn, Token input, Stack<Integer> balanced_counter) 	{
        assert(!TokenType.is_require_balanced(input.get_type()));
        // Since all that can be balanced are default  balanced.
        assert(TokenType.is_default_balanced(input.get_type()));

        // we assume that tkn == input except the require flag

        if (TokenType.is_require_balanced(tkn.get_type()))
        {
            if (is_balanced_open(input.get_type()))
            {
                balanced_counter.push(0);
                return true;
            }

            //(is_balanced_close(input.get_type()))
            // If we did not start counting or we are not balanced
            if (balanced_counter.empty() || balanced_counter.peek() != 0)
            {
                return false;
            }

            balanced_counter.pop();
            return true;
        }
        else
        {
            if (balanced_counter.empty())
                return true;

            if (is_balanced_open(input.get_type()))
            {
                balanced_counter.push(balanced_counter.pop()+1);
                return true;
            }

            //(is_balanced_close(input.get_type()))
            if (balanced_counter.peek() == 0)
            {
                return false;
            }


            balanced_counter.push(balanced_counter.pop()-1);
            return true;
        }
    }





    private void on_transition(Token tkn, State target, thread thrd, Token  input, int offset)	{
        if (TokenType.is_default_balanced(input.get_type()))
        {
            Stack<Integer> balanced_counter = thrd.balanced_counter;
            if (can_advance_on_balanced(tkn, input, balanced_counter))
            {
                add_new_thread(target, thrd.submatches, offset + 1, balanced_counter);
            }
        }
        else
        {
            add_new_thread(target, thrd.submatches, offset + 1, thrd.balanced_counter);
        }
    }



    private   void advance(thread thrd, Token input, int offset)
    {
        Multimap<Token,State> transitions = thrd.pc.transitions;

        if (input.get_type() != TokenType.Epsilon)
        {
            // Iterate through all values with the key input
            for (State target_state :transitions.get(Token.any))
            {
                assert(target_state != null);
                on_transition(Token.any, target_state, thrd, input, offset);
            }
        }

        // Iterate through all values with the key input
        for (State target_state :transitions.get(input))
        {
            assert(target_state != null);
            on_transition(input, target_state, thrd, input, offset);
        }

        // this is where a thread dies
        thrd.submatches.dec_ref();
    }


    private   void on_new_thread_added(thread thrd, int offset)
    {
        if (thrd.pc.is_accepting_state && thrd.balanced_counter.empty()) {
        flat_submatches fs = thrd.submatches.clone();
        fs.done(offset);
        matches.add(fs);
    }
    }
    private   void print_clist()	{
        StringBuilder builder = new StringBuilder();

        builder.append( now ).append( ":\t");
        for (thread thrd : clist) {
            builder .append(thrd.pc.get_decorated_state_id() ).append(" ");
        }
        builder .append("\n");
    }





    static 	boolean is_balanced_open(int type)
    {
        return type == TokenType.___opencurly___;
    }

    static boolean is_balanced_close(int type)
    {
        return type == TokenType.___closecurly___;
    }
};