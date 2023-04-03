package Parser;

import AST.AssignmentStatementNode;
import AST.BlockStatementNode;
import AST.ExprNode;
import AST.IdentifierNode;
import Tokenizer.Tokenizer;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.IfTree;
import jdk.dynalink.Operation;

import javax.crypto.IllegalBlockSizeException;
import javax.print.attribute.standard.DocumentName;
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

    private final List<String> commands = Arrays.stream(
            new String[]{"done", "relocate", "move", "invest", "collect", "shoot"}
    ).toList();
    private static final List<String> ReserveWords = Arrays.stream( new String[]{
            "collect", "done", "down", "downleft", "downright", "else", "if", "invest", "move", "nearby",
            "opponent", "relocate", "shoot", "then", "up", "upleft", "upright", "while"}).toList();

    private final Tokenizer tokenizer;

    public GrammarParser(Tokenizer tokenizer){
        this.tokenizer = tokenizer;
    }

    @Override
    public ExprNode Parse() throws SyntaxError {
        ExprNode statement = ParsePlan();
        if(tokenizer.HasNextToken()){
            throw new SyntaxError("leftover token");
        }
        return statement;
    }

    /** Plan → Statement+ */
    private ExprNode ParsePlan() throws SyntaxError{
        BlockStatementNode statements = new BlockStatementNode();
        statements.addStatement(ParseStatement());
        while(tokenizer.HasNextToken()){
            statements.addStatement(ParseStatement());
        }
        return statements;
    }

    /** Statement → Command | BlockStatement | IfStatement | WhileStatement */
    private ExprNode ParseStatement() throws SyntaxError{
        if(tokenizer.Peek("if")) {
            return ParseIfStatement();
        }else if(tokenizer.Peek("while")){
            return ParseWhileStatement();
        }else if(tokenizer.Peek("{")){
            return ParseBlockStatement();
        }else{
            return ParseCommand();
        }
    }

    /** Command → AssignmentStatement | ActionCommand */
    private ExprNode ParseCommand(){
        if(commands.contains(tokenizer.Peek())){
            return ParseActionCommand();
        }else {
            return  ParseAssignmentStatement();
            }
        }



    /** AssignmentStatement → <identifier> = Expression */
    private ExprNode ParseAssignmentStatement() throws SyntaxError{
        IdentifierNode identifier = ParseIdentifier();
        if (tokenizer.Peek("="))
            tokenizer.Consume();
        else
            throw new SyntaxError("command not found");
        ExprNode expression = ParseExpression();
        return new AssignmentStatementNode(identifier, expression);
    }


    /** ActionCommand → done | relocate | MoveCommand | RegionCommand | AttackCommand */
    private ExprNode ParseActionCommand() throws SyntaxError {
        String command = tokenizer.Consume();
        switch (command) {
            case "done":
                return  ParseDoneCommand();
            case "relocate":
                return  ParseRelocateCommand();
            case "move":
                return  ParseMoveCommand();
            case "invest":
                return  ParseInvestCommand();
            case "collect":
                return  ParseCollectCommand();
            case "shoot":
                return  ParseAttackCommand();
            default:
                throw new ParserException("Invalid command: " + tokenizer.getLine());
        }
    }


    private ExprNode ParseDoneCommand() throws SyntaxError{
        tokenizer.Consume("done");
        ExprNode DoneCommandNode = new DoneNode();
        return DoneCommandNode;

    }


    /** MoveCommand → move Direction */
    private ExprNode ParseMoveCommand(){
        tokenizer.Consume("move");
        Direction direction = ParseDirection();
        return new MoveCommand(direction);
    }


    private ExprNode ParseInvestCommand(){
        tokenizer.Consume("invest");
        ExprNode exprS = ParseExpression();
        return new InvestCommand(exprS);
    }


    private ExprNode ParseCollectCommand(){
        tokenizer.Consume("collect");
        ExprNode expression = ParseExpression();
        return new CollectCommand(expression);
    }

    /** AttackCommand → shoot Direction Expression */
    private ExprNode ParseAttackCommand(){
        tokenizer.Consume("shoot");
        Direction direction = ParseDirection();
        ExprNode power = ParseExpression();
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
        ExprNode condition = ParseExpression();
        tokenizer.Consume(")");
        ExprNode body = ParseStatement();
        return new WhileStatement(condition,body);
    }

    /** Expression → Expression + Term | Expression - Term | Term */
    private ExprNode ParseExpression() throws ParserException{
        ExprNode expression = ParseTerm();
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
    private ExprNode ParseTerm(){
        ExprNode expression = ParseFactor();
        while (tokenizer.Peek("*") || tokenizer.Peek("/") || tokenizer.Peek("%")){
            String operator = tokenizer.Consume();
            ExprNode factor = ParseFactor();
            expression = new Operation(expression,operator,factor);
        }
        return expression;
    }

    /** Factor → Power ^ Factor | Power */
    private ExprNode ParseFactor() throws ParserException{
        ExprNode expression = ParsePower();
        while (tokenizer.Peek("^")){
            String operator = tokenizer.Consume();
            ExprNode power = ParseFactor();
            expression = new Operation(expression,operator,power);
        }
        return  expression;
    }

    /**  Power → <number> | <identifier> | ( Expression ) | InfoExpression */
    private ExprNode ParsePower() throws ParserException {
        if(Character.isDigit(tokenizer.Peek().charAt(0))){
            return new Constant(Integer.parseInt(tokenizer.Consume()));
        }else if(tokenizer.Peek("opponent") || tokenizer.Peek("nearby")){
            return ParseInfoExpression();
        }else if(tokenizer.Peek("(")){
            tokenizer.Consume("(");
            ExprNode expression = ParseExpression();
            tokenizer.Consume(")");
            return expression;
        }
        return new Constant(tokenizer.Consume());
    }


    /** InfoExpression → opponent | nearby Direction */
    private ExprNode ParseInfoExpression() throws ParserException {
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


    public IdentifierNode ParseIdentifier() throws SyntaxError {
        String identifier = tokenizer.Peek();
        if (identifier.matches(Regex.Random)){
            tokenizer.Consume(Regex.Random);
            IdentifierNode randomNode = new IdentifierNode(identifier, player.getVariable());
            return randomNode;
        }
        tokenizer.Consume(Regex.Variable);

        IdentifierNode identifierNode = new IdentifierNode(identifier, player.getVariable());
        return identifierNode;
    }
}
