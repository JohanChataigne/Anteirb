package ants.actors;

import java.util.Random;

public class QueenNode extends CellLocatedNode {

    private int foodStock;

    public QueenNode(){
        super();
        foodStock = 10;
        setIcon("/resources/images/ant-queen.png");
        setIconSize(getIconSize()*2);
    }

    @Override
    public void onClock() {

        if (shouldProduceOffspring())
            produceOffspring();

    }

    @Override
    public void onPostClock() {
        super.onPostClock();
    }

    private boolean shouldProduceOffspring() {
        return new Random().nextDouble() < 0.01;
    }

    public void produceOffspring(){
        if(foodStock <= 0)
            return;

        foodStock--;

        AntNode babyAnt = new AntNode();
        babyAnt.setCurrentCell(getCurrentCell());
        getTopology().addNode(babyAnt);
    }

}
