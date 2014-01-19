package lockfree.vehicle;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class PerformanceTests {
    Logger logger = Logger.getLogger(getClass().getName());
    int numberOfThreads = Runtime.getRuntime().availableProcessors();
    int executionTimeInSeconds = 10;

    @Test
    public void compareVehicleImplementations() throws InterruptedException {
        List<Vehicle> implementations = getVehicles();
        List<double[]> results = testImplementations(implementations, numberOfThreads, executionTimeInSeconds);
    }

    private List<Vehicle> getVehicles() {
        Vehicle nonSync = new NonSyncronizedVehicle();
        return Arrays.asList(nonSync,
                new LockFreeVehicle(nonSync), new ReadWriteLockVehicle(nonSync), new SyncronizedVehicle(nonSync));
    }

    @Test
    public void testSynchronizationCorrectness() {

    }

    private List<double[]> testImplementations(List<Vehicle> implementations, int numberOfThreads, long executionTimeInSeconds) throws InterruptedException {
        StringBuilder sb = new StringBuilder("Implementation;Readers;NumOfThreads;Write;Read\n");
        List<double[]> results = new ArrayList<double[]>(implementations.size());
        for (double percentOfReadRequests = 0; percentOfReadRequests <= 1; percentOfReadRequests += 0.25) {
            for (Vehicle implementation : implementations) {
                ThreadsRunner runner = new ThreadsRunner(implementation, percentOfReadRequests, executionTimeInSeconds, numberOfThreads);
                double[] runResult = runner.run();
                String resultString =
                        String.format("%s;%f;%d;%f;%f\n",
                                implementation.getClass().getCanonicalName(),
                                percentOfReadRequests,
                                numberOfThreads,
                                runResult[0],
                                runResult[1]);
                sb.append(resultString);
            }
        }
        logger.info(sb.toString());
        return results;
    }
}
