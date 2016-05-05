package il.ac.technion.cs.ssdl.lola.bunnies;

/**
 * A Kyword used for the parsing algorithm. Either a ConcreteKeyword or a
 * GhostKeyword.
 */
public interface Keyword extends  Bunny {
    /**
     * should consider indentation!
     * accepts(null) should return false
     *
     * @param bunny
     * @return
     */
    boolean accepts(ConcreteBunny bunny);

    void append(ConcreteBunny bunny);

    void mature();
}
