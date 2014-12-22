/* This class represents a Grpah and provides methods to perform different actions on it
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Graph {
	
	private class Edge {
		public int a;
		public int b;
		public int length;
	}

	private int[][] adjMatrix;
	private int size;
	
	//creates a graph from a graph file
	public Graph(String filename) throws FileNotFoundException {
		File f = new File(filename);
		Scanner scan = new Scanner(f);
		size = scan.nextInt();
		adjMatrix = new int[size][size];
		for(int i = 0; i < size; ++i) {
			int edges = scan.nextInt();
			for(int j = 0; j < edges; ++j)
				adjMatrix[i][scan.nextInt()] = scan.nextInt();
		}
		scan.close();
	}
	
	//creates Graph without any edges
	public Graph(int size) {
		this.size = size;
		adjMatrix = new int[size][size];
	}
	
	public int size() {
		return size;
	}
	
	private void addEdge(int a, int b, int length) {
		//does not do any checks on a and b
		adjMatrix[a][b] = length;
		adjMatrix[b][a] = length;
	}
	
	private void addEdge(Edge e) {
		addEdge(e.a, e.b, e.length);
	}
	
	//checks if the graph is connected
	public boolean isConnected() {
		int[] num = new int[size + 1];
		num[size] = 1;
		//number the nodes using dfs
		dfs(num, 0);
		//if there are not numbered nodes left, the graph is not connected
		for(int i = 0; i < size; i++)
			if(num[i] == 0)
				return false;
		return true;
	}
	
	//numbers the nodes in the Graph according to a depth first search
	//numbers are put into num, num[size] is used as the counter
	private void dfs(int[] num, int node) {
		num[node] = num[size]++;
		for(int i : adjacentNodes(node))
			if(num[i] == 0)
				dfs(num, i);
	}
	
	//loops over the adjacency  matrix and creates list of nodes adjacent to start
	private LinkedList<Integer> adjacentNodes(int node) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		for(int i = 0; i < size; ++i)
			if(adjMatrix[node][i] != 0)
				list.add(i);
		return list;
	}
	
	//formats the string like the input file
	public String toString() {
		String s = "";
		s += size + "\n";
		for(int i = 0; i < size; ++i) {
			List<Integer> edges = adjacentNodes(i);
			s += edges.size() + " ";
			for(int edge : edges)
				s += edge + " " + adjMatrix[i][edge] + " ";
			s += "\n";
		}
		return s;
	}
	
	//prims algorithm
	//returns the minimum spanning tree as a Graph
	public Graph minimumSpanningTree() {
		Graph mst = new Graph(size);
		//sets
		LinkedList<Integer> S = new LinkedList<Integer>();
		//start the tree at 0
		S.add(0);
		LinkedList<Integer> V = new LinkedList<Integer>();
		for(int i = 1; i < size; ++i)	
			V.add(i);
		//find shortest edges from S to V and add it to the mst until V is empty
		while(! V.isEmpty()) {
			Edge shortest = shortestEdge(S, V);
			mst.addEdge(shortest);
			//move newly connected node from V to S
			V.remove((Integer)shortest.b);
			S.add(shortest.b);
		}
		return mst;
	}
	
	//shortest edge from set to set
	private Edge shortestEdge(List<Integer> S, List<Integer> V) {
		Edge shortest = new Edge();
		shortest.length = Integer.MAX_VALUE;
		//loops through all edges in S
		for(int s : S)
			for(int adjacent : adjacentNodes(s))
				//if the edge links to V and is shorter than the current shortest save the edge
				if(V.contains(adjacent) && adjMatrix[s][adjacent] < shortest.length) {
					shortest.a = s;
					shortest.b = adjacent;
					shortest.length = adjMatrix[s][adjacent];
				}
		return shortest;
	}
	
	public void printShortestPath(int start) {
		//setup arrays
		int[] distances = new int[size];
		int[] previous = new int[size];
		for(int i = 0; i < previous.length; i++)
			previous[i] = -1;
		//call dijkstra's algorithm
		shortestPath(start, distances, previous);
		//format results
		for(int i = 0; i < size; i++) {
			if(distances[i] < Integer.MAX_VALUE) {
				System.out.print(i + ": (" + distances[i] + ")\t");
				String path = "" + i;
				int pred = previous[i];
				//will eventually reach the start node who's predecessor will be not set(-1)
				while(pred != -1) {
					path = pred + " -> " + path;
					pred = previous[pred];
				}
				System.out.print(path);
			} else
				System.out.print(i + ": (Infinity)");
			System.out.println();
		}
	}
	
	//dijkstra's algorithm
	//puts the shortest distances to start in distances and keeps track of previous nodes in previous
	private void shortestPath(int start, int[] distances, int[] previous) {
		//create two sets
		LinkedList<Integer> S = new LinkedList<Integer>();
		LinkedList<Integer> V = new LinkedList<Integer>();
		for(int i = 0; i < size; ++i)
			V.add(i);
		//set distances to inf
		for(int i = 0; i < size; ++i)
			distances[i] = Integer.MAX_VALUE;
		//set distance of start to 0;
		distances[start] = 0;
		//while there is a edge from V to S
		while(S.isEmpty() || shortestEdge(S, V).length < Integer.MAX_VALUE) {
		//while(! V.isEmpty()) {
			//find node with closest distance to start
			int u = V.getFirst();
			for(int i : V)
				if(distances[i] < distances[u])
					u = i;
			//update distances
			for(int v : adjacentNodes(u))
				if(distances[v] > distances[u] + adjMatrix[v][u]) {
					distances[v] = distances[u] + adjMatrix[v][u];
					previous[v] = u;
				}
			//move u from V to S
			V.remove((Integer)u);
			S.add(u);
		}
	}
	
	//returns array with shortest distances from node start
	private int[] shortestDistances(int start) {
		int[] distances = new int[size];
		int[] previous = new int[size];
		shortestPath(start, distances, previous);
		return distances;
	}
	
	//checks if the graph is completely connected
	public boolean isCompletelyConnected() {
		//if every node has the right number of edges (size-1) the graph is completly connected
		for(int i = 0; i < size; ++i)
			if(adjacentNodes(i).size() != size - 1)
				return false;
		return true;
	}
	
	//checks is the graph obeys the triangle inequality
	public boolean obeysTriangleInEq() {
		//loop through all nodes
		for(int i = 0; i < size; ++i) {
			int[] shortest = shortestDistances(i);
			for(int adj : adjacentNodes(i))
				//if direct edge between nodes is not the
				//shortest path the triangle ineq is not fulfilled
				if(adjMatrix[i][adj] != shortest[adj]) {
					System.out.println(i + ":" + adj);
					return false;
				}
		}
		return true;
	}
	
	//adds edges to the graph to make it metric
	public void makeMetric() {
		for(int i = 0; i < size; ++i) {
			//get shortest distances and set add them as edges
			int[] shortest = shortestDistances(i);
			for(int j = 0; j < size; ++j) {
				//dont make a edge to node itself
				if(i != j)
					addEdge(i, j, shortest[j]);
			}
		}
	}
	
	//prints brute forced TSP tour
	public void printTravelingSalesman() {
		//setup
		int[] currentTour = new int[size];
		int[] bestTour = new int[size+1];
		bestTour[size] = Integer.MAX_VALUE;
		boolean[][] visitedEdges = new boolean[size][size];
		boolean[] visitedNodes = new boolean[size];
		//call brute force method
		tsp(visitedNodes, visitedEdges, bestTour, currentTour, 0, 0, 0);
		//format output
		if(bestTour[size] == Integer.MAX_VALUE)
			System.out.println("Error: Graph has no tour.");
		else {
			System.out.print(bestTour[size] + ": ");
			for(int i = 0; i < size; ++i)
				System.out.print(bestTour[i] + " -> ");
			System.out.println("0");
		}
	}
	
	//recursively try all the tours in the tree
	//puts the best tour into bestTour, bestTour[size] is the length of the tour
	private void tsp(boolean[] visitedNodes, boolean[][] visitedEdges, int[] bestTour, int[] currentTour, int node, int depth, int length) {
		//base case: if every node is visited
		if(depth == size) {
			//if the current tour is valid (ends up at 0) and is better than the current tour, update the best tour
			if(node == 0 && length < bestTour[size]) {
				for(int i = 0; i < size; ++i)
					bestTour[i] = currentTour[i];
				bestTour[size] = length;
			}
			return;
		}
		//add current node to tour
		currentTour[depth] = node;
		//loop through adjacent nodes
		for(int adj : adjacentNodes(node)) {
			//dont visit a node or a edge twice
			if(! visitedNodes[adj] && ! visitedEdges[node][adj]) {
				visitedNodes[adj] = true;
				visitedEdges[node][adj] = visitedEdges[adj][node] = true;
				tsp(visitedNodes, visitedEdges, bestTour, currentTour, adj, depth + 1, length + adjMatrix[node][adj]);
				visitedEdges[node][adj] = visitedEdges[adj][node] = false;
				visitedNodes[adj] = false;
			}
		}
	}
	
	//prints the approximate TSP tour
	public void printApproximateTSP() {
		Graph mst = minimumSpanningTree();
		boolean[] visited = new boolean[size];
		ArrayList<Integer> tour = new ArrayList<Integer>();
		preorder(mst, 0, visited, tour);
		//add a zero to the tour because it has to end up at the start again
		tour.add(0);
		//loop trough the list and calculate the length and prepare the output
		String s = "";
		int length = 0;
		for(int i = 0; i < size; ++i) {
			length += adjMatrix[tour.get(i)][tour.get(i+1)];
			s += tour.get(i) + " -> ";
		}
		System.out.println(length + ": " + s + "0");	
	}
	
	//puts a preorder traversal of the tree-graph into tour
	//also needs a visited array, so it does not recurse back to the parent node
	private void preorder(Graph mst, int root, boolean[] visited, List<Integer> tour) {
		//add current node
		visited[root] = true;
		tour.add(root);
		//loop through kids
		for(int adj : mst.adjacentNodes(root))
			//ignore parent node
			if(! visited[adj])
				preorder(mst, adj, visited, tour);

				
	}
 }
