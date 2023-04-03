package AST;

public class AssignmentStatementNode implements ExprNode{
    protected IdentifierNode variable;
    protected ExprNode expression;

    public AssignmentStatementNode(IdentifierNode variable, ExprNode expression){
        this.variable = variable;
        this.expression = expression;
    }


    @Override
    public long evaluate() {
        variable.assignValue(expression.evaluate());
        return 0;
    }

    @Override
    public void print(StringBuilder s) {
        s.append(variable);
        s.append("=");
        expression.print(new StringBuilder("" + expression.evaluate()));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        print(builder);
        return builder.toString();
    }
}
