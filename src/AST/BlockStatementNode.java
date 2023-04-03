package AST;

import java.util.ArrayList;
import java.util.List;

public class BlockStatementNode implements ExprNode{
    protected List<ExprNode> statements;
    public BlockStatementNode(){
        statements = new ArrayList<>();
    }

    public void addStatement(ExprNode statementNode){
        statements.add(statementNode);
    }

    @Override
    public long evaluate(){
        for(ExprNode statement : statements){
            statement.evaluate();
        }
        return 0;
    }

    @Override
    public void print(StringBuilder s){
        s.append("{");
        for (ExprNode statement : statements){
            statement.print(s);
        }
        s.append("}");
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        print(builder);
        return builder.toString();
    }
}
