package ants.environment;

import ants.actors.CellLocatedNode;

import java.util.Random;

public class FoodNode extends CellLocatedNode {

    public static final int MAX_QUANTITY = 10;
    public static final int MIN_QUANTITY = 10;

    /* temps de survie mini et maxi de la nourriture */
    private static final int MIN_TTL = 1000;
    private static final int MAX_TTL = 5000;
    public int TTL;

    private int quantity;

    public FoodNode(){
        super();
        setIcon("/resources/images/ant-worm.png");
        setWirelessStatus(false);

        setDirection(new Random().nextDouble()*2*Math.PI);
        quantity = new Random().nextInt(MAX_QUANTITY) + MIN_QUANTITY;

        TTL = new Random().nextInt(MAX_TTL) + MIN_TTL;

        setIconSize((int)(getIconSize()* quantity /10*0.9));
    }

    @Override
    public void onPostClock() {
        TTL--;
        if(TTL <= 0)
            die();

        super.onPostClock();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (quantity <= 0) {
            System.out.println("PLUS DE NOURRITURE");
            die();
        }
    }

    public int getQuantity(){
        return quantity;
    }

    public boolean hasFood() {
        return this.getQuantity() >= 1;
    }

}
