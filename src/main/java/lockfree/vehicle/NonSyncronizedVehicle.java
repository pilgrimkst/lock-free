package lockfree.vehicle;

public class NonSyncronizedVehicle implements Vehicle {
    private final int[] x = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final int[] y = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    public void move(int xDelta, int yDelta) {
        x[0] += xDelta;
        y[1] += yDelta;
    }

    @Override
    public void getPosition(int[] coords) {
        coords[0] = x[0];
        coords[1] = y[1];
    }

}
