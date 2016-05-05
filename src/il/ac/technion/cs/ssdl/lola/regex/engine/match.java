package il.ac.technion.cs.ssdl.lola.regex.engine;

import il.ac.technion.cs.ssdl.lola.bunnies.Token;

import java.util.List;

/// <summary>
/// A class representing a found match.
/// </summary>
public class match
        {
        /// <summary>
        /// The matched tokens.
        /// </summary>
        List<Token> matched_text;

        /// <summary>
        /// The index of the starting token in the original token list.
        /// </summary>
        int start_index;

        match(List<Token> text, int start_index, int end_index) {
            this.start_index = start_index;
            matched_text = text.subList(start_index, end_index);
        }

        match(Token matched_token, int start_index){
            matched_text.add(matched_token);
            this.start_index=start_index;
        }
        }
