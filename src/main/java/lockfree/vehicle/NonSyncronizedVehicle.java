package lockfree.vehicle;

public class NonSyncronizedVehicle implements Vehicle {
    private int[] position = {0, 0};

    @Override
    public void move(int xDelta, int yDelta) {
        position[0] += xDelta;
        position[1] += yDelta;
    }

    @Override
    public int[] getPosition() {
        return position;
    }

}
