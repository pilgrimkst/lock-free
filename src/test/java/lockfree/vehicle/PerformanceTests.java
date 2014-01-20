package lockfree.vehicle;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class PerformanceTests {
    Logger logger = Logger.getLogger(getClass().getName());
    int numberOfThreads = Runtime.getRuntime().availableProcessors();
    int executionTimeInSeconds = 20;
    private int warmupTimeInSeconds = 10;

    @Test
    public void compareVehicleImplementations() throws InterruptedException {
        List<Vehicle> implementations = getVehicles();
        List<double[]> results = testImplementations(implementations, numberOfThreads, executionTimeInSeconds, warmupTimeInSeconds);
    }

    private List<Vehicle> getVehicles() {
        Vehicle nonSync = new NonSyncronizedVehicle();
        return Arrays.asList(new LockFreeVehicle(nonSync), new ReadWriteLockVehicle(nonSync), new SyncronizedVehicle(nonSync));
    }

    @Test
    public void testSynchronizationCorrectness() {

    }

    private List<double[]> testImplementations(List<Vehicle> implementations, int numberOfThreads, long executionTimeInSeconds, long warmupTimeInSeconds) throws InterruptedException {
        StringBuilder sb = new StringBuilder("Implementation;1 read;;2 read;;3 read;\n");

        List<double[]> results = new ArrayList<double[]>(implementations.size());
        for (final Vehicle implementation : implementations) {
            sb.append(implementation.getClass().getCanonicalName()).append(";");
            for (int readThreads = 1; readThreads < numberOfThreads; readThreads++) {
                int writeThreads = numberOfThreads - readThreads;

                double readersResult = executeReadersTest(executionTimeInSeconds, warmupTimeInSeconds, implementation, readThreads);
                double writersResult = executeWritersTest(executionTimeInSeconds, warmupTimeInSeconds, implementation, writeThreads);

                sb.append(String.format("%f;%f;", readersResult, writersResult));
            }
            sb.append("\n");
        }
        logger.info(sb.toString());
        return results;
    }

    private double executeWritersTest(long executionTimeInSeconds, long warmupTimeInSeconds, final Vehicle implementation, int writeThreads) throws InterruptedException {
        final Iterator<int[]> steps = new EndlessStepsIterator().iterator();
        ThreadsRunner writeRunner = new ThreadsRunner(new Runnable() {
            @Override
            public void run() {
                int[] step = steps.next();
                implementation.move(step[0], step[1]);
            }
        }, writeThreads, executionTimeInSeconds, warmupTimeInSeconds);


        return writeRunner.run();
    }

    private double executeReadersTest(long executionTimeInSeconds, long warmupTimeInSeconds, final Vehicle implementation, int readThreads) throws InterruptedException {
        ThreadsRunner readRunner = new ThreadsRunner(new Runnable() {
            @Override
            public void run() {
                implementation.getPosition();
            }
        }, readThreads, executionTimeInSeconds, warmupTimeInSeconds);
        return readRunner.run();
    }
}
