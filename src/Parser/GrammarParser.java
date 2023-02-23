package Parser;

import AST.ExprNode;
import Tokenizer.Tokenizer;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.IfTree;
import jdk.dynalink.Operation;

import javax.crypto.IllegalBlockSizeException;
import java.beans.Expression;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.zip.InflaterInputStream;

public class GrammarParser implements Parser {
    /**Grammar for Construction Plan
     * Plan → Statement+
     * Statement → Command | BlockStatement | IfStatement | WhileStatement
     * Command → AssignmentStatement | ActionCommand
     * AssignmentStatement → <identifier> = Expression
     * ActionCommand → done | relocate | MoveCommand | RegionCommand | AttackCommand
     * MoveCommand → move Direction
     * RegionCommand → invest Expression | collect Expression
     * AttackCommand → shoot Direction Expression
     * Direction → up | down | upleft | upright | downleft | downright
     * BlockStatement → { Statement* }
     * IfStatement → if ( Expression ) then Statement else Statement
     * WhileStatement → while ( Expression ) Statement
     * Expression → Expression + Term | Expression - Term | Term
     * Term → Term * Factor | Term / Factor | Term % Factor | Factor
     * Factor → Power ^ Factor | Power
     * Power → <number> | <identifier> | ( Expression ) | InfoExpression
     * InfoExpression → opponent | nearby Direction
     *
     * + means at least one
     * * means zero or more
     * <number> is any nonnegative integer literal that can be stored as Java's long data type.
     * <identifier> is any string not a reserved word.
     * Identifiers must start with a letter, followed by zero or more alphanumeric characters.
     *
     * The following strings are reserved words and cannot be used as identifiers:
     * collect, done, down, downleft, downright, else, if, invest, move, nearby, opponent
     * relocate, shoot, then, up, upleft, upright, while */

    private static final Set<String> ReserveWords = new HashSet<>(Arrays.asList(
            "collect", "done", "down", "downleft", "downright", "else", "if", "invest", "move", "nearby",
            "opponent", "relocate", "shoot", "then", "up", "upleft", "upright", "while"));

    private final Tokenizer tokenizer;

    public GrammarParser(Tokenizer tokenizer){
        this.tokenizer = tokenizer;
    }

    @Override
    public ExprNode Parse(){
        ExprNode statement = ParsePlan();
        if(tokenizer.HasNextToken()){
            throw new ASTException(); // not yet done
        }
        return statement;
    }

    private ExprNode ParsePlan(){
        ExprNode statements = ParseStatement();
        statements.next = ParseStatement();
        return statements;
    }

    /** Statement → Command | BlockStatement | IfStatement | WhileStatement */
    private ExprNode ParseStatement(){
        if(tokenizer.Peek("if")) {
            return ParseIfStatement();
        }else if(tokenizer.Peek("while")){
            return ParseWhileStatement();
        }else if(tokenizer.Peek("{")){
            return ParseBlockStatement();
        }else {
            return ParseCommand();
        }
    }

    /** Command → AssignmentStatement | ActionCommand */
    private ExprNode ParseCommand(){
        if(tokenizer.Peek("<identifier>")){
            return ParseAssignmentStatement();
        }else {
            String command = tokenizer.Consume();
            switch (command){
                case "done":
                    return new DoneCommand();
                case "relocate":
                    return new RelocateCommand();
                case "move":
                    return new ParseMoveCommand();
                case "invest":
                    return new RegionCommand();
                case "collect":
                    return new AttackCommand();
                case "shoot":
                    return new ParseAttackCommand();
                default:
                    return new ParseException("Invalid command: ",1);
            }
        }
    }

    /** AssignmentStatement → <identifier> = Expression */
    private ExprNode ParseAssignmentStatement(){
        String var = tokenizer.Consume();
        tokenizer.Consume("=");
        Expression expression = ParseExpression();
        return new ExprNode(var,expression);
    }

    /** MoveCommand → move Direction */
    private ExprNode ParseMoveCommand(){
        tokenizer.Consume("move");
        Direction direction = ParseDirection();
        return new MoveCommand(direction);
    }

    /** AttackCommand → shoot Direction Expression */
    private ExprNode ParseAttackCommand(){
        tokenizer.Consume("shoot");
        Direction direction = ParseDirection();
        Expression power = ParseExpression();
        return new AttackCommand(direction,power);
    }

    /** BlockStatement → { Statement* } */
    private ExprNode ParseBlockStatement(){
        ExprNode statements ;
        tokenizer.Consume("{");
        statements = ParseStatement();
        tokenizer.Consume("}");
        return statements;
    }

    /** IfStatement → if ( Expression ) then Statement else Statement */
    private ExprNode ParseIfStatement(){
        tokenizer.Consume("if");
        tokenizer.Consume("(");
        ExprNode condition = ParseExpression();
        tokenizer.Consume(")");
        Statement thenBranch = ParseStatement();
        if(tokenizer.Peek("else")){
            tokenizer.Consume("else");
            Statement elseBranch = ParseStatement();
            return new IfStatement(condition,thenBranch,elseBranch);
        }else {
            return new IfStatement(condition,thenBranch);
        }
    }

    /** WhileStatement → while ( Expression ) Statement */
    private ExprNode ParseWhileStatement(){
        tokenizer.Consume("While");
        tokenizer.Consume("(");
        Expression condition = ParseExpression();
        tokenizer.Consume(")");
        ExprNode body = ParseStatement();
        return new WhileStatement(condition,body);
    }

    /** Expression → Expression + Term | Expression - Term | Term */
    private Expression ParseExpression(){
        Expression expression = ParseTerm();
        while (tokenizer.Peek("\\+|-")){
            String operator = tokenizer.Consume();
            Term term = ParseTerm();
            switch (operator){
                case "+":
                    expression = new AddExpression(expression,term);
                    break;
                case "-":
                    expression = new SubExpression(expression,term);
            }
        }
    }

    /** Term → Term * Factor | Term / Factor | Term % Factor | Factor */
    private Term ParseTerm(){
        Expression expression = ParseFactor();
        while (tokenizer.Peek("*") || tokenizer.Peek("/") || tokenizer.Peek("%")){
            String operator = tokenizer.Consume();
            Factor factor = ParseFactor();
            expression = new Operation(expression,operator,factor);
        }
        return expression;
    }

    /** Factor → Power ^ Factor | Power */
    private Factor ParseFactor(){
        Expression expression = ParsePower();
        while (tokenizer.Peek("^")){
            String operator = tokenizer.Consume();
            Power power = ParseFactor();
            expression = new Operation(expression,operator,power);
        }
        return  expression;
    }

    /**  Power → <number> | <identifier> | ( Expression ) | InfoExpression */
    private Power ParsePower(){
        if(Character.isDigit(tokenizer.Peek().charAt(0))){
            return new Constant(Integer.parseInt(tokenizer.Consume()));
        }else if(tokenizer.Peek("opponent") || tokenizer.Peek("nearby")){
            return ParseInfoExpression();
        }else if(tokenizer.Peek("(")){
            tokenizer.Consume("(");
            Expression expression = ParseExpression();
            tokenizer.Consume(")");
            return expression;
        }
        return new Constant(tokenizer.Consume());
    }

    private InfoExpression ParseInfoExpression(){
        if(tokenizer.Peek("opponent")){
            tokenizer.Consume();
            return new Opponent();
        }else if(tokenizer.Peek("nearby")){
            tokenizer.Consume();
            Direction direction = ParseDirection();
            return new Nearby(direction);
        }else {
            throw new InvalidInfoExpression(tokenizer.Peek(),tokenizer.getline());
        }
    }
}
