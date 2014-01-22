package lockfree.vehicle;

import org.junit.Test;

import java.util.*;
import java.util.logging.Logger;

public class PerformanceTests {
    Logger logger = Logger.getLogger(getClass().getName());
    int numberOfThreads = Runtime.getRuntime().availableProcessors();
    int executionTimeInSeconds = 20;
    private int warmupTimeInSeconds = 10;

    @Test
    public void compareVehicleImplementations() throws InterruptedException {
        List<Vehicle> implementations = getVehicles();
        testImplementations(implementations, numberOfThreads, executionTimeInSeconds, warmupTimeInSeconds);
    }

    private List<Vehicle> getVehicles() {
        Vehicle nonSync = new NonSyncronizedVehicle();
        return Arrays.asList(nonSync, new ReadWriteLockVehicle(),new SpinLockVehicle(), new SyncronizedVehicle());
    }

    @Test
    public void testSynchronizationCorrectness() {

    }

    private void testImplementations(List<Vehicle> implementations, int numberOfThreads, long executionTimeInSeconds, long warmupTimeInSeconds) throws InterruptedException {
        StringBuilder sb = new StringBuilder("Implementation;1 read;;2 read;;3 read;\n");
        for (final Vehicle implementation : implementations) {
            sb.append(implementation.getClass().getCanonicalName()).append(";");
            for (int readThreads = 1; readThreads < numberOfThreads; readThreads++) {
//            int readThreads = 2;
            int writeThreads = numberOfThreads - readThreads;
            ThreadsRunner tr = new ThreadsRunner(getWriterRunnable(implementation), getReaderRunnable(implementation), writeThreads, readThreads, executionTimeInSeconds, warmupTimeInSeconds);
            long[] results = tr.run();

            sb.append(String.format("%d;%d;", results[0], results[1]));
            }
            sb.append("\n");
        }
        logger.info(sb.toString());
    }

    private Runnable getWriterRunnable(final Vehicle implementation) throws InterruptedException {
        final Random r = new Random();
        return new Runnable() {
            @Override
            public void run() {
                implementation.move(r.nextInt(10), r.nextInt(10));
            }
        };
    }

    private Runnable getReaderRunnable(final Vehicle implementation) throws InterruptedException {
        final int[] in = new int[2];
        return new Runnable() {
            @Override
            public void run() {
                implementation.getPosition(in);
            }
        };
    }
}
