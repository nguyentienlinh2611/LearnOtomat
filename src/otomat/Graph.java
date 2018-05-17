package otomat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph<T> {
	private ArrayList<ArrayList<T>> adj;
	private int numVertex;
	private int numEdge;

	public Graph() {
		adj = new ArrayList<ArrayList<T>>();
		numEdge = 0;
		numVertex = 0;
	}

	public int getNumVertex() {
		return numVertex;
	}

	public int getNumEdge() {
		return numEdge;
	}

	public ArrayList<T> getVertex(int index) {
		return adj.get(index);
	}

	public int getVertexOfEdge(int src, T weight) {
		for (int j = 0; j < numVertex; j++) {
			if (adj.get(src).get(j) != null) {
				if (adj.get(src).get(j).equals(weight)) {
					return j;
				}
			}
		}
		return -1;
	}

	public Map<Integer,T> getDestVertexFromVertex(int src) {
		Map<Integer,T> dest = new HashMap<Integer,T>();
		for (int j = 0; j < numVertex; j++) {
			if (adj.get(src).get(j) != null) {
				dest.put(j,getEdge(src, j));
			}
		}
		return dest;
	}

	public T getEdge(int src, int dest) {
		return adj.get(src).get(dest);
	}

	public void addVertex() {
		for (int i = 0; i < numVertex; i++) {
			adj.get(i).add(null);
		}
		numVertex++;
		ArrayList<T> list = new ArrayList<T>();
		for (int i = 0; i < numVertex; i++) {
			list.add(null);
		}
		adj.add(list);
	}

	public void removeVertex(int vertex) {
		if (vertex <= numVertex) {
			for (int i = 0; i < numVertex; i++) {
				adj.get(i).remove(vertex);
			}
			adj.remove(vertex);
			numVertex--;
		}
	}

	public void addEdge(int src, int dest, T weight) {
		while (src >= numVertex) {
			addVertex();
		}
		while (dest >= numVertex) {
			addVertex();
		}
		adj.get(src).set(dest, weight);
		numEdge++;
	}

	public void removeEdge(int src, int dest) {
		adj.get(src).set(dest, null);
		numEdge--;
	}

	public void combineVertex(int v1, int v2) {
		ArrayList<T> vertex = this.getVertex(v2);
		for (int i = 0; i < vertex.size(); i++) {
			T t = vertex.get(i);
			if (t != null) {
				adj.get(v1).set(i, t);
			}
		}
		for (int i = 0; i < vertex.size(); i++) {
			Map<Integer,T> dest = this.getDestVertexFromVertex(i);
			if (dest.containsKey(v2)) {
				adj.get(i).set(v1,dest.get(v2));
			}
		}
	}

	public void printGraph() {
		for (int i = 0; i < numVertex; i++) {
			System.out.println("Adjacency list of vertex: " + i);
			for (int j = 0; j < numVertex; j++) {
				if (adj.get(i).get(j) != null) {
					System.out.print("vertex: " + j + ", weight: " + adj.get(i).get(j).toString() + "\t");
				}
			}
			System.out.println();
		}
	}

}
