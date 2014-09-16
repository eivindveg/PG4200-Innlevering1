public class NanoStopwatch {


    private final long start;

    public NanoStopwatch() {
        start = System.nanoTime();
    }

    public double elapsedTime() {
        long now = System.nanoTime();
        return (now - start) / 1000000000.0;
    }

}
