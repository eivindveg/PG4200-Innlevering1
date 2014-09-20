import edu.princeton.cs.introcs.Out;
import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;

import java.time.LocalDateTime;
import java.util.*;


public class SearchTest {
    private static final String outPrefix = "results";

    public static void main(String[] args) {
        LocalDateTime timeStamp = LocalDateTime.now();
        String timeStampString = timeStamp.toString().replaceAll("(:|\\.)", "");
        System.out.println(timeStampString);
        Out out = new Out(outPrefix + timeStampString + ".csv");

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
        StdOut.printf("%-12s %-25s", "Size", "SortingTest");
        out.print("size;SortingTest;");
        for(Searcher<Integer> searcher : searchers) {
            StdOut.printf("%-25s", searcher.getClass().getSimpleName());
            out.print(searcher.getClass().getSimpleName() + ";");
        }
        StdOut.printf("%-25s", "List Implementation");
        StdOut.println();
        out.print("List Implementation\n");
        for (int i = 1; i <= 10; i++) {
            int size = 10000 * (int)(Math.pow(i, 2));

            for (List<Integer> list : lists) {
                StdOut.printf("%-13s", size);
                out.print(size + ";");
                final int numberOfTests = 3;

                double totalTime = 0;
                for (int j = 1; j <= numberOfTests; j++) {
                    NanoStopwatch timer = new NanoStopwatch();
                    prepareForTest(list, size);
                    totalTime += timer.elapsedTime();
                }
                double averageTime = totalTime / numberOfTests;
                StdOut.printf("%-25.8s", averageTime);
                out.print(averageTime + ";");


                for (Searcher<Integer> searcher : searchers) {
                    totalTime = 0;

                    for (int j = 1; j <= numberOfTests; j++) {
                        int valueToFind = prepareForTest(list, size);
                        NanoStopwatch timer = new NanoStopwatch();
                        if(searcher.search(list, valueToFind) == -1) {
                            totalTime = -1;
                            break;
                        } else {
                            totalTime += timer.elapsedTime();
                        }
                    }
                    if(totalTime != -1) {
                        averageTime = totalTime / numberOfTests;
                        String timeString = String.format(Locale.US, "%f", averageTime);
                        StdOut.printf("%-25s", timeString);
                        out.print(timeString + ";");
                    } else {
                        StdOut.printf("%-25s", "FAILED");
                        out.print("FAILED;");
                    }

                }
                StdOut.printf("%-25s", list.getClass().getSimpleName());
                StdOut.println();
                out.print(list.getClass().getSimpleName() + ";\n");
            }
        }

    }


    private static double test(Searcher<Integer> searcher, List<Integer> list, int size) {
        prepareForTest(list, size);

        NanoStopwatch timer = new NanoStopwatch();

        searcher.search(list, 0);

        return timer.elapsedTime();
    }

    private static int prepareForTest(List<Integer> list, int size) {
        list.clear();
        for (int i = 0; i < size; i++) {
            list.add(StdRandom.uniform(-size, size));
        }
        int valueToFind = list.get(StdRandom.uniform(0, size));
        Collections.sort(list);
        return valueToFind;
    }


    private static void example(Searcher<Integer> searcher, List<Integer> list, int size) {
        StdOut.printf("Searcher: %s\tList: %s\n",
                searcher.getClass().getSimpleName(),
                list.getClass().getSimpleName());

        StdOut.println(list);

        StdOut.println(searcher.search(list, 0));
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
        for (int i = 0; i < list.size(); i++) {
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
            if(timer.elapsedTime() > 60.0) {
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

        while (low <= high) {
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
