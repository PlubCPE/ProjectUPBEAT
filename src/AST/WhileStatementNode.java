package AST;

public class WhileStatementNode implements ExprNode {
    protected ExprNode ExprNode;
    protected ExprNode StatementNode;
    public WhileStatementNode(ExprNode ExprNode, ExprNode StatementNode){
        this.ExprNode = ExprNode;
        this.StatementNode = StatementNode;
    }
    @Override
    public long evaluate() {
        for (int counter = 0; counter < 10000 && ExprNode.evaluate() > 0; counter++){
            StatementNode.evaluate();
        }
        return 0;
    }

    @Override
    public void print(StringBuilder s) {
        s.append("while (");
        ExprNode.print(s);
        s.append(") ");
        StatementNode.print(s);
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        print(builder);
        return builder.toString();
    }
}
