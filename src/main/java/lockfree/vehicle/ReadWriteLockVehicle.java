package lockfree.vehicle;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockVehicle implements Vehicle {
    private final Vehicle vehicle;
    private final ReadWriteLock rwl;

    public ReadWriteLockVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        rwl = new ReentrantReadWriteLock();
    }

    @Override
    public void move(int xDelta, int yDelta) {
        try {
            rwl.writeLock().lock();
            vehicle.move(xDelta, yDelta);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public void getPosition(int[] coords) {
        try {
            rwl.readLock().lock();
            vehicle.getPosition(coords);
        } finally {
            rwl.readLock().unlock();
        }
    }
}
