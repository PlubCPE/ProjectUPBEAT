package Tokenizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {
    private Token tokenizer;

    @Test
    public void TestHasNextToken() {
        tokenizer = new Token(null);
        assertFalse(tokenizer.HasNextToken());
        tokenizer = new Token("");
        assertFalse(tokenizer.HasNextToken());
        tokenizer = new Token("s");
        assertFalse(tokenizer.HasNextToken());

    }

    @Test
    public void TestPeek() {
        tokenizer = new Token(null);
        assertThrows(TokenizerException.NoToken.class, tokenizer::Peek);
        tokenizer = new Token("");
        assertThrows(TokenizerException.NoToken.class, tokenizer::Peek);
        tokenizer = new Token("s");
        assertEquals("a",tokenizer.Peek());
    }

    @Test
    public void TestConsume() {
        tokenizer = new Token(null);
        assertThrows(TokenizerException.NoToken.class, tokenizer::Consume);
        tokenizer = new Token("");
        assertThrows(TokenizerException.NoToken.class, tokenizer::Consume);
        tokenizer = new Token("s");
        assertEquals("a",tokenizer.Consume());
    }

    @Test
    public void TestStringPeek() {
        tokenizer = new Token(null);
        assertFalse(tokenizer.Peek(""));
        assertFalse(tokenizer.Peek("s"));
        tokenizer = new Token("");
        assertFalse(tokenizer.Peek(""));
        assertFalse(tokenizer.Peek("s"));
        tokenizer = new Token("s");
        assertTrue(tokenizer.Peek("s"));
        tokenizer = new Token("star");
        assertFalse(tokenizer.Peek("s"));
        assertTrue(tokenizer.Peek("star"));
    }

    @Test
    public void TestStringConsume() {
        tokenizer = new Token(null);
        assertThrows(TokenizerException.NoToken.class, () -> tokenizer.Consume(""));
        assertThrows(TokenizerException.NoToken.class, () -> tokenizer.Consume("a"));
        tokenizer = new Token("");
        assertThrows(TokenizerException.NoToken.class, () -> tokenizer.Consume(""));
        assertThrows(TokenizerException.NoToken.class, () -> tokenizer.Consume("a"));
        tokenizer = new Token("s");
        assertTrue(tokenizer.Consume("s"));
        tokenizer = new Token("s");
        assertFalse(tokenizer.Consume("star"));
        assertTrue(tokenizer.Consume("s"));
        tokenizer = new Token("star");
        assertFalse(tokenizer.Consume("s"));
        assertTrue(tokenizer.Consume("star"));
    }
}