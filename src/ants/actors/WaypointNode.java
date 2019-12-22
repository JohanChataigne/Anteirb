package ants.actors;

import ants.environment.Cell;
import io.jbotsim.core.Node;
import io.jbotsim.core.Point;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This type of Node can move over a sequence of destinations,
 * specified through the addDestination() method.
 */
public class WaypointNode extends CellLocatedNode {

    Queue<Cell> destinations = new LinkedList<Cell>();

    double speed = 1;

    @Override
    public void onClock() {
        if(!destinations.isEmpty()){
            Point dest = destinations.peek();

            if(distance(dest) > speed) {
                setDirection(dest);
                move(speed);
            } else {
                /* on est arrive a destination : on la retire de la queue */
                setLocation(dest);
                destinations.poll();
                onArrival();
            }
        } else
            onArrival();
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void addDestination(Cell destination){
        destinations.add(destination);
    }

    public void onArrival(){ }
}
