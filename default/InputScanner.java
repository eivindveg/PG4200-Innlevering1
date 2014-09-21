public interface InputScanner<T> {

    /**
     * Scans the source T for a target String. The implementation should provide its own spec for how to scan what.
     *
     * @param source The source possible containing target
     * @param target The target String possible found in the source
     * @return Whether or not the source contains target
     */
    boolean scan(final T source, final String target);
}
