package lockfree.vehicle;

import vanilla.java.affinity.AffinityLock;
import vanilla.java.affinity.AffinityStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ThreadsRunner {
    private Logger logger = Logger.getLogger(getClass().getName());
    private final long warmupTimeInMillis;
    private final long executionTimeInMillis;
    private final int numOfWriters;
    private final int numOfReaders;
    private final List<Thread> runningThreads;
    private final Runnable writeTask;
    private final Runnable readTask;

    public ThreadsRunner(Runnable writeTask, Runnable readTask, int numOfWriters, int numOfReaders, long executionTimeInSeconds, long warmupTimeInSeconds) {
        this.writeTask = writeTask;
        this.readTask = readTask;
        this.numOfWriters = numOfWriters;
        this.numOfReaders = numOfReaders;
        this.executionTimeInMillis = executionTimeInSeconds * 1000;
        this.warmupTimeInMillis = warmupTimeInSeconds * 1000;
        runningThreads = new ArrayList<Thread>(numOfReaders + numOfWriters);

    }

    public long[] run() throws InterruptedException {
        final AtomicLong writeRequests = new AtomicLong(0l);
        final AtomicLong readRequests = new AtomicLong(0l);
        AffinityLock al = AffinityLock.acquireLock();
        AffinityLock readerLock = al.acquireLock(AffinityStrategies.DIFFERENT_SOCKET, AffinityStrategies.DIFFERENT_CORE);
        submit(readTask, numOfReaders, readRequests);
        AffinityLock writerLock = readerLock.acquireLock(AffinityStrategies.SAME_CORE, AffinityStrategies.SAME_SOCKET, AffinityStrategies.ANY);
        submit(writeTask, numOfWriters, writeRequests);

        for (Thread t : runningThreads) {
            t.join();
        }
        long testTime = (executionTimeInMillis - warmupTimeInMillis) * 1000;
        return new long[]{writeRequests.get() / testTime, readRequests.get() / testTime};
    }

    private void submit(final Runnable task, int numOfThreads, final AtomicLong requests) {
        final long startTimeInMillis = getCurrentTime();
        final long warmupEnd = startTimeInMillis + warmupTimeInMillis;
        final long testEnd = startTimeInMillis + executionTimeInMillis;
        for (int i = 0; i < numOfThreads; i++) {

            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (getCurrentTime() < testEnd) {
                        task.run();
                        if (getCurrentTime() > warmupEnd) {
                            requests.incrementAndGet();
                        }
                    }
                }
            });
            runningThreads.add(t);
            t.start();
        }
    }

    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }
}
