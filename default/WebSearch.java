import edu.princeton.cs.algorithms.Queue;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdOut;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebSearch {

    public static final String INITIAL_LINK = "http://nith.no";
    public static final String TARGET = "Bedrifter";


    public static void main(String[] args) throws MalformedURLException {
        UrlSearcher searcher = new UrlSearcher();

        NanoStopwatch timer = new NanoStopwatch();
        URL[] linksWithTarget = searcher.search(new URL(INITIAL_LINK), TARGET.toLowerCase(), 500);
        StdOut.println(searcher.getClass().getSimpleName() + " spent " + timer.elapsedTime() + " seconds to find \"" +
                TARGET + "\" in these links: ");
        for (URL url : linksWithTarget) {
            StdOut.println(url.toString());
        }
    }
}

class UrlSearcher implements InputScanner<URL> {

    private final List<URL> visited;
    private final Queue<URL> queue;
    public static final String LINK_IDENTIFIER = "href=\"";

    public UrlSearcher() {
        queue = new Queue<>();
        visited = new ArrayList<>();
    }

    public URL[] search(URL url, String target, int max) {
        ArrayList<URL> targetFoundAt = new ArrayList<>();
        queue.enqueue(url);

        while (!queue.isEmpty() && visited.size() <= max) {
            final URL link = queue.dequeue();
            StdOut.println(visited.size());
            if (this.scan(link, target)) {
                targetFoundAt.add(link);
            }
            visited.add(link);
        }

        URL[] returnArray = new URL[targetFoundAt.size()];
        return targetFoundAt.toArray(returnArray);
    }

    /**
     * Scans a given file for an exact match to the word target. We have chosen to use only equals and not contains
     * because simple words like "and" or "or" could easily be included in words like "operand" and "operator"
     *
     * @param url    The link to scan
     * @param target The word to search for
     * @return Whether or not this file contains the target word.
     */
    public boolean scan(final URL url, final String target) {
        boolean foundTarget = false;
        In in = new In(url);
        String[] strings;
        try {
            strings = in.readAllStrings();
        } catch (NullPointerException e) {
            return false;
        }

        for (String word : strings) {

            if (word.startsWith(LINK_IDENTIFIER + "http")) {
                String urlString = word.replace(LINK_IDENTIFIER, "");

                String[] splits = urlString.split("\"(>|)");
                if (splits[0] != null) {

                    urlString = splits[0];
                    try {

                        URL newLink = new URL(urlString);
                        if (!visited.contains(newLink)) {
                            queue.enqueue(new URL(urlString));
                        }
                    } catch (MalformedURLException ignored) {}
                }

                if (splits.length > 1 && splits[1] != null) {
                    word = splits[1];
                }
            }

            if (word.toLowerCase().equals(target) || word.toLowerCase().contains(target)) {
                foundTarget = true;
            }
        }
        return foundTarget;
    }


}