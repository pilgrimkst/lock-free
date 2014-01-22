package lockfree.vehicle;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeVehicle implements Vehicle {
    private final AtomicReference<int[]> coordinates = new AtomicReference<int[]>(new int[]{0, 0});

    @Override
    public void move(int xDelta, int yDelta) {
        int[] oldCoords;
        int[] newCoords;
        do {
            oldCoords = coordinates.get();
            newCoords = new int[]{oldCoords[0] + xDelta, oldCoords[1] + yDelta};
        } while (!coordinates.compareAndSet(oldCoords, newCoords));

    }

    @Override
    public void getPosition(int[] coords) {
        int[] newCoords;
        do {
            newCoords = coordinates.get();
            coords[0] = newCoords[0];
            coords[1] = newCoords[1];
        } while (newCoords != coordinates.get());
    }

}
