package AST;


import Game.Player;

public class RegionCommandNode implements ExprNode{ // invest, collect
    protected String financeMode;
    protected Player player;
    protected ExprNode Expression;

    public RegionCommandNode(String financeMode,ExprNode Expression, Player player){
        this.financeMode = financeMode;
        this.Expression = Expression;
        this.player = player;
    }
    @Override
    public long evaluate() { //todo implement RegionCommand
//        throw new NotImplementYet();
        if (financeMode.equals("invest")){
            player.invest(Expression.evaluate());
        }else if (financeMode.equals("collect")){
            player.collect(Expression.evaluate());
        }
        return 0;
    }

    @Override
    public void print(StringBuilder s) {
        s.append(financeMode);
        s.append(" " + Expression.evaluate());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        print(builder);
        return builder.toString();
    }

}
