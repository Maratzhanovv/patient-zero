import java.util.*;

/** Independent small-case oracles; run with -cp out after compiling this file. */
public class AlgorithmChecks {
    static int checks;
    static void check(boolean ok) {
        checks++;
        if (!ok) throw new AssertionError("Check " + checks);
    }
    static int edit(String a, String b) {
        if (a.isEmpty()) return b.length();
        if (b.isEmpty()) return a.length();
        return Math.min(edit(a.substring(1), b.substring(1)) + (a.charAt(0) == b.charAt(0) ? 0 : 1),
                1 + Math.min(edit(a.substring(1), b), edit(a, b.substring(1))));
    }
    static int lcs(String a, String b) {
        int best = 0;
        for (int mask = 0; mask < (1 << a.length()); mask++) {
            int j = 0, length = 0;
            boolean ok = true;
            for (int i = 0; i < a.length(); i++) if ((mask & (1 << i)) != 0) {
                while (j < b.length() && b.charAt(j) != a.charAt(i)) j++;
                if (j == b.length()) { ok = false; break; }
                j++; length++;
            }
            if (ok) best = Math.max(best, length);
        }
        return best;
    }
    static String dna(Random r, int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) s.append("ACGT".charAt(r.nextInt(4)));
        return s.toString();
    }
    public static void main(String[] args) {
        Random r = new Random(42026);
        check(Marker.findAll("AAA", "").length == 0);
        check(Arrays.equals(Marker.findAll("AAAA", "AA"), new int[]{0,1,2}));
        check(Marker.sharedWords("AAAAA", "AAA", 3) == 3);
        check(Outbreak.longestChain(0, new int[0][]) == 0);
        check(Outbreak.cheapestTests(0, new int[0][], new int[0]).length == 0);
        for (int trial = 0; trial < 400; trial++) {
            String a = dna(r, r.nextInt(6)), b = dna(r, r.nextInt(6));
            check(Strains.editDistance(a, b) == edit(a, b));
            check(Strains.lcsLength(a, b) == lcs(a, b));
            String t = dna(r, r.nextInt(80)), p = dna(r, 1 + r.nextInt(8));
            List<Integer> expected = new ArrayList<>();
            for (int i = 0; i + p.length() <= t.length(); i++)
                if (t.substring(i, i + p.length()).equals(p)) expected.add(i);
            check(Arrays.equals(Marker.findAll(t, p), expected.stream().mapToInt(x -> x).toArray()));
            int[] pi = Marker.prefixFunction(p);
            for (int i = 0; i < p.length(); i++) {
                int border = 0;
                for (int k = 1; k <= i; k++)
                    if (p.substring(0, k).equals(p.substring(i + 1 - k, i + 1))) border = k;
                check(pi[i] == border);
            }
            int k = 1 + r.nextInt(7), shared = 0;
            for (int i = 0; i + k <= a.length(); i++) {
                for (int j = 0; j + k <= b.length(); j++) {
                    if (a.substring(i, i + k).equals(b.substring(j, j + k))) { shared++; break; }
                }
            }
            check(Marker.sharedWords(a, b, k) == shared);
            int n = 1 + r.nextInt(11);
            int[] x = new int[n - 1], y = new int[n - 1], cost = new int[n];
            for (int i = 1; i < n; i++) { x[i-1] = i; y[i-1] = r.nextInt(i); }
            for (int i = 0; i < n; i++) cost[i] = r.nextInt(10);
            int[][] nb = new Input.Tree(n, x, y, cost).neighbours();
            for (int mode = 0; mode < 2; mode++) {
                int[] weights = cost.clone();
                if (mode == 1) Arrays.fill(weights, 1);
                long best = Long.MAX_VALUE;
                for (int mask = 0; mask < (1 << n); mask++) {
                    boolean valid = true;
                    for (int e = 0; e < x.length; e++)
                        if ((mask & ((1 << x[e]) | (1 << y[e]))) == 0) valid = false;
                    if (!valid) continue;
                    long sum = 0;
                    for (int i = 0; i < n; i++) if ((mask & (1 << i)) != 0) sum += weights[i];
                    best = Math.min(best, sum);
                }
                boolean[] tested = Outbreak.cheapestTests(n, nb, weights);
                long sum = 0;
                for (int i = 0; i < n; i++) if (tested[i]) sum += weights[i];
                check(sum == best);
                for (int e = 0; e < x.length; e++) check(tested[x[e]] || tested[y[e]]);
            }
            int diameter = 0;
            for (int start = 0; start < n; start++) {
                int[] dist = new int[n]; Arrays.fill(dist, -1); dist[start] = 0;
                ArrayDeque<Integer> q = new ArrayDeque<>(); q.add(start);
                while (!q.isEmpty()) {
                    int v = q.remove();
                    for (int u : nb[v]) if (dist[u] < 0) {
                        dist[u] = dist[v] + 1; diameter = Math.max(diameter, dist[u]); q.add(u);
                    }
                }
            }
            check(Outbreak.longestChain(n, nb) == diameter);
        }
        System.out.println("Passed " + checks + " independent assertions across 400 seeded trials.");
    }
}
