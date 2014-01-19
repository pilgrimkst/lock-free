package lockfree;

import lockfree.vehicle.Vehicle;

public class GPS extends MeasurableAction {

    private final Vehicle vehicle;

    public GPS(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    protected void internalAction() {
        vehicle.getPosition();
    }
}
