package il.ac.technion.cs.ssdl.lola.bunnies;

/**
 * A concrete {@link Bunny} is one which originates from a file, rather than a ghost {@link Bunny}, whose sole purpose
 * is to be used in the parsing algorithm.
 */
public interface ConcreteBunny extends Bunny {
    /// <summary>
    /// The line in which the bunny appears. First line is line 0. The line
    /// number refers to the original file or string (and not after the
    /// preprocessing).
    /// </summary>
    int getLine();

    /// <summary>
    /// The column in which the bunny appears. First column is column 0.
    /// The column number refers to the original file or string (and not
    /// after the preprocessing).
    /// </summary>
    int getColumn();
}
