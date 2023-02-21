package Parser;

public class GrammarParser {
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


}
