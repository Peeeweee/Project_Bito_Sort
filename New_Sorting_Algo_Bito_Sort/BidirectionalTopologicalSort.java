import java.util.*;
import java.util.concurrent.*;

public class BidirectionalTopologicalSort {
    private final int vertices;
    private final ArrayList<Edge>[] forwardGraph;
    private final ArrayList<Edge>[] reverseGraph;
    private final int[] inDegree;
    private final int[] result;
    private static final int PARALLEL_THRESHOLD = 10000;

    private static final class Edge {
        final int to;
        final int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    @SuppressWarnings("unchecked")
    public BidirectionalTopologicalSort(int vertices) {
        this.vertices = vertices;
        this.forwardGraph = new ArrayList[vertices];
        this.reverseGraph = new ArrayList[vertices];
        this.inDegree = new int[vertices];
        this.result = new int[vertices];

        for (int i = 0; i < vertices; i++) {
            forwardGraph[i] = new ArrayList<>();
            reverseGraph[i] = new ArrayList<>();
        }
    }

    public void addEdge(int from, int to, int weight) {
        forwardGraph[from].add(new Edge(to, weight));
        reverseGraph[to].add(new Edge(from, weight));
        inDegree[to]++;
    }

    public int longestPath(int source, int destination) {
        if (source < 0 || source >= vertices || destination < 0 || destination >= vertices) {
            return -1;
        }

        int[] forwardDist = new int[vertices];
        int[] backwardDist = new int[vertices];
        Arrays.fill(forwardDist, Integer.MIN_VALUE);
        Arrays.fill(backwardDist, Integer.MIN_VALUE);
        forwardDist[source] = 0;
        backwardDist[destination] = 0;

        return vertices >= PARALLEL_THRESHOLD
                ? processParallel(forwardDist, backwardDist)
                : processSequential(forwardDist, backwardDist);
    }

    private int processParallel(int[] forwardDist, int[] backwardDist) {
        try {
            CompletableFuture<Void> forwardTask = CompletableFuture.runAsync(() -> processAndUpdate(forwardDist, forwardGraph));
            CompletableFuture<Void> backwardTask = CompletableFuture.runAsync(() -> processAndUpdate(backwardDist, reverseGraph));

            CompletableFuture.allOf(forwardTask, backwardTask).join();
            return findMeetingPoint(forwardDist, backwardDist);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int processSequential(int[] forwardDist, int[] backwardDist) {
        processAndUpdate(forwardDist, forwardGraph);
        processAndUpdate(backwardDist, reverseGraph);
        return findMeetingPoint(forwardDist, backwardDist);
    }

    private void processAndUpdate(int[] dist, ArrayList<Edge>[] graph) {
        Queue<Integer> queue = new ArrayDeque<>();
        int[] localInDegree = Arrays.copyOf(inDegree, vertices);

        for (int i = 0; i < vertices; i++) {
            if (localInDegree[i] == 0) {
                queue.add(i);
            }
        }

        while (!queue.isEmpty()) {
            int node = queue.poll();

            for (Edge edge : graph[node]) {
                int newDist = dist[node] == Integer.MIN_VALUE ? Integer.MIN_VALUE : dist[node] + edge.weight;
                if (dist[edge.to] < newDist) {
                    dist[edge.to] = newDist;
                }

                if (--localInDegree[edge.to] == 0) {
                    queue.add(edge.to);
                }
            }
        }
    }

    private int findMeetingPoint(int[] forwardDist, int[] backwardDist) {
        int maxPath = Integer.MIN_VALUE;

        for (int v = 0; v < vertices; v++) {
            if (forwardDist[v] != Integer.MIN_VALUE && backwardDist[v] != Integer.MIN_VALUE) {
                maxPath = Math.max(maxPath, forwardDist[v] + backwardDist[v]);
            }
        }

        return maxPath == Integer.MIN_VALUE ? -1 : maxPath;
    }

    public static void main(String[] args) {
        testPerformance();
    }

    private static void testPerformance() {
        BidirectionalTopologicalSort a = new BidirectionalTopologicalSort(6);
        a.addEdge(0, 1, 5);
        a.addEdge(0, 2, 3);
        a.addEdge(1, 2, 2);
        a.addEdge(1, 3, 6);
        a.addEdge(2, 3, 7);
        a.addEdge(2, 4, 4);
        a.addEdge(2, 5, 2);
        a.addEdge(3, 5, 1);
        a.addEdge(3, 4, -1);
        a.addEdge(4, 5, -2);
        System.out.println("Longest Path from 0 to "+(a.vertices-1)+" is: "+a.longestPath(0, 5));
        try {
            int[] testSizes = {10, 10, 10, 10, 10};

            for (int V : testSizes) {
                System.out.println("\nTesting with " + V + " vertices");
                int E = Math.min((V * (V - 1)) / 10, V * 20);

                long startGen = System.nanoTime();
                BidirectionalTopologicalSort largeGraph = generateRandomGraph(V, E);
                long endGen = System.nanoTime();

                System.out.println("Graph generation time: " + (endGen - startGen) / 1_000_000.0 + " ms");

                long startPath = System.nanoTime();
                int result = largeGraph.longestPath(0, V - 1);
                long endPath = System.nanoTime();

                System.out.println("Longest path from 0 to " + (V - 1) + ": " + result);
                System.out.println("Path finding time: " + (endPath - startPath) / 1_000_000.0 + " ms");
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory error. Try running with -Xmx4g or higher.");
            e.printStackTrace();
        }
    }

    private static BidirectionalTopologicalSort generateRandomGraph(int vertices, int edges) {
        BidirectionalTopologicalSort graph = new BidirectionalTopologicalSort(vertices);
        Random random = ThreadLocalRandom.current();

        for (int i = 0; i < vertices - 1; i++) {
            graph.addEdge(i, i + 1, random.nextInt(10) + 1);
        }

        for (int i = 0; i < edges - (vertices - 1); i++) {
            int from = random.nextInt(vertices - 1);
            int to = random.nextInt(vertices - from - 1) + from + 1;
            int weight = random.nextInt(10) + 1;
            graph.addEdge(from, to, weight);
        }

        return graph;
    }
}
