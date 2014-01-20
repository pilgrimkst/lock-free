package lockfree.vehicle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ThreadsRunner {
    private Logger logger = Logger.getLogger(getClass().getName());
    private final long warmupTimeInMillis;
    private final long executionTimeInMillis;
    private final int numOfThreads;
    private final List<Thread> runningThreads;
    private final Runnable task;

    public ThreadsRunner(Runnable task, int numOfThreads, long executionTimeInSeconds, long warmupTimeInMillis) {
        this.executionTimeInMillis = executionTimeInSeconds * 1000;
        this.numOfThreads = numOfThreads;
        this.warmupTimeInMillis = warmupTimeInMillis;
        runningThreads = new ArrayList<Thread>(numOfThreads);
        this.task = task;
    }

    public double run() throws InterruptedException {
        final long startTimeInMillis = System.nanoTime() / 1000000;
        final AtomicLong requests = new AtomicLong(0l);
        for (int i = 0; i < numOfThreads; i++) {
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!timeExpired(startTimeInMillis, executionTimeInMillis)) {
                        task.run();
                        if (timeExpired(startTimeInMillis, warmupTimeInMillis)) {
                            requests.incrementAndGet();
                        }
                    }
                }
            });
            runningThreads.add(t);
            t.start();
        }

        for (Thread t : runningThreads) {
            t.join();
        }
        long testTime = (executionTimeInMillis - warmupTimeInMillis) * 1000;
        return requests.get() / testTime;
    }

    private long timePassedInMillis(long startTimeInMillis) {
        return (System.nanoTime() / 1000000) - startTimeInMillis;
    }

    private boolean timeExpired(long startTimeInMillis, long durationInMillis) {
        return timePassedInMillis(startTimeInMillis) - durationInMillis > 0;
    }

}
