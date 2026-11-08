
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Experiments {
    private static final Random RANDOM = new Random(42);
    private static final String ALPHABET = "abcd";
    private static final int RUNS = 5;
    private static volatile long sink;

    private static String randomString(int size) {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private static int[] suffixArray(String text) {
        Integer[] order = new Integer[text.length()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                int i = a, j = b;
                while (i < text.length() && j < text.length()) {
                    if (text.charAt(i) != text.charAt(j)) {
                        return Character.compare(text.charAt(i), text.charAt(j));
                    }
                    i++;
                    j++;
                }
                return Integer.compare(text.length() - a, text.length() - b);
            }
        });
        int[] result = new int[order.length];
        for (int i = 0; i < order.length; i++) result[i] = order[i];
        return result;
    }

    private static void dp(String name, String a, String b) {
        for (int i = 0; i < 3; i++) {
            sink += name.equals("LCS") ? Algorithms.lcs(a, b) : Algorithms.editDistance(a, b);
        }
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            int answer = name.equals("LCS") ? Algorithms.lcs(a, b) : Algorithms.editDistance(a, b);
            total += System.nanoTime() - start;
            sink += answer;
        }
        System.out.printf("DP,%s,-,%d,%.0f,-%n", name, a.length(), (double) total / RUNS);
    }

    private static Algorithms.SearchResult search(String name, String text, String pattern) {
        switch (name) {
            case "Naive": return Algorithms.naive(text, pattern);
            case "KMP": return Algorithms.kmp(text, pattern);
            default: return Algorithms.rabinKarp(text, pattern);
        }
    }

    private static void match(String name, String label, String text, String pattern) {
        for (int i = 0; i < 3; i++) sink += search(name, text, pattern).indices.size();
        long total = 0, comparisons = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            Algorithms.SearchResult result = search(name, text, pattern);
            total += System.nanoTime() - start;
            comparisons += result.comparisons;
            sink += result.indices.size();
        }
        System.out.printf("SEARCH,%s,%s,%d,%.0f,%d%n", name, label, text.length(),
                (double) total / RUNS, comparisons / RUNS);
    }

    private static void kasai(String text) {
        int[] sa = suffixArray(text);
        for (int i = 0; i < 3; i++) sink += Algorithms.kasai(text, sa).length;
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            int[] lcp = Algorithms.kasai(text, sa);
            total += System.nanoTime() - start;
            sink += lcp.length;
        }
        System.out.printf("KASAI,Kasai,-,%d,%.0f,-%n", text.length(), (double) total / RUNS);
    }

    public static void main(String[] args) {
        System.out.println("group,algorithm,case,size,avg_ns,char_comparisons");
        for (int size : new int[]{100, 500, 1000}) {
            String a = randomString(size), b = randomString(size);
            dp("LCS", a, b);
            dp("EditDistance", a, b);
        }
        for (int size : new int[]{10_000, 50_000, 100_000}) {
            String text = randomString(size);
            String present = text.substring(size / 2, size / 2 + 20);
            String absent;
            do {
                absent = randomString(20);
            } while (!Algorithms.naive(text, absent).indices.isEmpty());
            for (String name : new String[]{"Naive", "KMP", "RabinKarp"}) {
                match(name, "present", text, present);
                match(name, "absent", text, absent);
            }
        }
        for (int size : new int[]{1000, 5000, 10000}) kasai(randomString(size));
    }
}

