package lockfree;

import lockfree.vehicle.Vehicle;

import java.util.Iterator;

public class Driver extends MeasurableAction {

    private final Vehicle vehicle;
    Iterator<int[]> stepsIterator;

    public Driver(Vehicle vehicle, Iterable<int[]> steps) {
        this.vehicle = vehicle;
        stepsIterator = steps.iterator();
    }

    @Override
    public void internalAction() {
        if (stepsIterator.hasNext()) {
            int[] coordsDelta = stepsIterator.next();
            vehicle.move(coordsDelta[0], coordsDelta[1]);
        }
    }
}

