package ants.actors;

import ants.environment.Cell;
import ants.environment.FoodNode;
import io.jbotsim.core.Node;
import io.jbotsim.core.Point;

import java.util.ArrayList;
import java.util.Random;

public class AntNode extends WaypointNode {

    /* temps de survie mini et maxi de la fourmi */
    private static final int MIN_TTL = 500;
    private static final int MAX_TTL = 1000;
    public int TTL;

    public boolean carryingFood;

    /* reine mere à qui deposer de la nourriture */
    public QueenNode queen;
    /* chemin inverse pour aller jusqua la reine */
    public ArrayList<Cell> pathToQueen = new ArrayList<Cell>();

    /* liste de noeuds à visiter ou trouver de la nourriture */
    public ArrayList<FoodNode> foodNodes = new ArrayList<FoodNode>();

    public AntNode(QueenNode mother){
        super();
        this.queen = mother;
        carryingFood = false;

        TTL = new Random().nextInt(MAX_TTL) + MIN_TTL;

        setIcon("/resources/images/ant.png");
        setSensingRange(50);
    }

    @Override
    public void onStart() {
        super.onStart();
        setLocation(currentCell);
    }

    @Override
    public void onClock() {
        TTL--;
        if(TTL <= 0)
            die();

        super.onClock();
    }

    protected void antAlgorithm() {
        /* on suit le trajet vers la reine */
        if(carryingFood && !pathToQueen.isEmpty()) {
            addDestination(pathToQueen.remove(0));
            return;
        }

        /* s'il y a de la nourriture qui a ete reperee */
        if(!foodNodes.isEmpty()) {
            if(foodNodes.get(0).hasFood())
                addDestination(foodNodes.get(0).currentCell);
            else {
                foodNodes.remove(0);
                pickRandomDestination();
            }
        }
        else
            pickRandomDestination();
    }

    public void pickRandomDestination() {
        /* une fois arrivee a destination, la fourmi prends une case aléatoire adjacente
         * et l'ajoute a ses destinations */
        Cell cell = pickNeighBoringCell();
        setCurrentCell(cell);
        addDestination(cell);
    }

    protected Cell pickNeighBoringCell() {
        Cell nextCell = null;
        Random random = new Random();
        int rInt;

        while(nextCell == null) {
            rInt = random.nextInt(8);

            switch (rInt) {
                case 0:
                    nextCell = getCurrentCell().getTopNeighbor();
                    break;
                case 1:
                    nextCell = getCurrentCell().getTopRightNeighbor();
                    break;
                case 2:
                    nextCell = getCurrentCell().getRightNeighbor();
                    break;
                case 3:
                    nextCell = getCurrentCell().getBottomRightNeighbor();
                    break;
                case 4:
                    nextCell = getCurrentCell().getBottomNeighbor();
                    break;
                case 5:
                    nextCell = getCurrentCell().getBottomLeftNeighbor();
                    break;
                case 6:
                    nextCell = getCurrentCell().getLeftNeighbor();
                    break;
                case 7:
                    nextCell = getCurrentCell().getTopLeftNeighbor();
                    break;
                default:
                    /* erreur */
                    break;
            }
        }
        return nextCell;
    }

    public void takeFood(FoodNode node) {
        System.out.println("TAKE FOOD");
        carryingFood = true;

        /* on descend la quantité de nourriture dans la case */
        node.setQuantity(node.getQuantity() - 1);

        foodNodes.remove(node);

        /* on peut prendre encore de la nourriture sur la case */
        if(node.getQuantity() >= 1) {
            foodNodes.add(node);
        }

        /* on change d'image */
        setIcon("/resources/images/ant-bean.png");
    }

    public void dropFood() {
        carryingFood = false;
        queen.setFoodStock(queen.getFoodStock() + 1);

        setIcon("/resources/images/ant.png");
    }

    @Override
    public void addDestination(Cell destination) {
        super.addDestination(destination);
        if(!carryingFood)
            pathToQueen.add(0,destination);
    }

    @Override
    /* lorsquon arrive a notre case on ajoute une case suivante */
    public void onArrival() {

        /* si la cell sur laquelle on est arrivee est la reine et on a de la nourriture on lui donne */
        if(distance(queen) <= this.speed && carryingFood) {
            dropFood();
        }

        /* si lorsquon est arrive nous sommes potentiellement sur de la nourriture : on peut la prendre */
        for(FoodNode n : foodNodes) {
            if (distance(n) <= this.speed && !carryingFood && n.hasFood()) {
                takeFood(n);
                break;
            }
        }

        /* cas ou de la nourriture est a proximite OU la reine */
        for(Node n : getSensedNodes()) {
            /* on peut deposer de la nourriture à la reine */
            if(n.equals(queen) && carryingFood) {
                addDestination(queen.currentCell);

                break;
            }

            /* il y a autour de la nourriture qui peut etre prise */
            if(n instanceof FoodNode) {
                if(((FoodNode) n).getQuantity() >= 1)
                    foodNodes.add((FoodNode)n);
            }
        }
        antAlgorithm();
    }

}
