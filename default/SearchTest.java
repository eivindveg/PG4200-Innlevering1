import edu.princeton.cs.introcs.Out;
import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;

import java.time.LocalDateTime;
import java.util.*;


public class SearchTest {

    private static final String outPrefix = "results";

    public static void main(String[] args) {

        // Set up a file dump to produce graphs from
        LocalDateTime timeStamp = LocalDateTime.now();
        String timeStampString = timeStamp.toString().replaceAll("(:|\\.)", "");
        Out out = new Out(outPrefix + timeStampString + ".csv");

        // Prepare empty lists and searchers
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        Searcher<Integer> sequenceIndex = new SequentialIndexSearch<>();
        Searcher<Integer> binary = new BinarySearch<>();
        Searcher<Integer> sequenceForEach = new SequentialForEachSearch<>();
        Searcher<Integer> builtin = new BuiltinSearch<>();

        List<List<Integer>> lists = new ArrayList<>();

        lists.add(arrayList);
        lists.add(linkedList);

        List<Searcher<Integer>> searchers = new ArrayList<>();

        searchers.add(binary);
        searchers.add(sequenceIndex);
        searchers.add(sequenceForEach);
        searchers.add(builtin);

        // Print headers for tests:
        StdOut.printf("%-12s %-25s", "Size", "SortingTest");
        out.print("size;SortingTest;");
        for(Searcher<Integer> searcher : searchers) {
            StdOut.printf("%-25s", searcher.getClass().getSimpleName());
            out.print(searcher.getClass().getSimpleName() + ";");
        }
        StdOut.printf("%-25s", "List Implementation");
        StdOut.println();
        out.print("List Implementation\n");

        /*
            We want to run tests for exponentially incrementing values, but with not too large distances between number
            When running the tests, we first test the time it takes to set up the list, then we grab a random number
            from the list and assign the searcher to find that number. Because this can create dramatically biased and
            random results, we run this tests five times, with a different number each time, then take the average of the
            test duration. We also fail the test if it takes longer than 20 seconds to find the assigned number in any
            one test cycle.
         */
        for (int i = 1; i <= 10; i++) {
            // Size is 10000 multiplied with the i squared, ie 10,000, 40,000, 90,000
            int size = 10000 * (int)(Math.pow(i, 2));

            for (List<Integer> list : lists) {
                StdOut.printf("%-13s", size);
				out.print(size + ";");

                // Run each test 5 times.
                final int numberOfTests = 5;

                double totalTime = 0;

                // Run a measured test setup and calculate the average
                for (int j = 1; j <= numberOfTests; j++) {
                    NanoStopwatch timer = new NanoStopwatch();
                    prepareForTest(list, size);
                    totalTime += timer.elapsedTime();
                }
                double averageTime = totalTime / numberOfTests;
                StdOut.printf("%-25.8s", averageTime);
                out.print(averageTime + ";");


                // Prepare for test, the measure the actual lookup
                for (Searcher<Integer> searcher : searchers) {
                    totalTime = 0;

                    for (int j = 1; j <= numberOfTests; j++) {
                        int valueToFind = prepareForTest(list, size);
                        NanoStopwatch timer = new NanoStopwatch();
                        // Check if the test failed to find the value, and abort if failed.
                        // (Our lifespans are measured in years, not centuries)
                        if(searcher.search(list, valueToFind) == -1) {
                            totalTime = -1;
                            break;
                        } else {
                            totalTime += timer.elapsedTime();
                        }
                    }
                    if(totalTime != -1) {
                        // If the test didn't fail, calculate the average time and print it
                        averageTime = totalTime / numberOfTests;
                        String timeString = String.format(Locale.US, "%f", averageTime);
                        StdOut.printf("%-25s", timeString);
                        out.print(timeString + ";");
                    } else {
                        StdOut.printf("%-25s", "FAILED");
                        out.print("FAILED;");
                    }

                }
                // In the last column, output the list implementation's name
                StdOut.printf("%-25s", list.getClass().getSimpleName());
                StdOut.println();
                out.print(list.getClass().getSimpleName() + ";\n");
            }
        }

    }

    /**
     * Clears a given list, then fills it with random numbers, before grabbing a random number from the list, sorting it
     * and returning the random number.
     * @param list The list to prepare
     * @param size How large the list should be
     * @return The given value to search for after preparation
     */
    private static int prepareForTest(List<Integer> list, int size) {
        list.clear();
        for (int i = 0; i < size; i++) {
            list.add(StdRandom.uniform(-size, size));
        }
        int valueToFind = list.get(StdRandom.uniform(0, size));
        Collections.sort(list);
        return valueToFind;
    }

}


interface Searcher<T> {
    /*
     * Returns index of target if the target is present
     * in the list.'
     *
     * When the target is not in the list, this method
     * return -1.
     */
    int search(List<T> list, T target);

}


class SequentialIndexSearch<T> implements Searcher<T> {

    public int search(List<T> list, T target) {
        NanoStopwatch timer = new NanoStopwatch();
        for (int i = 0; i < list.size(); i++) {
            if(timer.elapsedTime() > 20.0) {
                return -1;
            }
            if (list.get(i).equals(target))
                return i;
        }
        return -1;
    }

}

class SequentialForEachSearch<T> implements Searcher<T> {
    public int search(List<T> list, T target) {
        int i = 0;
        NanoStopwatch timer = new NanoStopwatch();
        for (T obj : list) {
            if(timer.elapsedTime() > 20.0) {
                return -1;
            }
            if (obj.equals(target)) {
                return i;
            }
            i += 1;
        }
        return -1;
    }
}

class BuiltinSearch<T> implements Searcher<T> {
    public int search(List<T> list, T target) {
        return list.indexOf(target);
    }
}


class BinarySearch<T extends Comparable<T>> implements Searcher<T> {
    public int search(List<T> list, T target) {
        int low = 0;
        int high = list.size() - 1;
        int middle;
        
        NanoStopwatch timer = new NanoStopwatch();
        while (low <= high) {
            if(timer.elapsedTime() > 20.0) {
                return -1;
            }
            middle = (low + high) / 2;
            int cmp = target.compareTo(list.get(middle));
            if (cmp < 0) {
                high = middle - 1;
            } else if (cmp > 0) {
                low = middle + 1;
            } else { // cmp == 0
                return middle;
            }
        }

        return -1;
    }
}
