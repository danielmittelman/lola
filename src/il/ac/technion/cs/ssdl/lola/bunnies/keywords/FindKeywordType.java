package il.ac.technion.cs.ssdl.lola.bunnies.keywords;

import il.ac.technion.cs.ssdl.lola.bunnies.*;

public class FindKeywordType implements KeywordType {
    public boolean isOfThisType(String name) {
        return "@Find".equals(name);
    }

    public int getPythonArgumentRequirement() {
        return PythonArgumentRequirement.Optional;
    }

    public ConcreteKeyword create(String name, int line, int column) {
        return new FindKeyword(this, name, line, column);
    }

    static private class FindKeyword extends CommonKeyword implements Constructor {
        protected FindKeyword(KeywordType type, String value, int line, int column) {
            super(type, value, line, column);
        }
    }
}
