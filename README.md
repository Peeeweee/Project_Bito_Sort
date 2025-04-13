# BiToSort: Bidirectional Topological Sort Algorithm

## Overview
BiToSort is an innovative algorithm that enhances path finding in Directed Acyclic Graphs (DAGs) by combining traditional topological sorting with a bidirectional approach. Inspired by Double Selection Sort, BiToSort processes vertices from both ends simultaneously, leading to more efficient identification of longest/shortest paths in DAGs.

## Authors
- Delgado, Kent Paulo R.
- Pasia, Theo Benedict S.

## Background & Motivation
The development of BiToSort stemmed from recognizing the limitations of traditional topological sorting algorithms, particularly when dealing with complex graphs with multiple dependencies and long paths. By incorporating a bidirectional approach, BiToSort addresses these challenges more efficiently.

### Inspiration
BiToSort draws inspiration from Double Selection Sort, which traverses data in dual directions to achieve faster sorting. This concept has been adapted for graph traversal to optimize path finding in DAGs.

## Core Concepts

### Topological Sort
- A linear ordering of vertices where for every directed edge u→v, vertex u comes before v
- Only possible in DAGs (graphs without cycles)
- Traditionally implemented using algorithms like Depth-First Search (DFS) or Kahn's Algorithm

### Bidirectional Aspect
- **Forward direction**: Traditional topological sort order
- **Backward direction**: Reverse topological sort order
- Dual approach optimizes path finding by processing from both ends simultaneously

## How BiToSort Works

BiToSort processes the graph by:
1. Simultaneously selecting two nodes: one from the start and one from the end of the graph
2. Concurrently processing these nodes and their relationships
3. Reducing the need for multiple graph traversals by working from both directions
4. Pinpointing key nodes in the DAG using principles similar to Double Selection Sort
5. Creating a more evenly distributed topological sort

## Advantages Over Traditional Methods

- **Reduced Search Space**: By working from both directions, BiToSort significantly narrows down the search space
- **Faster Convergence**: Identifies critical path intersections earlier in the process
- **Optimized for Dense Graphs**: Particularly effective for graphs with heavy dependencies
- **Reduced Computational Overhead**: Minimizes redundant calculations by processing in dual directions
- **Enhanced Adaptability**: Better flexibility in adjusting to dynamic graph changes
- **Memory Efficiency**: More efficient memory usage for large-scale graphs

## Applications

BiToSort is particularly useful for:
- Project scheduling and task dependency management
- Network routing optimization
- Critical path analysis in project management
- Dependency resolution in build systems
- Compiler optimization for code generation

## Implementation

```python
# Pseudocode for BiToSort Algorithm
def bitoSort(graph):
    # Initialize data structures
    n = len(graph.vertices)
    forward_visited = [False] * n
    backward_visited = [False] * n
    result = []
    
    # Get nodes with no incoming edges (sources)
    sources = [v for v in graph.vertices if v.in_degree == 0]
    
    # Get nodes with no outgoing edges (sinks)
    sinks = [v for v in graph.vertices if v.out_degree == 0]
    
    while sources and sinks:
        # Process from forward direction
        if sources:
            current = sources.pop(0)
            if not forward_visited[current.id]:
                forward_visited[current.id] = True
                result.append(current)
                
                # Update neighbors
                for neighbor in current.out_neighbors:
                    neighbor.in_degree -= 1
                    if neighbor.in_degree == 0:
                        sources.append(neighbor)
        
        # Process from backward direction
        if sinks:
            current = sinks.pop(0)
            if not backward_visited[current.id]:
                backward_visited[current.id] = True
                if current not in result:
                    result.append(current)
                
                # Update predecessors
                for pred in current.in_neighbors:
                    pred.out_degree -= 1
                    if pred.out_degree == 0:
                        sinks.append(pred)
    
    # Check if all vertices are processed
    if len(result) == n:
        return result
    else:
        return "Graph has a cycle, topological sort not possible"
```

## Performance Analysis

BiToSort shows significant performance improvements compared to traditional topological sort algorithms, especially for:
- Large graphs with complex dependency structures
- Graphs with long chains of dependencies
- Dense graphs with many interconnections

## Contributing

Contributions to improve BiToSort or adapt it for specific use cases are welcome. Please feel free to fork the repository, make your changes, and submit a pull request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## References

- GeeksforGeeks. (2024). Topological sorting. https://www.geeksforgeeks.org/topological-sorting/
- Btd. (2024). An overview of bidirectional processing in deep learning. Medium.
- Simic, M. (2024). Bidirectional search for path finding. Baeldung on Computer Science.
- Data Structures. (2023). Topological Sort: Common Pitfalls and Misconceptions.
- Prepbytes. (2023). Topological sort: Algorithm, examples and advantages.
