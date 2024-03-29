package AST;

import Game.Player;

public class RelocateNode implements ExprNode{
    Player player;

    public RelocateNode(Player player){
        this.player = player;
    }
    @Override
    public long evaluate() {
        player.relocate();
        return 0;
    }

    @Override
    public void print(StringBuilder s) {
        s.append("relocate");
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        print(builder);
        return builder.toString();
    }

}
