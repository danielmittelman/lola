package il.ac.technion.cs.ssdl.lola.bunnies;

public class GhostKeyword implements Keyword {
    public boolean accepts(ConcreteBunny bunny) {
        if (bunny == null) {
            return false;
        }

        return true;
    }

    public void append(ConcreteBunny bunny) {

    }

    public void mature() {

    }

    public String getText() {
        return "<GhostKeyword>";
    }
}
