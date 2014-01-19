package lockfree;

import java.util.concurrent.atomic.AtomicBoolean;

public class SpinLock {
    private static AtomicBoolean lock = new AtomicBoolean(false);

    public static void acquire() {
        while (!lock.compareAndSet(false, true)) {
        }
    }

    public static void release() {
        boolean isLocked = lock.get();
        if (!lock.compareAndSet(isLocked, false)) {
            throw new RuntimeException("Concurrent modification of lock");
        }
    }
}
