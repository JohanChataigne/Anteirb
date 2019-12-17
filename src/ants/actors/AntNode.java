package ants.actors;

import ants.environment.Cell;
import ants.environment.FoodNode;
import io.jbotsim.core.Node;

import java.util.ArrayList;
import java.util.Random;

public class AntNode extends WaypointNode {

    public ArrayList<FoodNode> foodNodes = new ArrayList<FoodNode>();

    public AntNode(){
        super();
        setIcon("/resources/images/ant.png");
        setSensingRange(50);
    }

    @Override
    public void onStart() {
        super.onStart();
        setLocation(currentCell);
    }

    @Override
    public void onClock() { super.onClock(); }

    protected void antAlgorithm() {
        /* s'il y a de la nourriture */
        if(!foodNodes.isEmpty())
            addDestination(foodNodes.remove(0).currentCell);
        else {
            /* une fois arrivee a destination, la fourmi prends un case aléatoire adjacente
             * et l'ajoute a ses destinations */
            Cell cell = pickNeighBoringCell();
            setCurrentCell(cell);
            addDestination(cell);
        }
    }

    protected Cell pickNeighBoringCell() {
        Cell nextCell = null;
        Random random = new Random();
        int rInt;

        while(nextCell == null) {
            rInt = random.nextInt(4);

            switch (rInt) {
                case 0:
                    nextCell = getCurrentCell().getTopNeighbor();
                    break;
                case 1:
                    nextCell = getCurrentCell().getRightNeighbor();
                    break;
                case 2:
                    nextCell = getCurrentCell().getBottomNeighbor();
                    break;
                case 3:
                    nextCell = getCurrentCell().getLeftNeighbor();
                    break;
                default:
                    /* erreur */
                    break;
            }
        }
        return nextCell;
    }

    public void takeFood(FoodNode node) {
        System.out.println("MIAM LA NOURRITURE");
        /* on descend la quantité de nourriture dans la case */
        node.setQuantity(node.getQuantity() -1);

        /* on change d'image */
        setIcon("/images/ant-bean.png");
    }

    public void dropFood() {
        // TODO
    }

    @Override
    /* lorsquon arrive a notre case on ajoute une case suivante */
    public void onArrival() {
        for (FoodNode n : foodNodes) {
            /* si on est sur une cellule contenant de la nourriture */
            if(distance(n) <= 1)
                takeFood(n);
        }
        antAlgorithm();
    }

    @Override
    public void onSensingIn(Node node) {
        super.onSensingIn(node);

        /* si le noeud est de la nourriture alors ce sera notre prochaine destinations */
        if(node instanceof FoodNode) {
            foodNodes.add((FoodNode)node);
        }
    }

}
