package AST;

public interface ExprNode {
     long evaluate();
     String toString();
     void print(StringBuilder s);

}