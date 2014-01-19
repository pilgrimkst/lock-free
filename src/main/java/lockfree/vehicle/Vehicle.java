package lockfree.vehicle;

public interface Vehicle {
    void move(int xDelta, int yDelta);
    int[] getPosition();
}
