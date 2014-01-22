package lockfree.vehicle;


import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VehicleCorrectnessTest {
    public static final Logger logger = Logger.getLogger(VehicleCorrectnessTest.class.getName());
    Vehicle nonSynhronizedVehicle = new NonSyncronizedVehicle();
    final long executionTimeInMs = 20000;
    List<Vehicle> implementations = Arrays.asList(new LockFreeVehicle(), new SpinLockVehicle(), new ReadWriteLockVehicle(), new SyncronizedVehicle());

    @Test
    public void shouldCorrectlyWriteUpdatesToStateInMultiThreadedEnvironment() throws InterruptedException {
        int numOfThreads = Runtime.getRuntime().availableProcessors();
        assertFalse(checkImplementation(numOfThreads, nonSynhronizedVehicle));
        for (final Vehicle impl : implementations) {
            assertTrue(checkImplementation(numOfThreads, impl));
        }
    }

    private boolean checkImplementation(int numOfThreads, final Vehicle impl) throws InterruptedException {
        logger.info("Checking " + impl.getClass());
        assertInStartPosition(impl);
        final List<int[]> actions = new CopyOnWriteArrayList<int[]>();
        List<Thread> threads = new ArrayList<Thread>(numOfThreads);
        for (int n = 0; n < numOfThreads; n++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    EndlessStepsIterator stepsIterator = new EndlessStepsIterator();
                    long startTime = System.currentTimeMillis();
                    Iterator<int[]> iterator = stepsIterator.iterator();
                    while (System.currentTimeMillis() - startTime < executionTimeInMs) {
                        int[] newStep = iterator.next();
                        impl.move(newStep[0], newStep[1]);
                    }
                    actions.addAll(stepsIterator.getStepsMade());
                }
            });
            t.start();
            threads.add(t);
        }
        for (Thread t : threads) {
            t.join();
        }
        return assertPositionRight(impl, actions);
    }

    private boolean assertPositionRight(Vehicle impl, List<int[]> actions) {
        int[] position = new int[]{0, 0};
        for (int[] delta : actions) {
            position[0] += delta[0];
            position[1] += delta[1];
        }
        int[] realVechiclePosition = new int[]{0, 0};
        impl.getPosition(realVechiclePosition);
        logger.info(String.format("checking: %s: position is x=%d(asserted %d) y=%d(asserted %d)", impl.getClass(), realVechiclePosition[0], position[0], realVechiclePosition[1], position[1]));
        return position[0] == realVechiclePosition[0] && position[1] == realVechiclePosition[1];
    }

    private void assertInStartPosition(Vehicle impl) {
        int[] expectedPosition = new int[]{0, 0};
        int[] actualPosition = new int[]{0, 0};
        impl.getPosition(actualPosition);
        logger.info("start poistion is: x: " + actualPosition[0] + " y: " + actualPosition[1] + "; 0,0 asserted");
        Assert.assertEquals(expectedPosition[0], actualPosition[0]);
        Assert.assertEquals(expectedPosition[1], actualPosition[1]);
    }
}
