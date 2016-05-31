package il.ac.technion.cs.ssdl.lola.parser;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import il.ac.technion.cs.ssdl.lola.bunnies.ConcreteBunny;
import il.ac.technion.cs.ssdl.lola.bunnies.KeywordType;
import il.ac.technion.cs.ssdl.lola.bunnies.keywords.FindKeywordType;

public class Tokenizer {
	private Lexer lexer;
	
	public Tokenizer(Reader stream){
		List<KeywordType> keywordTypes = new ArrayList<>();
        keywordTypes.add(new FindKeywordType());
        
		lexer = new Lexer(stream, keywordTypes);
	}
	
	public Token next() throws IOException, ParseException{
		ConcreteBunny b = lexer.read();
		if(null == b)
			return null;
		return new Token(b.getLine(), b.getColumn(), b.getText(), null);
	}
	
	public boolean hasNext() throws IOException{
		return lexer.hasMore();
	}
}
