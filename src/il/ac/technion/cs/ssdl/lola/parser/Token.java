package il.ac.technion.cs.ssdl.lola.parser;

public class Token {
	private int row;
	private int col;
	private String text;
	private String classification;

	public Token(int _row, int _col, String _text, String _classification){
		row = _row;
		col = _col;
		text = _text;
		classification = _classification;
	}
	
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public String getText() {
		return text;
	}

	public String getClassification() {
		return classification;
	}

}
