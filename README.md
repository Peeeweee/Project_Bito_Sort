# 🧠 Project: BiToSort  
*A Bidirectional Topological Sort Algorithm for Enhanced DAG Processing*

Welcome to **BiToSort** — an innovative algorithm designed to enhance pathfinding in **Directed Acyclic Graphs (DAGs)** by leveraging a **bidirectional topological sort**. Inspired by **Double Selection Sort**, this algorithm simultaneously processes from both ends of the graph, providing optimized sorting and traversal ideal for scheduling, dependency resolution, and more!

---

## 🚀 Features
- 🔁 **Bidirectional Topological Sorting**: Simultaneous forward and backward graph traversal.
- ⚡ **Optimized Path Discovery**: Efficient identification of longest/shortest paths.
- 🧮 **Memory Efficient**: Handles large-scale DAGs with reduced computational load.
- 🔍 **Critical Path Analysis**: Great for task management, compiler design, and routing.
- 🧠 **Inspired by Sorting Algorithms**: Adapts the principles of Double Selection Sort.

---

## 👨‍💻 Authors
- Kent Paulo R. Delgado  
- Theo Benedict S. Pasia

---

## 💡 Background & Motivation

BiToSort was developed to address the **limitations of traditional topological sorting algorithms**, which can be inefficient when applied to **dense graphs** or those with **long dependency chains**. By drawing from the **dual-directional traversal** nature of Double Selection Sort, BiToSort offers a smarter way to uncover paths and sort nodes in DAGs.

---

## 🧰 Core Concepts

### 📐 Topological Sort
- Linear ordering of vertices where for every directed edge `u → v`, vertex `u` comes before `v`.
- Only applicable to **DAGs** (no cycles).
- Typically implemented via **DFS** or **Kahn’s Algorithm**.

### 🔁 Bidirectional Twist
- 🔼 **Forward Traversal**: Begins from sources (nodes with no incoming edges).
- 🔽 **Backward Traversal**: Begins from sinks (nodes with no outgoing edges).
- Merges insights from both directions to reduce redundant traversals.

---

## ⚙️ How It Works

1. Identify **source** (start) and **sink** (end) nodes.
2. Process one node from each direction **simultaneously**.
3. Update neighbors and predecessors.
4. Repeat until all nodes are sorted or a cycle is detected.
5. Produce a **balanced, efficient topological order**.

---

## 🧪 Sample Implementation (Pseudocode)

```python
def bitoSort(graph):
    n = len(graph.vertices)
    forward_visited = [False] * n
    backward_visited = [False] * n
    result = []

    sources = [v for v in graph.vertices if v.in_degree == 0]
    sinks = [v for v in graph.vertices if v.out_degree == 0]

    while sources and sinks:
        if sources:
            current = sources.pop(0)
            if not forward_visited[current.id]:
                forward_visited[current.id] = True
                result.append(current)
                for neighbor in current.out_neighbors:
                    neighbor.in_degree -= 1
                    if neighbor.in_degree == 0:
                        sources.append(neighbor)

        if sinks:
            current = sinks.pop(0)
            if not backward_visited[current.id]:
                backward_visited[current.id] = True
                if current not in result:
                    result.append(current)
                for pred in current.in_neighbors:
                    pred.out_degree -= 1
                    if pred.out_degree == 0:
                        sinks.append(pred)

    return result if len(result) == n else "Graph has a cycle, topological sort not possible"
