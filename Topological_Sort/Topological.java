import java.util.*;

class Edge {
    final int destination;
    final int weight;

    public Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }
}

public class Topological {
    private final int V;
    private final List<Edge>[] adj;
    private static final int CHUNK_SIZE = 1000;

    @SuppressWarnings("unchecked")
    public Topological(int V) {
        this.V = V;
        adj = new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    public List<Edge>[] getAdjList() {
        return adj;
    }

    public int getVertices() {
        return V;
    }

    public void addEdge(int source, int destination, int weight) {
        if (source >= 0 && source < V && destination >= 0 && destination < V) {
            adj[source].add(new Edge(destination, weight));
        }
    }

    // Optimized iterative topological sort using Kahn's algorithm
    private int[] topologicalSort() {
        int[] result = new int[V];
        int index = 0;

        // Calculate in-degree for each vertex
        int[] inDegree = new int[V];
        for (List<Edge> edges : adj) {
            for (Edge edge : edges) {
                inDegree[edge.destination]++;
            }
        }

        // Use array-based queue instead of Deque for better performance
        int[] queue = new int[V];
        int front = 0, rear = 0;

        // Add vertices with 0 in-degree to queue
        for (int i = 0; i < V; i++) {
            if (inDegree[i] == 0) {
                queue[rear++] = i;
            }
        }

        while (front < rear) {
            int vertex = queue[front++];
            result[index++] = vertex;

            for (Edge edge : adj[vertex]) {
                if (--inDegree[edge.destination] == 0) {
                    queue[rear++] = edge.destination;
                }
            }
        }

        return index == V ? result : new int[0]; // Return empty array if cycle detected
    }

    public void findLongestPath(int source) {
        findLongestPath(source, V - 1);
    }

    public void findLongestPath(int source, int target) {
        if (source < 0 || source >= V || target < 0 || target >= V) {
            System.out.println("Invalid source or target vertex");
            return;
        }

        int[] topoOrder = topologicalSort();
        if (topoOrder.length == 0) {
            System.out.println("Graph contains a cycle");
            return;
        }

        // Use long array to prevent integer overflow in large graphs
        long[] dist = new long[V];
        Arrays.fill(dist, Long.MIN_VALUE);
        dist[source] = 0;

        // Process vertices in topological order
        for (int vertex : topoOrder) {
            if (dist[vertex] != Long.MIN_VALUE) {
                for (Edge edge : adj[vertex]) {
                    long newDist = dist[vertex] + edge.weight;
                    if (newDist > dist[edge.destination]) {
                        dist[edge.destination] = newDist;
                    }
                }
            }
        }

        // Print result
        if (dist[target] == Long.MIN_VALUE) {
            System.out.println("No path exists from vertex " + source + " to vertex " + target);
        } else {
            System.out.println("Longest distance from vertex " + source + " to vertex " + target +
                    " is " + dist[target]);
        }
    }

    public static Topological generateRandomGraph(int vertices, int edges) {
        if (edges > (long)vertices * (vertices - 1) / 2) {
            throw new IllegalArgumentException("Too many edges for a DAG with " + vertices + " vertices");
        }

        Topological graph = new Topological(vertices);
        Random rand = new Random();

        // Process edges in chunks to manage memory
        long remainingEdges = edges;

        // Use BitSet instead of HashSet for edge tracking
        BitSet existingEdges = new BitSet(vertices * vertices);

        for (int source = 0; source < vertices - 1 && remainingEdges > 0; source++) {
            int maxEdgesForVertex = Math.min((int)remainingEdges, vertices - source - 1);
            int edgesForThisVertex = Math.min(maxEdgesForVertex, CHUNK_SIZE);

            for (int e = 0; e < edgesForThisVertex; e++) {
                // Only try to add edges to vertices after the current source
                int destination = source + 1 + rand.nextInt(vertices - source - 1);
                int edgeIndex = source * vertices + destination;

                if (!existingEdges.get(edgeIndex)) {
                    int weight = rand.nextInt(21) - 10;
                    graph.addEdge(source, destination, weight);
                    existingEdges.set(edgeIndex);
                    remainingEdges--;
                }
            }
        }

        return graph;
    }

    public static void main(String[] args) {
        Topological a = new Topological(6);
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
        a.findLongestPath(0, 5);
        try {
            // Test with different sizes
            int[] testSizes = {10, 10, 10, 10, 10};

            for (int V : testSizes) {
                System.out.println("\nTesting with " + V + " vertices");

                // Calculate reasonable number of edges (10% of max possible edges)
                int E = Math.min((V * (V - 1)) / 10, V * 20);

                System.gc(); // Suggest garbage collection before large operation

                long startGen = System.nanoTime();
                Topological graph = generateRandomGraph(V, E);
                long endGen = System.nanoTime();

                System.out.println("Graph generation time: " +
                        (endGen - startGen) / 1_000_000.0 + " ms");

                long startPath = System.nanoTime();
                graph.findLongestPath(0);
                long endPath = System.nanoTime();

                System.out.println("Path finding time: " +
                        (endPath - startPath) / 1_000_000.0 + " ms");
            }

        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory error. Try running with -Xmx4g or higher");
            e.printStackTrace();
        }
    }
}