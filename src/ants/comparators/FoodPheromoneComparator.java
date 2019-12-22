package ants.comparators;

import ants.environment.Cell;
import java.util.Comparator;

public class FoodPheromoneComparator implements Comparator<Cell> {

    @Override
    public int compare(Cell c1, Cell c2) {
        return Double.compare(c1.getFoodPheromoneIntensity(),c2.getFoodPheromoneIntensity());
    }
}
