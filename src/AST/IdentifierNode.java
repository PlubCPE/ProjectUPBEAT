package AST;

import java.util.Map;

public class IdentifierNode implements ExprNode{
    protected Map<String,Long> var;
    protected String identifier;

    public IdentifierNode(String identifier,Map<String,Long> var){
        this.identifier = identifier;
        this.var = var;
    }
    @Override
    public long evaluate() {
        if (!var.containsKey(identifier)){
            var.put(identifier,0L);
        }
        return var.get(identifier);
    }

    @Override
    public void print(StringBuilder s) {
        s.append(identifier);
    }

    public void assignValue(Long value){
        var.put(identifier,value);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        print(builder);
        return builder.toString();
    }
}
