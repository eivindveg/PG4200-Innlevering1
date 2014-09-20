public class NanoStopwatch {


    private final long start;

    public NanoStopwatch() {
        start = System.nanoTime();
    }

    /**
     * Returns, in seconds, the amount of time elapsed since this instance was created.
     * @return Seconds since instantiation
     */
    public double elapsedTime() {
        long now = System.nanoTime();
        return (now - start) / 1000000000.0;
    }

}
