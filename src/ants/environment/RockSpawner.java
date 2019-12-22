package ants.environment;

import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;

import java.util.Random;

public class RockSpawner{

    private final Random random;
    private Topology tp;
    private Environment environment;

    public RockSpawner(Topology topology, Environment environment) {
        tp = topology;
        this.environment = environment;
        random = new Random();
    }

    public void spawnRandomRocks() {
        RockNode r = new RockNode();
        Cell location = environment.getRandomLocation();
        r.setLocation(location);
        r.setCurrentCell(location);
        tp.addNode(r);
    }
}
