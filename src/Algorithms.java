
import java.util.ArrayList;
import java.util.List;

public class Algorithms {
    public static int lcs(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    public static int editDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    public static class SearchResult {
        public final List<Integer> indices;
        public final long comparisons;

        public SearchResult(List<Integer> indices, long comparisons) {
            this.indices = indices;
            this.comparisons = comparisons;
        }
    }

    private static SearchResult emptyPattern(int textLength) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i <= textLength; i++) indices.add(i);
        return new SearchResult(indices, 0);
    }

    public static SearchResult naive(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        if (m == 0) return emptyPattern(n);
        List<Integer> indices = new ArrayList<>();
        long comparisons = 0;
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) break;
                j++;
            }
            if (j == m) indices.add(i);
        }
        return new SearchResult(indices, comparisons);
    }

    private static int[] lps(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        for (int i = 1; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                lps[i++] = ++length;
            } else if (length != 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    public static SearchResult kmp(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        if (m == 0) return emptyPattern(n);
        int[] lps = lps(pattern);
        List<Integer> indices = new ArrayList<>();
        long comparisons = 0;
        int i = 0, j = 0;
        while (i < n) {
            comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    indices.add(i - m);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return new SearchResult(indices, comparisons);
    }

    public static SearchResult rabinKarp(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        if (m == 0) return emptyPattern(n);
        List<Integer> indices = new ArrayList<>();
        if (m > n) return new SearchResult(indices, 0);
        long prime = 1_000_000_007L, base = 256;
        long power = 1, patternHash = 0, windowHash = 0;
        for (int i = 1; i < m; i++) power = (power * base) % prime;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * base + pattern.charAt(i)) % prime;
            windowHash = (windowHash * base + text.charAt(i)) % prime;
        }
        long comparisons = 0;
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                int j = 0;
                while (j < m) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) break;
                    j++;
                }
                if (j == m) indices.add(i);
            }
            if (i < n - m) {
                windowHash = (windowHash - text.charAt(i) * power % prime + prime) % prime;
                windowHash = (windowHash * base + text.charAt(i + m)) % prime;
            }
        }
        return new SearchResult(indices, comparisons);
    }

    public static int[] kasai(String text, int[] suffixArray) {
        int n = text.length();
        int[] rank = new int[n];
        int[] lcp = new int[n];
        for (int i = 0; i < n; i++) rank[suffixArray[i]] = i;
        int matched = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] == 0) {
                matched = 0;
                continue;
            }
            int j = suffixArray[rank[i] - 1];
            while (i + matched < n && j + matched < n
                    && text.charAt(i + matched) == text.charAt(j + matched)) {
                matched++;
            }
            lcp[rank[i]] = matched;
            if (matched > 0) matched--;
        }
        return lcp;
    }
}

