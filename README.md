# Assignment 4. Patient Zero

Student ID: 252673  
Name: **TO COMPLETE**  
Group: **TO COMPLETE**

## Program output

Personal data was generated with `java tools/MakeVariant.java 252673 data`.

```text
Mutations: 91
Shared core: 1213
Marker hits: 6
First hit: 8652
Border length: 4
Shared words: 554
Tests needed: 30
Cheapest testing: 124
Longest chain: 19
```

The generated lab console is [output/outbreak.html](output/outbreak.html).
**Screenshot pending:** open this page locally and save a full-page screenshot as `output/outbreak.png`. Browser capture was attempted, but headless Edge failed to launch and the connected browser timed out reaching localhost. No screenshot has been fabricated. The console's original calculation code was independently executed with Node: all nine values, all hit positions, and coverage by the reconstructed people agree. This verifies calculations, not visual layout or animation behavior.

## Questions

**AI-assisted study drafts: review, understand, and rewrite all five answers in your own words before submission.**

Mission 1. `d[i][j]` is the minimum number of edits changing the first i letters of A into the first j letters of B. The empty-prefix boundaries cost i deletions or j insertions; each other cell takes the minimum of deletion from above, insertion from the left, and diagonal substitution (cost 0 for equal letters, otherwise 1).

Mission 2. A subsequence preserves order but can skip letters; a substring must be contiguous. Insertions and deletions shift corresponding letters, so an LCS can match letters at different positions and be longer than the number of equal letters at identical indices.

Mission 3. `pi[i]` is the longest proper prefix of the pattern prefix ending at i that is also its suffix. KMP reuses these borders on a mismatch, so it changes the pattern pointer without moving the text pointer backward; after a full match it retains the final border to allow overlaps. My marker's border length is 4: its first four letters equal its last four, and no longer proper prefix does.

Mission 4. Different words can have equal hashes, so actual characters must be compared before counting a match. Many collisions make us check many candidates; in the worst case the comparisons cost O(a*b*k), although updating each window hash still takes O(1).

Mission 5. `dp[v][0]` is the cheapest cover of v's subtree when v is untested; `dp[v][1]` is the cheapest when v is tested. An untested v requires every child to be tested, whereas a tested v allows the cheaper state of each child; reconstruction applies the same choices starting at root 0. Degree alone ignores costs: on a three-person path with costs [1,100,1], choosing the highest-degree center costs 100, but choosing both leaves costs 2.

## AI use

OpenAI Codex read the assignment and starter, implemented all five missions, added independent randomized and exhaustive small-case tests, ran the official checker and benchmarks, generated personal data after I supplied my ID, and drafted this report and study explanations. Codex also checked the lab's unmodified JavaScript calculations and attempted browser screenshot capture, which failed. This is AI-generated code and report text; I must review, understand, and revise it before submission rather than claim it is independently authored work. No external website or book code was used. The assignment, starter comments, and supplied Hints were the references.

## Timing (Mission 7)

Measured using the unchanged `Bench`, Java 25.0.1, Windows, average of three runs; milliseconds. Raw console output: [output/benchmark.txt](output/benchmark.txt).

| test | size | time (ms) |
|---|---|---|
| naive search | 20 000 / 40 000 / 80 000 | 57.0 / 124.8 / 248.5 |
| KMP | 20 000 / 40 000 / 80 000 | 2.8 / 1.5 / 0.4 |
| edit distance | 500 / 1000 / 2000 / 4000 | 7.0 / 5.8 / 14.7 / 55.9 |

With the benchmark's fixed pattern length 2000, the naive times approximately double as text size doubles; KMP has O(n+m) work but its very short measurements are dominated by JVM warm-up, so the decreasing measured times do not imply decreasing asymptotic cost. Edit distance is quadratic when both strains grow: the last doubling increases time about 3.8 times, close to the expected factor of four; smaller cases also show warm-up effects. A separate full personal Main run, including JVM startup, reading inputs, calculations, and HTML generation, took approximately 306 ms, well under one minute.

## Complexity

Here a and b are the two strain lengths, n the text length, m the pattern length, k the window length, h the number of hits, and v the number of people.

| algorithm | time | auxiliary space | assumptions |
|---|---|---|---|
| Edit distance | O(a*b) | O(a*b) | Full prefix table, constant work per cell |
| LCS | O(a*b) | O(a*b) | Full prefix table, constant work per cell |
| Prefix function and KMP | O(m), then O(n+m) | O(m+h) | Includes returned hit positions; amortized border fallbacks |
| Rabin-Karp (shared words) | Expected O(a+b+C*k); worst O(a*b*k) | O(b) | C is the number of candidate window comparisons; HashMap expected O(1), fixed k=12 in Main |
| Tree DP and reconstruction | O(v) | O(v) | Valid connected tree; two states per node, recursion depth at most v |
| Longest chain | O(v) | O(v) | Two deepest downward branches per node; recursion stack |

The initial powers/hashes take O(k); after the length checks k is bounded by both strain lengths. The rolling hash uses long arithmetic, a hand-written polynomial hash, and character verification. The recursion is appropriate for assignment trees of about 100 people. Empty pattern means no hits in this implementation; nonpositive window lengths are rejected. Empty strings, short windows, singleton trees, zero costs, overlap, and tied cover choices are tested. Empty-tree method calls are supported, though the unchanged file reader expects at least one person.

## Validation and reproduction

Use a JDK 17 or newer, from this project directory:

```sh
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
java tools/Check.java
javac -encoding UTF-8 -cp out -d out tests/AlgorithmChecks.java
java -cp out AlgorithmChecks
java -cp out Bench
node tools/VerifyLab.cjs
```

On this machine the Oracle `javapath` alias stalled, so validation used `C:\Program Files\Java\jdk-25\bin\java.exe` and `javac.exe` directly. Compilation with `--release 17` also succeeded.

- Official checker: tiny, small, dense each pass all nine lines.
- Independent tests: 8,568 assertions across 400 reproducible randomized trials; recursive edit-distance oracle, enumerated LCS subsequences, direct matching and borders, direct shared-window matching, exhaustive vertex covers with weighted and unit costs, and all-pairs BFS diameter.
- These input-based randomized trials exercise general algorithms without ID-dependent branches; no other student's actual generated files were available for testing.
- Personal HTML: all nine results agree with the original lab calculations, selected vertices cover every edge, and KMP positions agree. Run VerifyLab after Main, since Check overwrites the HTML with the last example.
- Source architecture and signatures preserved. Only Strains.java, Marker.java, and Outbreak.java were changed among starter source files. Main, Input, Render, Generator, Bench, lab.html, Check, and MakeVariant are unchanged.
- Git: an initial starter commit and five separate mission commits exist on main, followed by a report/testing commit. No remote is configured or pushed.

## Before you submit

- [x] All five algorithms implemented and official examples pass.
- [x] Four personal data files included for ID 252673.
- [x] Personal output, measured timings, complexities, and accurate AI-use note recorded.
- [x] At least one meaningful local commit per algorithm mission.
- [ ] Fill name and group; rewrite the question answers in your own words and review every line of code.
- [ ] Open the personal HTML, inspect animations, save `output/outbreak.png`, and add its image link here.
- [ ] Create a public GitHub repository, add origin, push main, and check the link while signed out.
- [ ] Submit the repository link in Moodle before its posted deadline.

## Defense preparation

Work the tiny example by hand: edit distance 1, LCS 13, marker prefix array [0,0,0,1,2], marker hits [0,3], shared words 2, minimum tests 2, minimum cost 6, diameter 4. Explain why each DP state includes exactly the needed prefix or subtree, why KMP preserves overlaps, why a hash equality needs verification, and why an untested tree parent forces tested children. A leaf has skip cost 0, take cost equal to its own price, downward height 0, and diameter 0. Use the supplied defense preparation PDF for practice; AI tools are not allowed during the quiz.
