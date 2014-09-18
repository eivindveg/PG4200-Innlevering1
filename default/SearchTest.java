import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class SearchTest {

    public static void main(String[] args) {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        Searcher<Integer> binary = new BinarySearch<>();
        Searcher<Integer> sequenceIndex = new SequentialIndexSearch<>();
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

        for (int i = 50; i < 51; i++) {
            for (List<Integer> list : lists) {

                NanoStopwatch timer = new NanoStopwatch();
                prepareForTest(list, i * 10000);
                StdOut.println("Setup: " + timer.elapsedTime());

                for (Searcher<Integer> searcher : searchers) {
                    String s = String.format("%d\t%f\t%s\t%s\n", i, test(searcher, list, i * 10000), list.getClass().getSimpleName(), searcher.getClass().getSimpleName());
                    s = s.replace(".", ",");
                    StdOut.print(s);
                    //StdOut.printf();
                }

            }
        }

    }


    private static double test(Searcher<Integer> searcher, List<Integer> list, int size) {
        prepareForTest(list, size);

        NanoStopwatch timer = new NanoStopwatch();

        searcher.search(list, 0);

        return timer.elapsedTime();
    }

    private static void prepareForTest(List<Integer> list, int size) {
        list.clear();
        for (int i = 0; i < size; i++) {
            list.add(StdRandom.uniform(-size, size));
        }
        NanoStopwatch timer = new NanoStopwatch();
        Collections.sort(list);
        StdOut.println("Sorting: " + timer.elapsedTime());
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
        for (T obj : list) {
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
