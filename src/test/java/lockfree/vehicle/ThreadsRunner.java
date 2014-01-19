package lockfree.vehicle;

import lockfree.Driver;
import lockfree.GPS;
import lockfree.MeasurableAction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ThreadsRunner {
    private Logger logger = Logger.getLogger(getClass().getName());
    private final ThreadLocal<MeasurableAction> readAction = new ThreadLocal<MeasurableAction>();
    private final ThreadLocal<MeasurableAction> writeAction = new ThreadLocal<MeasurableAction>();
    private final double percentOfReadRequests;
    private final long executionTimeInMillis;
    private final int numberOfThreads;
    private final List<Thread> runningThreads;
    private final Vehicle vehicle;

    private long warmupTime = 1000000;

    public ThreadsRunner(Vehicle implementation, double percentOfReadRequests, long executionTimeInSeconds, int numberOfThreads) {
        this.percentOfReadRequests = percentOfReadRequests;
        this.executionTimeInMillis = executionTimeInSeconds * 1000;
        this.numberOfThreads = numberOfThreads;
        runningThreads = new ArrayList<Thread>(numberOfThreads);
        vehicle = implementation;
    }

    public double[] run() throws InterruptedException {
        final long startTime = System.nanoTime();
        final List<int[]> totalSteps = new ArrayList<int[]>();
        for (int i = 0; i < numberOfThreads; i++) {
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    readAction.set(new GPS(vehicle));
                    EndlessStepsIterator steps = new EndlessStepsIterator();
                    writeAction.set(new Driver(vehicle, steps));
                    while (!warmupExpired(startTime)) {
                        makeStep();
                    }

                    resetCounters();
                    while (!timeExpired(startTime)) {
                        makeStep();
                    }
                    totalSteps.addAll(steps.getStepsMade());
                }
            });
            runningThreads.add(t);
            t.start();
        }

        for (Thread t : runningThreads) {
            t.join();
        }
        return new double[]{readAction.get().getRequestsPerSecond(), writeAction.get().getRequestsPerSecond()};
    }

    private void resetCounters() {
        readAction.get().reset();
        writeAction.get().reset();
    }

    private void makeStep() {
        if (isReadOperation()) {
            readAction.get().doAction();
        } else {
            writeAction.get().doAction();
        }
    }

    private boolean warmupExpired(long startTime) {
        return ((System.nanoTime() - startTime) / 1000000) > warmupTime;
    }

    private boolean timeExpired(long startTime) {
        return ((System.nanoTime() - startTime) / 1000000) > executionTimeInMillis;
    }


    private boolean isReadOperation() {
        return Math.random() <= percentOfReadRequests;
    }
}
