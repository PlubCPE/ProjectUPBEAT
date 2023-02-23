package Tokenizer;

public class Token implements Tokenizer{
    private final String src;
    private String next,prev;
    private  int pos, line;

    public Token(String src){
        this.src = src;
        pos = 0;
        line = 1;
        ComputeNext();
    }

    @Override
    public boolean HasNextToken(){
        return next != null;
    }

    @Override
    public String Peek(){
        if(next == null){
            throw  new RuntimeException();
        }
        return next;
    }

    @Override
    public String Consume(){
        if(!HasNextToken()){
            throw new RuntimeException();
        }
        String result = next;
        ComputeNext();
        return result;
    }

    @Override
    public boolean Peek(String s){
        if(!HasNextToken()){
            return false;
        }
        return Peek().equals(s);
    }

    @Override
    public boolean Consume(String s){
        if(!HasNextToken()){
            return false; // มาแก้ throw exception ด้วย
        }else {
            if (Peek().equals(s)) {
                ComputeNext();
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public int getLine(){
        return line;
    }

    public boolean isCharacter(char c){
        return Character.isLetter(c);
    }

    public boolean IgnoreCharacter(char c){
        return Character.isWhitespace(c) || c == '"';
    }

    public void ComputeNext(){
        if(!HasNextToken()) return;
        StringBuilder sb = new StringBuilder();
        while (pos < src.length() && IgnoreCharacter(src.charAt(pos))) {
            if (src.charAt(pos) == '\n') {
                line++;
            } else {
                pos++;
            }
        }
        if(pos == src.length()){
            next = null;
            return;
        }
        char c = sb.charAt(pos);
        if(Character.isDigit(c)){
            sb.append(c);
            while(pos < src.length() && Character.isDigit(src.charAt(pos))){
                sb.append(src.charAt(pos));
                pos++;
            }
        }else if(isCharacter(c)){
            sb.append(c);
            while(pos < src.length() && Character.isDigit(src.charAt(pos))){
                sb.append(src.charAt(pos));
                pos++;
            }
        }else if("()+-*/%^{}=".contains(String.valueOf(c))){
            sb.append(src.charAt(pos));
            pos++;
        }else {
            throw new RuntimeException();
        }
        prev = next;
        next = sb.toString();
    }
}
