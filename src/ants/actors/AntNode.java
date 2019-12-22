package ants.actors;

import ants.environment.Cell;
import ants.environment.FoodNode;
import ants.comparators.FoodPheromoneComparator;
import ants.comparators.QueenPheromoneComparator;

import io.jbotsim.core.Node;
import io.jbotsim.core.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class AntNode extends WaypointNode {

    /* temps de survie mini et maxi de la fourmi */
    private static final int MIN_TTL = 500;
    private static final int MAX_TTL = 1000;
    private int TTL;

    /* booleen d'états*/
    private boolean carryingFood;
    private boolean droppedFood;
    private boolean isOnQueen;
    private boolean foundFood;

    /* reine mere à qui deposer de la nourriture */
    private QueenNode queen;

    /* chemin inverse pour aller jusqua la reine */
    private ArrayList<Cell> pathToQueen = new ArrayList<Cell>();

    private ArrayList<Node> sensedNodes = new ArrayList<Node>();
    private ArrayList<Cell> neighbors = new ArrayList<Cell>();
    private ArrayList<Cell> neighborsSortedFood = new ArrayList<Cell>();
    private ArrayList<Cell> neighborsSortedQueen = new ArrayList<Cell>();

    /* pheromones */
    private double maxIntensity;

    /* dernières positions de la fourmi */
    private Cell previousCell;
    private Cell previousPreviousCell;

    public AntNode(QueenNode mother) {
        super();

        /* initialiser position initiale */
        this.queen = mother;

        /* temps de vie de la fourmi */
        TTL = new Random().nextInt(MAX_TTL) + MIN_TTL;

        carryingFood = false;

        setIcon("/resources/images/ant.png");
        setSensingRange(50);
    }

    @Override
    public void onStart() {
        super.onStart();
        setLocation(currentCell);
        pathToQueen.add(currentCell);
    }

    @Override
    public void onClock() {
        TTL--;
        /* si la fourmi n'a plus de temps de dispo : meurt */
        if (TTL <= 0)
            die();

        super.onClock();
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

        while (nextCell == null) {
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
        /* la fourmi prend de la nourriture */
        carryingFood = true;
        foundFood = true;

        node.setQuantity(node.getQuantity() - 1);

        /* on change d'image */
        setIcon("/resources/images/ant-bean.png");
    }

    public void dropFood() {
        /* on depose la nourriture */
        carryingFood = false;
        droppedFood = true;

        queen.setFoodStock(queen.getFoodStock() + 1);

        setIcon("/resources/images/ant.png");
    }

    @Override
    public void onArrival() {
        antAlgorithm();
    }

    protected void antAlgorithm() {
        /* on sent les noeuds autour */
        sensedNodes.clear();
        sensedNodes = new ArrayList<Node>(getSensedNodes());

        maxIntensity = 0.0;

        /* liste des cell autour de la notre afin de pouvoir aller dans la meilleure direction */
        neighbors = currentCell.getAllNeighbors();
        neighborsSortedFood = sortByFoodIntensity(neighbors);

        if(!carryingFood) {
            for (Node n : sensedNodes) {
                /* si on est sur le noeud */
                if (distance(n) <= speed) {
                    if (n instanceof FoodNode) {
                        takeFood((FoodNode) n);
                        goBackToQueen();
                        return;
                    }
                } /* la fourmi depose une pheromene sur la reine */ else
                    currentCell.setQueenPheromoneIntensity(currentCell.getQueenPheromoneIntensity() + 0.1);

                /* si la fourmi detecte de la nourriture a cote : depose une pheromone et prends la nourriture */
                if (n instanceof FoodNode) {
                    currentCell.setFoodPheromoneIntensity(currentCell.getFoodPheromoneIntensity() + 0.1);
                    addDestination(((FoodNode) n).getCurrentCell());
                    return;
                }
            }

            /* si il y a une pheromone nourriture : prends la plus intense (on se dirige vers de la nourriture) */
            for (int i = 0; i < neighborsSortedFood.size(); i++) {
                if (neighborsSortedFood.get(i).getFoodPheromoneIntensity() > 0.0) {
                    /* si elle vient de donner la nourriture à la reine elle repart en arriere */
                    if (droppedFood) {
                        droppedFood = false;
                        addDestination(neighborsSortedQueen.get(i));
                        return;
                    }

                    if (!neighborsSortedFood.get(i).equals(previousCell) && !neighborsSortedFood.get(i).equals(previousPreviousCell)) {
                        addDestination(neighborsSortedFood.get(i));
                        return;
                    }
                }
            }
            /* si la fourmi n'a rien detecté aux alentours elle va n'importe ou */
            pickRandomDestination();
            return;
        }
        /* la fourmi a de la nourriture */
        else {

            /* elle est sur la reine : elle depose la nourriture */
            if (isOnQueen) {
                dropFood();
                isOnQueen = false;

                /* redemarre son algo */
                antAlgorithm();
                return;
            }
            /* sinon elle continue de chercher la reine */
            else {
                goBackToQueen();
                return;
            }
        }
    }


    @Override
    public void addDestination(Cell destination){
        /* on affecte les cell precedentes */
        previousPreviousCell = previousCell;
        previousCell = currentCell;

        super.addDestination(destination);
    }

    /* methode permettant de suivre le chmin vers la reine */
    private void goBackToQueen() {

        maxIntensity = 0;
        neighbors = currentCell.getAllNeighbors();
        neighborsSortedQueen = sortByQueenIntensity(neighbors);

        /* si on a tout juste trouve de la nourriture on revient en arriere */
        if(foundFood) {
            foundFood = false;
            addDestination(previousCell);
        } /* sinon on indique que la case sur laquelle on est mene a de la nourriture */
        else
            currentCell.setFoodPheromoneIntensity(currentCell.getFoodPheromoneIntensity() + 0.1);

        /* si on trouve la reine dans les cellules voisines */
        for(Node n : sensedNodes) {
            if(n instanceof QueenNode && n.equals(queen)) {
                isOnQueen = true;
                currentCell.setQueenPheromoneIntensity(currentCell.getQueenPheromoneIntensity() + 0.1);
                addDestination(queen.getCurrentCell());
                return;
            }
        }

        /* trouver la reine par les pheromones de reine voisines */
        for(int i = 0; i < neighborsSortedQueen.size(); i++) {
            if(neighborsSortedQueen.get(i).getQueenPheromoneIntensity() > 0) {
                /* si on vient de trouver de la nourriture */
                if(foundFood) {
                    foundFood = false;
                    addDestination(neighborsSortedQueen.get(i));
                    return;
                } /* si on prends un autre chemin que les dernières cases */
                else if(!neighborsSortedQueen.get(i).equals(previousCell) && !neighborsSortedQueen.get(i).equals(previousPreviousCell)) {
                    addDestination(neighborsSortedQueen.get(i));
                    return;
                }
            }
        }

        /* prends une case au hasard */
        pickRandomDestination();
        return;
    }

    /* méthode permettant de trier les cases voisines selon soit les pheromones de nourriture ou celle de la reine */
    private ArrayList<Cell> sortByFoodIntensity(ArrayList<Cell> neighbors) {
        ArrayList<Cell> neighborsSortedFood = new ArrayList<Cell>(neighbors);
        Collections.sort(neighborsSortedFood, new FoodPheromoneComparator());
        return neighborsSortedFood;
    }

    private ArrayList<Cell> sortByQueenIntensity(ArrayList<Cell> neighbors) {
        ArrayList<Cell> neighborsSortedQueen = new ArrayList<Cell>(neighbors);
        Collections.sort(neighborsSortedQueen, new QueenPheromoneComparator());
        return neighborsSortedQueen;
    }

}
