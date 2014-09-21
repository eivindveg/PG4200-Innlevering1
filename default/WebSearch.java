import edu.princeton.cs.algorithms.Queue;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdOut;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WebSearch {

    public static final String INITIAL_LINK = "http://vg.no";
    public static final String TARGET = "Petter";

    public static void main(String[] args) throws MalformedURLException {
        UrlSearcher searcher = new UrlSearcher();
        NanoStopwatch timer = new NanoStopwatch();

        URL[] linksWithTarget = searcher.search(new URL(INITIAL_LINK), TARGET.toLowerCase(), 1000);
        StdOut.println(searcher.getClass().getSimpleName() + " spent " + timer.elapsedTime() + " seconds to find \"" +
                TARGET + "\" in these links: ");
        for (URL url : linksWithTarget) {
            StdOut.println(url.toString());
        }
    }
}

class UrlSearcher implements InputScanner<URL> {

    private final Set<URL> discovered;
    private final Queue<URL> queue;
    public static final String LINK_IDENTIFIER = "href=\"";
    private int max;

    public UrlSearcher() {
        queue = new Queue<>();
        discovered = new HashSet<>();
    }

    /**
     * Searches a target URL recursively for the target string, up to a max number of links and returns all the links
     * containing the target string as a word in the link or in its content body.
     *
     * @param url    The link to search from
     * @param target The word to search for
     * @param max    Max number of links to open
     * @return An array of URLs containing all the links and pages matching the search
     */
    public URL[] search(URL url, String target, int max) {
        this.max = max;
        final ArrayList<URL> targetFoundAt = new ArrayList<>();
        queue.enqueue(url);

        while (!queue.isEmpty()) {
            final URL link = queue.dequeue();
            StdOut.println(discovered.size());
            discovered.add(url);
            if (this.scan(link, target)) {
                targetFoundAt.add(link);
            }
        }

        URL[] returnArray = new URL[targetFoundAt.size()];
        return targetFoundAt.toArray(returnArray);
    }

    /**
     * Scans a given file for an exact match to the word target. We have chosen to use equals and contains so that sites
     * including the target in the URL get included.
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
            // If the word can be identified as a link
            if (word.startsWith(LINK_IDENTIFIER + "http")) {

                /*
                    Prepares the link for construction into a URL. First it replaces the link identifier, typically
                    a string like href=", however this is only valid for the beginning. We later use a split to remove the
                    " following the actual URL, as well as possibly a tag closure like >. The reason for implementing this
                    with split, is that we want to preserve the word immediately following the tag closure if applicable.
                    This is because this may be a word matching target, so we later replace word with the result of that
                    split where applicable.
                 */
                String[] splits = word.replace(LINK_IDENTIFIER, "").split("\"(>|)");
                if (splits[0] != null) {
                    splits[0] = splits[0].replace("&amp;", "&");
                    try {
                        URL newLink = new URL(splits[0]);

                        // Don't enqueue the link if we've already discovered it.
                        if (!discovered.contains(newLink) && discovered.size() < max) {
                            queue.enqueue(newLink);
                            discovered.add(newLink);
                        }
                    } catch (MalformedURLException ignored) {
                        // We have already turned the string in question into a valid link. It should not be identified
                        // as a link if it does not conform to the above statements. It could still be saved with more
                        // complex parsing, but we don't wish to expend resources parsing javascripts or by analysing
                        // patterns to determine what to fix.
                    }
                }
                if (splits.length > 1 && splits[1] != null) {
                    word = splits[1];
                }
            }

            // Make sure we check for lower case values when matching words.
            if (word.toLowerCase().equals(target) || word.toLowerCase().contains(target)) {
                foundTarget = true;
            }
        }
        return foundTarget;
    }
}