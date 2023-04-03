package AST;

import Game.Player;

public class MoveCommandNode implements ExprNode {
    protected String direction;

    Player player;
    public MoveCommandNode(String direction, Player player){
        this.direction = direction;
        this.player = player;
    }
    @Override
    public long evaluate() {
        player.move(direction);
        return 0;
    }

    @Override
    public void print(StringBuilder s) {
        s.append("move ");
        s.append(direction);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        print(builder);
        return builder.toString();
    }
}
