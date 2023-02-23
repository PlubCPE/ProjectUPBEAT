package AST;

public abstract class ExprNode {
    public abstract long Eval(long game);
    public abstract String toString();
    public ExprNode next;
}
