import edu.princeton.cs.algorithms.Queue;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdOut;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FileSearch {

    public static final String TARGET = "bacon";
    public static final File SEARCH_DIRECTORY = new File("default/searchdir");

    public static void main(String[] args) {

        // Setup array of searchers
        FileSearcher[] fileSearchers = {
                new RecursiveFileSearcher(),
                new StackFileSearcher(),
                new QueueFileSearcher()
        };

        // Time each searcher using the same parameters and list the files found
        for (FileSearcher fileSearcher : fileSearchers) {
            NanoStopwatch timer = new NanoStopwatch();
            File[] filesFound = fileSearcher.search(SEARCH_DIRECTORY, TARGET);
            StdOut.println(fileSearcher.getClass().getSimpleName() + " spent " + timer.elapsedTime() +
                    " seconds to find \"" + TARGET.toLowerCase() + "\" in this order:");
            for (File file : filesFound) {
                StdOut.println(file.getPath());
            }
            StdOut.println();
        }
    }
}

abstract class FileSearcher implements InputScanner<File> {

    /**
     * Searches for the target string using the directory or file supplied, and returns the
     * @param file The file or directory to search through
     * @param target The word to search for
     * @return An array of Files containing the target string
     */
    public abstract File[] search(File file, String target);

    /**
     * Scans a given file for an exact match to the word target. We have chosen to use only equals and not contains
     * because simple words like "and" or "or" could easily be included in words like "operand" and "operator"
     *
     * @param file   The file to scan
     * @param target The word to search for
     * @return Whether or not this file contains the target word.
     */
    public boolean scan(final File file, final String target) {
        In in = new In(file);
        String[] strings = in.readAllStrings();
        for (String word : strings) {
            if (word.toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }


}

/**
 * Implementation that uses standard recursion to find a target string in a directory
 */
class RecursiveFileSearcher extends FileSearcher {

    @Override
    public File[] search(final File file, final String target) {
        List<File> discoveredFiles = new ArrayList<>();

        searchHelper(file, target, discoveredFiles);

        File[] returnValue = new File[discoveredFiles.size()];
        return discoveredFiles.toArray(returnValue);
    }

    /**
     * Recu
     * @param file
     * @param target
     * @param discoveredFiles
     */
    private void searchHelper(final File file, final String target, final List<File> discoveredFiles) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files != null ? files : new File[0]) {
                this.searchHelper(f, target, discoveredFiles);
            }
        } else if (super.scan(file, target)) {
            discoveredFiles.add(file);
        }
    }

}

class StackFileSearcher extends FileSearcher {

    @Override
    public File[] search(final File file, final String target) {
        ArrayList<File> discoveredFiles = new ArrayList<>();
        Stack<File> searchStack = new Stack<>();

        searchStack.push(file);
        while (!searchStack.empty()) {
            this.searchHelper(target, searchStack, discoveredFiles);
        }

        File[] returnValue = new File[discoveredFiles.size()];
        return discoveredFiles.toArray(returnValue);
    }

    private void searchHelper(final String target, final Stack<File> searchStack, List<File> discoveredFiles) {
        File file = searchStack.pop();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files != null ? files : new File[0]) {
                searchStack.push(f);
            }
        } else if (super.scan(file, target)) {
            discoveredFiles.add(file);
        }
    }
}

class QueueFileSearcher extends FileSearcher {

    @Override
    public File[] search(final File file, final String target) {
        ArrayList<File> discoveredFiles = new ArrayList<>();
        Queue<File> searchQueue = new Queue<>();

        searchQueue.enqueue(file);
        while (!searchQueue.isEmpty()) {
            this.searchHelper(target, searchQueue, discoveredFiles);
        }

        File[] returnValue = new File[discoveredFiles.size()];
        return discoveredFiles.toArray(returnValue);
    }

    private void searchHelper(final String target, final Queue<File> searchQueue, final ArrayList<File> discoveredFiles) {
        File file = searchQueue.dequeue();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files != null ? files : new File[0]) {
                searchQueue.enqueue(f);
            }
        } else if (super.scan(file, target)) {
            discoveredFiles.add(file);
        }
    }
}

