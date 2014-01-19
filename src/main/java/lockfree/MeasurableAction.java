package lockfree;

import java.util.concurrent.atomic.AtomicLong;

public abstract class MeasurableAction {
    private AtomicLong totalExecutionTime = new AtomicLong(0l);
    protected AtomicLong totalRequests = new AtomicLong(0l);

    public void doAction() {
        long start = System.nanoTime();
        internalAction();
        logTime(System.nanoTime() - start);
    }

    protected abstract void internalAction();

    public double getRequestsPerSecond() {
        double timeInSeconds = totalExecutionTime.get() / 1000000;
        return totalRequests.get() / timeInSeconds;
    }

    public double getTotalRequests() {
        return totalRequests.get();
    }

    public void reset() {
        totalExecutionTime.set(0);
        totalRequests.set(0);
    }

    private void logTime(long executionTime) {
        totalExecutionTime.addAndGet(executionTime);
        totalRequests.incrementAndGet();
    }
}
