package Tokenizer;

public interface Tokenizer {
    /** Return true if there is more token */
    boolean HasNextToken();

    /** Return the next token in the input stream */
    String Peek();

    /**
     * Consume the next token from the input stream and return it
     * effect: removes the next token from the input stream
     * */
    String Consume();

    /** Returns true if
     *  the next token (if any) is s.
     *  */
    boolean Peek(String s);

    /** Consumes the next token if it is s.
     *  effects: removes the next token
     *           from input stream if it is s
     */
    boolean Consume(String s);


    /** return: line*/
    int getLine();


}
