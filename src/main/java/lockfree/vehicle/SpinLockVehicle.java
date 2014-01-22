package lockfree.vehicle;

import lockfree.SpinLock;

public class SpinLockVehicle implements Vehicle {
    private final Vehicle vehicle;

    public SpinLockVehicle() {
        this.vehicle = new NonSyncronizedVehicle();
    }

    @Override
    public void move(int xDelta, int yDelta) {
        try {
            SpinLock.acquire();
            vehicle.move(xDelta, yDelta);
        } finally {
            SpinLock.release();
        }
    }

    @Override
    public void getPosition(int[] coords) {
        try {
            SpinLock.acquire();
            vehicle.getPosition(coords);
        } finally {
            SpinLock.release();
        }
    }

}
