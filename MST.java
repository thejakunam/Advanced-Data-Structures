// Starter code for SP9
/**
 * @Author  Shen Zhang          sxz162330
 *        Theja Shree Kunam   txk190012
 */

package txk190012;

import txk190012.Graph.*;
import txk190012.BinaryHeap.*;
import txk190012.Graph.Timer;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * Minimum Spanning Tree implementation
 */
public class MST extends GraphAlgorithm<MST.MSTVertex> {
	String algorithm;
	public long wmst;		//weight of minimum spanning tree
	List<Edge> mst;			// list of safe edges

	MST(Graph g) {
		super(g, new MSTVertex((Vertex) null));
		mst = new LinkedList<>();
	}

	/**
	 * Implementation of vertex in minimum spanning tree
	 */
	public static class MSTVertex implements Index, Comparable<MSTVertex>, Factory {
		boolean seen;			//to keep track of vertices that are visited
		MSTVertex parent;
		int priority;
		Vertex vertex;
		int index;
		int comp;			//to keep track of component to which vertex belongs

		MSTVertex(Vertex u) {
			seen = false;
			parent = null;
			priority = Integer.MAX_VALUE;
			vertex = u;
			comp = 0;
		}

		MSTVertex(MSTVertex u) {  // for prim2
			this.seen = u.seen;
			this.parent = u.parent;
			this.priority = u.priority;
			this.vertex = u.vertex;
			this.comp = u.comp;
		}

		public MSTVertex make(Vertex u) {
			return new MSTVertex(u);
		}

		@Override
		public void putIndex(int index) {
			this.index = index;
		}

		@Override
		public int getIndex() {
			return this.index;
		}

		@Override
		public int compareTo(MSTVertex other) {
			if (other == null || this.priority > other.priority) return 1;
			else if (this.priority < other.priority) return -1;
			else return 0;
		}
	}

	public long kruskal() {
		algorithm = "Kruskal";
		Edge[] edgeArray = g.getEdgeArray();
		mst = new LinkedList<>();
		wmst = 0;
		return wmst;
	}

	/**
	 * method to label vertices according to the component that they belong to
	 * @param s
	 * @param count
	 * @param F
	 */
	void label(Vertex s, int count, MST F) {
		Queue<Vertex> q = new LinkedList<>();
		q.add(s);

		while (!q.isEmpty()) {
			Vertex v = q.remove();
			if (!F.get(v).seen) {
				F.get(v).seen = true;
				F.get(v).comp = count;

				for (Edge e :  F.g.adj(v).outEdges) {
					if(e.fromVertex().equals(v)) {

						q.add(e.toVertex());

					} else {

						q.add(e.fromVertex());
					}
				}
			}
		}
	}

	/**
	 * Helper method to count the components in graph
	 * @param F
	 * @return
	 */
	public int countAndLabel(MST F) {
		int count = 0;

		for (Vertex vert : F.g.getVertexArray()) {
			F.get(vert).seen = false;
		}
		for (Vertex u : F.g.getVertexArray()) {
			if (!F.get(u).seen) {
				count++;
				label(u, count,F);
			}
		}
		return count;
	}

	/**
	 * helper method to add safe edges to the graph and also calculates weight of
	 * minimum spanning tree by adding weights of safe edges
	 * @param edgeArr
	 * @param F
	 * @param count
	 */
	void addSafeEdges(Edge[] edgeArr, MST F, int count) {
		Edge[] safe = new Edge[edgeArr.length];

		for (int i = 0; i < count; i++) {
			safe[i] = null;
		}

		for (Edge e : edgeArr) {
			MSTVertex ru = F.get(e.fromVertex());
			MSTVertex rv = F.get(e.toVertex());
			//System.out.println("U comp\t"+ru.comp+"\tV comp\t"+rv.comp);

			if (ru.comp != rv.comp) {
				if(safe[ru.comp-1] == null || e.compareTo(safe[ru.comp-1]) < 0) {
					safe[ru.comp-1] = e;
				}
				if(safe[rv.comp-1] == null || e.compareTo(safe[rv.comp-1]) < 0) {
					safe[rv.comp-1] = e;
				}
			}
		}
		for(int i = 0; i < count; i++) {

			if(!this.mst.contains(safe[i])) {

				F.g.addEdge(safe[i].fromVertex(), safe[i].toVertex(), safe[i].getWeight(), safe[i].getName());
				this.mst.add(safe[i]);
				this.wmst += safe[i].getWeight();

			}
		}

	}

	/**
	 * method to implement Boruvkas algorithm to find weight of minimum spanning tree
	 * @return
	 */
	public long boruvka() {
		algorithm = "Boruvka";
		//Create a graph F = [V,null] from G = [V,E]
		int size = this.g.n;
		MST F = new MST(new Graph(size));
		int count = countAndLabel(F);
		//System.out.println("Initial Count\t" + count);

		while (count > 1) {
			addSafeEdges(this.g.getEdgeArray(), F, count);
			count = countAndLabel(F);
			//System.out.println("After Count\t"+count);
		}
		return this.wmst;
	}

	/**
	 * Initialization for Prim's 2.
	 * Set all vertices as unseen, priority as infinity.
	 * Source vertex's priority as 0.
	 *
	 * @param src Source vertex
	 */
	public void initialize(Vertex src) {
		for (Vertex u : g) {
			get(u).seen = false;
			get(u).parent = null;
			get(u).priority = Integer.MAX_VALUE;
		}
		get(src).priority = 0;
		get(src).seen = true;
	}

	/**
	 * Method for Prim's 2
	 *
	 * @param s Source vertex
	 * @return Weight of MST as long number
	 */
	public long prim2(Vertex s) {
		algorithm = "indexed heaps";
		mst = new LinkedList<>();
		wmst = 0;

		initialize(s);

		IndexedHeap<MSTVertex> q = new IndexedHeap<>(g.size());
		for (Vertex u : g) {
			q.add(get(u));
		}

		while (!q.isEmpty()) {
			MSTVertex u = q.remove();
			u.seen = true;
			wmst += u.priority;
			for (Edge e : g.incident(u.vertex)) {
				MSTVertex v = get(e.otherEnd(u.vertex));
				if (!v.seen && e.getWeight() < v.priority) {
					v.priority = e.getWeight();
					v.parent = u;
					q.decreaseKey(v);
				}
			}
		}

		return wmst;
	}

	public long prim1(Vertex s) {
		algorithm = "PriorityQueue<Edge>";
		mst = new LinkedList<>();
		wmst = 0;
		PriorityQueue<Edge> q = new PriorityQueue<>();
		return wmst;
	}

	public static MST mst(Graph g, Vertex s, int choice) {
		MST m = new MST(g);
		switch (choice) {
			case 0:
				m.boruvka();
				break;
			case 1:
				m.prim1(s);
				break;
			case 2:
				m.prim2(s);
				break;
			case 3:
				m.kruskal();
				break;
			default:

				break;
		}
		return m;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in;
		int choice = 0;  // prim2
		String string = "7 12  1 2 4  1 3 26   1 4 14  2 4 12  3 4 30  2 5 18  3 6 16  4 5 2  4 6 3  5 6 10  5 7 8  6 7 5";
		if (args.length == 0 || args[0].equals("-")) {
			in = new Scanner(string);
		} else {
			File inputFile = new File(args[0]);
			in = new Scanner(inputFile);
		}

		if (args.length > 1) {
			choice = Integer.parseInt(args[1]);
		}


		Graph g = Graph.readGraph(in);
		Vertex s = g.getVertex(1);

		Timer timer = new Timer();
		MST m = mst(g, s, choice);
		System.out.println(m.algorithm + "\n" + m.wmst);
		System.out.println(timer.end());
	}
}
