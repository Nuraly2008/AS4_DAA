
import java.util.Arrays;
import java.util.List;

public class Tests {
    private static int passed = 0;

    private static void check(Object actual, Object expected) {
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
        passed++;
    }

    private static void search(String text, String pattern, List<Integer> expected) {
        Algorithms.SearchResult a = Algorithms.naive(text, pattern);
        Algorithms.SearchResult b = Algorithms.kmp(text, pattern);
        Algorithms.SearchResult c = Algorithms.rabinKarp(text, pattern);
        check(a.indices, expected);
        check(b.indices, expected);
        check(c.indices, expected);
        check(a.indices, b.indices);
        check(b.indices, c.indices);
    }

    public static void main(String[] args) {
        check(Algorithms.lcs("abc", "abc"), 3);
        check(Algorithms.lcs("abc", "xyz"), 0);
        check(Algorithms.lcs("", "abc"), 0);

        check(Algorithms.editDistance("abc", "abc"), 0);
        check(Algorithms.editDistance("", "abc"), 3);
        check(Algorithms.editDistance("abc", ""), 3);
        check(Algorithms.editDistance("cat", "cut"), 1);
        check(Algorithms.editDistance("cat", "cats"), 1);
        check(Algorithms.editDistance("cats", "cat"), 1);

        search("abcde", "ab", List.of(0));
        search("abcde", "bc", List.of(1));
        search("abcde", "de", List.of(3));
        search("bananana", "ana", List.of(1, 3, 5));
        search("abcde", "xyz", List.of());
        search("abc", "abcd", List.of());
        search("abc", "", List.of(0, 1, 2, 3));
        search("aaaaa", "aa", List.of(0, 1, 2, 3));

        check(Arrays.toString(Algorithms.kasai("banana", new int[]{5, 3, 1, 0, 4, 2})),
                "[0, 1, 3, 0, 0, 2]");
        check(Arrays.toString(Algorithms.kasai("", new int[]{})), "[]");
        System.out.println("All " + passed + " checks passed.");
    }
}
