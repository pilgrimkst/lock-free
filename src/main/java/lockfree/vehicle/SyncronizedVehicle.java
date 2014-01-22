package lockfree.vehicle;

public class SyncronizedVehicle implements Vehicle {
    private final Vehicle vehicle;

    public SyncronizedVehicle() {
        this.vehicle = new NonSyncronizedVehicle();
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
