package AST;

public class BinaryArithExprNode implements ExprNode{
    protected ExprNode left, right;
    protected String op;

    public BinaryArithExprNode(ExprNode left, String op, ExprNode right){
        this.left = left;
        this.op = op;
        this.right = right;

    }
    @Override
    public long evaluate() throws ArithmeticException {
        long lv = left.evaluate();
        long rv = right.evaluate();
        if (op.equals("+")) return lv + rv;
        if (op.equals("-")) return lv - rv;
        if (op.equals("*")) return lv * rv;
        if (op.equals("/")){
            if (rv == 0){
                throw new ArithmeticException("Cannot divide by zero");
            }
            return lv / rv;
        }
        if (op.equals("%")){
            if (rv == 0)
                throw new ArithmeticException("Cannot modulo by zero");
            return lv % rv;
        }
        if (op.equals("^")) return (long) Math.pow(lv,rv);
        throw new ArithmeticException("unknown op : " + op);
    }


    @Override
    public void print(StringBuilder s) {
        s.append("(");
        left.print(s);
        s.append(" ");
        s.append(op);
        s.append(" ");
        right.print(s);
        s.append(")");
    }
}
