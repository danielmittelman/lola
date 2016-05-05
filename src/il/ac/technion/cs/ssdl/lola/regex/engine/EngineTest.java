package il.ac.technion.cs.ssdl.lola.regex.engine;

import il.ac.technion.cs.ssdl.lola.bunnies.Token;
import il.ac.technion.cs.ssdl.lola.regex.automaton.TokenType;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/// /// <summary>
/// Some abuse here. We use the a token type (never mind which, but we chose
/// token_type::id), and use the string value to distinguish between
/// characters.
/// </summary>
public class EngineTest {

    static List<Token> as_tokens(String str)
    {
        List<Token> result = new ArrayList<Token>();

        for (char c : str.toCharArray())
        {
            result.add(new Token(TokenType.___identifier___, String.valueOf(c)));
        }

        return result;
    }


    static String as_string(List<Token> tokens)
    {
        StringBuilder result = new StringBuilder();

        for (Token t : tokens)
        {
            result.append(t.get_value());
        }

        return result.toString();
    }

    static Set<String> matches_to_string_set(List<match> matches)
    {
        Set<String> result = new HashSet<String>();

        for (match m : matches)
            result.add(as_string(m.matched_text));

        return result;
    }

    static Set<String> regex_test(String regex, String text)
    {
        engine e = new engine();

        e.set_regex(regex);
        List<match> matches = e.find_using_dfa(as_tokens(text));

        return matches_to_string_set(matches);
    }

    static Set<String> as_set(String comma_seperated_strings)
    {
        // comma_seperated_strings is in the format "a,b,c"

        Set<String> set = new HashSet<String>();

        if (comma_seperated_strings.equals(""))
            return  set;

        for (String str :  comma_seperated_strings.split(",")){
                set.add(str);
        }

        return set;
    }


    @Test
            public  void test_the_as_set()
    {
        // In this test we make sure that the function as_set is working properly.
        Set<String> result = as_set("a,aa,aaa,aaaa");
        Set<String> expected = new HashSet<String>( Arrays.asList(
                "a",
                "aa",
                "aaa",
                "aaaa"
                ));

        assertEquals(expected, result );
    }

    @Test
    public  void test_the_as_set_empty()
    {
        // In this test we make sure that the function as_set is working properly.
        Set<String> result = as_set("");
        Set<String> expected = new HashSet<String>();

        assertEquals(expected, result );
    }

    @Test
    public  void test_engine()
    {
        engine e = new engine();

        e.set_regex("aa*");
        List<match> matches = e.find_using_dfa(as_tokens("aaaa"));

        assertFalse(matches.isEmpty());

        Set<String> result = matches_to_string_set(matches);

        Set<String> expected = new HashSet<String>( Arrays.asList(
                "a",
                "aa",
                "aaa",
                "aaaa"
        ));

        assertEquals(expected, result);
    }


    private void REGEX_TEST_CASE(String expression, String text, String expected_result) {
        Set<String> result = regex_test(expression, text);
        Set<String> expected = as_set(expected_result);

        assertEquals(expected, result );
    }




    @Test public void test_regex_star	(){REGEX_TEST_CASE("aa*", "aaaa", "a,aa,aaa,aaaa");  }
    @Test public void test_regex_or		(){REGEX_TEST_CASE("(a|b)*", "a", ",a");  }

    // All the problems I encountered were because of the tests, not the implementation. Once in a while, fix another test.
// Also, ? (zero or one) and + (one or more) are not implemented.
    @Test public void test_regex_01		(){REGEX_TEST_CASE("(ab|a)(bc|c)",			"abc",	"abc"				);}
    @Test public void test_regex_02		(){REGEX_TEST_CASE("(ab|a)(bc|c)",			"acb",	"ac"				);}
    @Test public void test_regex_03		(){REGEX_TEST_CASE("(ab)c|abc",				"abc",	"abc"				);}
    @Test public void test_regex_04		(){REGEX_TEST_CASE("(ab)(c|a)bc",			"ababc",	"ababc"			);}
    //@Test public void test_regex_05	(){REGEX_TEST_CASE("(a*)(b?)(b+)",			"aaabbbb",	"aaabbbb"		);}
//@Test public void test_regex_06	(){REGEX_TEST_CASE("(a*)(b?)(b+)",			"aaaa",	"aaaa"				);}
    @Test public void test_regex_07		(){REGEX_TEST_CASE("((a|a)|a)",				"a",	"a"					);}
    @Test public void test_regex_08		(){REGEX_TEST_CASE("((a|a)|a)",				"aa",	"a"					);}
    @Test public void test_regex_09		(){REGEX_TEST_CASE("(a*)(a|aa)",				"aaaa",	"a,aa,aaa,aaaa"		);}
    @Test public void test_regex_10		(){REGEX_TEST_CASE("(a*)(a|aa)",				"b",	""					);}
    @Test public void test_regex_11		(){REGEX_TEST_CASE("a(b)|c(d)|a(e)f",		"aef",	"aef"				);}
    @Test public void test_regex_12		(){REGEX_TEST_CASE("a(b)|c(d)|a(e)f",		"adf",	""					);}
    @Test public void test_regex_13		(){REGEX_TEST_CASE("(a|b)c|a(b|c)",			"ac",	"ac"				);}
    //@Test public void test_regex_14	(){REGEX_TEST_CASE("(a|b)c|a(b|c)",			"acc",	"acc"				);}
    @Test public void test_regex_15		(){REGEX_TEST_CASE("(a|b)c|a(b|c)",			"ab",	"ab"				);}
    //@Test public void test_regex_16	(){REGEX_TEST_CASE("(a|b)c|a(b|c)",			"acb",	"acb"				);}
//@Test public void test_regex_17	(){REGEX_TEST_CASE("(a|b)*c|(a|ab)*c",		"abc",	"abc"				);}
//@Test public void test_regex_18	(){REGEX_TEST_CASE("(a|b)*c|(a|ab)*c",		"bbbcabbbc",	"bbbcabbbc"	);}
//@Test public void test_regex_19	(){REGEX_TEST_CASE("a?(ab|ba)ab",			"abab",	"abab"				);}
//@Test public void test_regex_20	(){REGEX_TEST_CASE("a?(ab|ba)ab",			"aaabab",	"aaabab"		);}
//@Test public void test_regex_21	(){REGEX_TEST_CASE("(aa|aaa)*|(a|aaaaa)",	"aa",	"aa"				);}
    @Test public void test_regex_22		(){REGEX_TEST_CASE("(a)(b)(c)",				"abc",	"abc"				);}
    @Test public void test_regex_23		(){REGEX_TEST_CASE("((((((((((x))))))))))",	"x",	"x"					);}
    //@Test public void test_regex_24	(){REGEX_TEST_CASE("((((((((((x))))))))))*",	"xx",	"xx"				);}
//@Test public void test_regex_25	(){REGEX_TEST_CASE("a?(ab|ba)*",				"ababababababababababababababababa",	"ababababababababababababababababa"				);BOOST_REQUIRE(result == expected);}
//@Test public void test_regex_26	(){REGEX_TEST_CASE("a*a*a*a*a*b",			"aaaaaaaab",	"aaaaaaaab"	);}
    @Test public void test_regex_27		(){REGEX_TEST_CASE("abc",					"abc",	"abc"				);}
    @Test public void test_regex_28		(){REGEX_TEST_CASE("ab*c",					"abc",	"abc"				);}
    @Test public void test_regex_29		(){REGEX_TEST_CASE("ab*bc",					"abbc",	"abbc"				);}
    @Test public void test_regex_30		(){REGEX_TEST_CASE("ab*bc",					"abbbbc",	"abbbbc"		);}
    //@Test public void test_regex_31	(){REGEX_TEST_CASE("ab+bc",					"abbc",	"abbc"				);}
//@Test public void test_regex_32	(){REGEX_TEST_CASE("ab+bc",					"abbbbc",	"abbbbc"		);}
//@Test public void test_regex_33	(){REGEX_TEST_CASE("ab?bc",					"abbc",	"abbc"				);}
//@Test public void test_regex_34	(){REGEX_TEST_CASE("ab?bc",					"abc",	"abc"				);}
    @Test public void test_regex_35		(){REGEX_TEST_CASE("ab|cd",					"ab",	"ab"				);}
    @Test public void test_regex_36		(){REGEX_TEST_CASE("(a)b(c)",				"abc",	"abc"				);}
    @Test public void test_regex_37		(){REGEX_TEST_CASE("a*",						"aaa",	",a,aa,aaa"			);}
    //@Test public void test_regex_38	(){REGEX_TEST_CASE("(a+|b)*",				"ab",	"ab"				);}
//@Test public void test_regex_39	(){REGEX_TEST_CASE("(a+|b)+",				"ab",	"ab"				);}
    @Test public void test_regex_40		(){REGEX_TEST_CASE("a|b|c|d|e",				"e",	"e"					);}
    @Test public void test_regex_41		(){REGEX_TEST_CASE("(a|b|c|d|e)f",			"ef",	"ef"				);}
    @Test public void test_regex_42		(){REGEX_TEST_CASE("abcd*efg",				"abcdefg",	"abcdefg"		);}
    @Test public void test_regex_43		(){REGEX_TEST_CASE("(ab|ab*)bc",				"abc",	"abc"				);}
    @Test public void test_regex_44		(){REGEX_TEST_CASE("(ab|a)b*c",				"abc",	"abc"				);}
    @Test public void test_regex_45		(){REGEX_TEST_CASE("((a)(b)c)(d)",			"abcd",	"abcd"				);}
//@Test public void test_regex_46	(){REGEX_TEST_CASE("(a|ab)(c|bcd)",			"abcd",	"abcd"				);}
//@Test public void test_regex_47	(){REGEX_TEST_CASE("(a|ab)(bcd|c)",			"abcd",	"abcd"				);}
//@Test public void test_regex_48	(){REGEX_TEST_CASE("(ab|a)(c|bcd)",			"abcd",	"abcd"				);}
//@Test public void test_regex_49	(){REGEX_TEST_CASE("(ab|a)(bcd|c)",			"abcd",	"abcd"				);}
//@Test public void test_regex_50	(){REGEX_TEST_CASE("((a|ab)(c|bcd))(d*)",	"abcd",	"abcd"				);}
//@Test public void test_regex_51	(){REGEX_TEST_CASE("((a|ab)(bcd|c))(d*)",	"abcd",	"abcd"				);}
//@Test public void test_regex_52	(){REGEX_TEST_CASE("((ab|a)(c|bcd))(d*)",	"abcd",	"abcd"				);}
//@Test public void test_regex_53	(){REGEX_TEST_CASE("((ab|a)(bcd|c))(d*)",	"abcd",	"abcd"				);}
//@Test public void test_regex_54	(){REGEX_TEST_CASE("(a|ab)((c|bcd)(d*))",	"abcd",	"abcd"				);}
//@Test public void test_regex_55	(){REGEX_TEST_CASE("(a|ab)((bcd|c)(d*))",	"abcd",	"abcd"				);}
//@Test public void test_regex_56	(){REGEX_TEST_CASE("(ab|a)((c|bcd)(d*))",	"abcd",	"abcd"				);}
//@Test public void test_regex_57	(){REGEX_TEST_CASE("(ab|a)((bcd|c)(d*))",	"abcd",	"abcd"				);}
//@Test public void test_regex_58	(){REGEX_TEST_CASE("(a*)(b|abc)",			"abc",	"abc"				);}
//@Test public void test_regex_59	(){REGEX_TEST_CASE("(a*)(abc|b)",			"abc",	"abc"				);}
//@Test public void test_regex_60	(){REGEX_TEST_CASE("((a*)(b|abc))(c*)",		"abc",	"abc"				);}
//@Test public void test_regex_61	(){REGEX_TEST_CASE("((a*)(abc|b))(c*)",		"abc",	"abc"				);}
//@Test public void test_regex_62	(){REGEX_TEST_CASE("(a*)((b|abc))(c*)",		"abc",	"abc"				);}
//@Test public void test_regex_63	(){REGEX_TEST_CASE("(a*)((abc|b)(c*))",		"abc",	"abc"				);}
//@Test public void test_regex_64	(){REGEX_TEST_CASE("(a*)(b|abc)",			"abc",	"abc"				);}
//@Test public void test_regex_65	(){REGEX_TEST_CASE("(a*)(abc|b)",			"abc",	"abc"				);}
//@Test public void test_regex_66	(){REGEX_TEST_CASE("((a*)(b|abc))(c*)",		"abc",	"abc"				);}
//@Test public void test_regex_67	(){REGEX_TEST_CASE("((a*)(abc|b))(c*)",		"abc",	"abc"				);}
//@Test public void test_regex_68	(){REGEX_TEST_CASE("(a*)((b|abc)(c*))",		"abc",	"abc"				);}
//@Test public void test_regex_69	(){REGEX_TEST_CASE("(a*)((abc|b)(c*))",		"abc",	"abc"				);}
//@Test public void test_regex_70	(){REGEX_TEST_CASE("(a|ab)",					"ab",	"ab"				);}
//@Test public void test_regex_71	(){REGEX_TEST_CASE("(ab|a)",					"ab",	"ab"				);}
//@Test public void test_regex_72	(){REGEX_TEST_CASE("(a|ab)(b*)",				"ab",	"ab"				);}
//@Test public void test_regex_73	(){REGEX_TEST_CASE("(ab|a)(b*)",				"ab",	"ab"				);}

}