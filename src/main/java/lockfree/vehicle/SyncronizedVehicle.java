package lockfree.vehicle;

public class SyncronizedVehicle implements Vehicle {
    private final Vehicle vehicle;

    public SyncronizedVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public void move(int xDelta, int yDelta) {
        synchronized (vehicle) {
            vehicle.move(xDelta, yDelta);
        }
    }

    @Override
    public void getPosition(int[] coords) {
        synchronized (vehicle) {
            vehicle.getPosition(coords);
        }
    }
}
