package il.ac.technion.cs.ssdl.lola.regex.engine;

import java.util.ArrayList;
import java.util.List;

public class flat_submatches {
    public static class smt {
        public int now;

        public enum type {
            token, open_paren, close_paren
        }

        public type _type;
        public String name;

        public smt(int now) {
            this(now, "");
        }

        public smt(int now, String name) {
            this.now = now;
            this._type = type.token;
            this.name = name;
        }


        public smt(int now, type t) {
            this(now, t, "");
        }

        public smt(int now, type t, String name) {
            this.now = now;
            this._type = t;
            this.name = name;
        }
    }

    ;

    public List<smt> a = new ArrayList<smt>();
    public int ref_count;

    public void start(int now, String name) {
        a.add(new smt(now, smt.type.open_paren, name));
    }

    public void done(int now) {
        a.add(new smt(now, smt.type.close_paren));
    }

    public static flat_submatches get_new() {
        return new flat_submatches();
    }

    public flat_submatches clone() {
        return new flat_submatches(this);
    }

    public void dec_ref() {
        //if (!--ref_count)
        //delete this;
    }


    public void inc_ref() {
        //++ref_count;
    }

    private flat_submatches() {
        ref_count = 1;
    }

    private flat_submatches(flat_submatches other) {
        this.a = other.a;
        ref_count = 1;
    }
};
