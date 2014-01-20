package lockfree.vehicle;

import lockfree.SpinLock;

public class LockFreeVehicle implements Vehicle {
    private final Vehicle vehicle;

    public LockFreeVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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
