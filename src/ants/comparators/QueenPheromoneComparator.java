package ants.comparators;

import ants.environment.Cell;
import java.util.Comparator;

public class QueenPheromoneComparator implements Comparator<Cell> {

    @Override
    public int compare(Cell c1, Cell c2) {
        return Double.compare(c1.getQueenPheromoneIntensity(),c2.getQueenPheromoneIntensity());
    }
}
