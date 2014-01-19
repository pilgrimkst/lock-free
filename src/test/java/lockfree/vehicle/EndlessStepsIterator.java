package lockfree.vehicle;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class EndlessStepsIterator implements Iterable<int[]> {
    private List<int[]> stepsMade = new CopyOnWriteArrayList<int[]>();

    @Override
    public Iterator<int[]> iterator() {
        return new Iterator<int[]>() {
            Random r = new Random();

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public int[] next() {
                int[] step = {r.nextInt(10), r.nextInt(10)};
                stepsMade.add(step);
                return step;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public List<int[]> getStepsMade() {
        return stepsMade;
    }
}
