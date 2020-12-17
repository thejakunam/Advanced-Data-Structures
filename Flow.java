package txk190012;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import txk190012.Graph;
import txk190012.Graph.Vertex;
import txk190012.Graph.Edge;
import java.util.HashMap;
import java.util.Iterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Flow {
    Vertex source;
    Vertex sink;
    int excess[];
    int height[];
    HashMap<Edge, Integer> edgesFlow;
    HashMap<Edge, Integer> capacity;
    Graph g;
    List<Vertex> list;


    /**
     * Creating source, sink, graph and the capacity of edges
     */
    public Flow(Graph g, Vertex s, Vertex t, HashMap<Edge, Integer> capacity) {
        this.g = g;
        this.source = s;
        this.sink = t;
        this.capacity = capacity;
        edgesFlow = new HashMap<>();
        list = new LinkedList<>();
        excess = new int[g.n];
        height = new int[g.n];
    }


    /**
     *
     * @return max flow from source to sink
     */
    public int preflowPush() {
        initialize();

        Iterator<Vertex> iter;
        boolean finish = false;

        while(!finish) {
            finish = true;

            iter = list.iterator();
            while(iter.hasNext()) {
                Vertex curr = iter.next();
                if(excess[curr.getIndex()] == 0)
                    continue;
                int oldh = height[curr.getIndex()];
                discharge(curr);
                if(oldh != height[curr.getIndex()]) {
                    finish = false;
                    iter.remove();
                    list.add(0, curr);
                    break;
                }
            }
        }
        return excess[sink.getIndex()];
    }

    /**
     * Method initializes flow, capacities, excess and height for the graph
     */
    private void initialize() {
        for(Vertex u:g) {
            if(!source.equals(u) && !sink.equals(u))
                list.add(u);

            for(Edge e: g.adj(u).outEdges)
                edgesFlow.put(e, 0);

            int vi = u.getIndex();
            excess[vi] = 0;
            height[vi] = 0;
        }

        height[source.getIndex()] = g.n;
        for(Edge e: g.adj(source).outEdges) {
            int edgecap = capacity(e);
            edgesFlow.put(e, edgecap);

            excess[source.getIndex()] -= edgecap;
            excess[e.otherEnd(source).getIndex()] += edgecap;
        }
    }

    /**
     * The method discharges the excess flow
     * @param u
     * @return void
     *
     */
    private void discharge(Vertex u) {
        while(excess[u.getIndex()] > 0) {

            for(Edge e : g.adj(u).outEdges) {
                if(residual(e, u)) {
                    Vertex otherV = e.otherEnd(u);
                    if (height[otherV.getIndex()] + 1 == height[u.getIndex()]) {
                        push(e, u, otherV);
                        if (excess[u.getIndex()] == 0)
                            return;
                    }
                }
            }

            for(Edge e : g.adj(u).inEdges) {
                if(residual(e, u)) {
                    Vertex otherV = e.fromVertex();
                    if (height[otherV.getIndex()] + 1 == height[u.getIndex()]) {
                        push(e, u, otherV);
                        if (excess[u.getIndex()] == 0)
                            return;
                    }
                }
            }

            relabel(u);
        }
    }



    /**
     * Change the height of vertex so that it can push excess flow
     * @param u
     * @retutn void
     */
    private void relabel(Vertex u) {
        int minHeight = 3*g.n;
        for(Edge e: g.adj(u).outEdges) {
            int mheight = height[e.toVertex().getIndex()];
            if(residual(e,u) && mheight < minHeight){
                minHeight = mheight;
            }
        }


        for(Edge e: g.adj(u).inEdges) {
            int mheight = height[e.fromVertex().getIndex()];
            if(residual(e,u) && mheight < minHeight) {
                minHeight = mheight;
            }
        }
        height[u.getIndex()] = minHeight+1;
    }

    /**
     * Method returns if there is an edge which is reversed in residual graph
     * as compared to original graph
     * @param e,u
     * @return excess flow is greater than 0 or not
     */
    private boolean residual(Edge e, Vertex u) {
        if(e.fromVertex().equals(u)) {
            return flow(e) < capacity(e);
        }
        return flow(e) > 0;
    }

    /**
     * Method pushes the excess flow out from vertex and make it 0
     * @param e,u,v
     * @return void
     */
    private void push(Edge e, Vertex u, Vertex v) {
        int flowDelta = 0;
        if(e.fromVertex().equals(u)) {
            flowDelta = Math.min((capacity(e) - flow(e)), excess[u.getIndex()]);
            edgesFlow.put(e, flow(e) + flowDelta);
        }
        else {
            flowDelta = Math.min(flow(e), excess[u.getIndex()]);
            edgesFlow.put(e, flow(e) - flowDelta);
        }
        excess[u.getIndex()] -= flowDelta;
        excess[v.getIndex()] += flowDelta;
    }

    /**
     *flow through edge e
     */
    public int flow(Edge e) {
        return edgesFlow.containsKey(e) ? edgesFlow.get(e) : 0;
    }

    /**
     * Method returns the capacity of edge
     */
    public int capacity(Edge e) {
        return capacity.containsKey(e) ? capacity.get(e): 0;
    }

    /* After edgesFlow has been computed, this method can be called to
       get the "S"-side of the min-cut found by the algorithm
    */
    public Set<Vertex> minCutS() {
        return null;
    }

    /* After edgesFlow has been computed, this method can be called to
       get the "T"-side of the min-cut found by the algorithm
    */
    public Set<Vertex> minCutT() {
        return null;
    }
/*
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        if (args.length > 0 && !args[0].equals("-")) {
            File file = new File(args[0]);
            in = new java.util.Scanner(file);
        } else {
            in = new Scanner(System.in);
        }
        boolean VERBOSE = false;
        if (args.length > 1) { VERBOSE = Boolean.parseBoolean(args[1]); }
        Graph g = Graph.readDirectedGraph(in);
        Flow.Timer timer = new Flow.Timer();
        int s = in.nextInt();
        int t = in.nextInt();
        System.out.println("Source:"+s);
        System.out.println("Sink:"+t);
        HashMap<Edge, Integer> capacity = new HashMap<>();
        int[] arr = new int[1 + g.edgeSize()];
        for (int i = 1; i <= g.edgeSize(); i++) {
            arr[i] = 1;
        }
        while (in.hasNextInt()) {
            int i = in.nextInt();
            int cap = in.nextInt();
            arr[i] = cap;
            System.out.println(i+"    vertex      "+ arr[i]+"    capacity");
        }
        for (Vertex u : g) {
            for (Edge e : g.outEdges(u)) {
                capacity.put(e, arr[e.getName()]);
            }
        }



        Flow f = new Flow(g, g.getVertex(s), g.getVertex(t), capacity);
        //f.setVerbose(VERBOSE);

        int value = f.preflowPush();

        System.out.println("Max flow computed is:");
        System.out.println(value);

        /*if (VERBOSE) {
            for (Vertex u : g) {
                System.out.print(u + " : ");
                for(Edge e: g.outEdges(u)) {
                    System.out.print(e + ":" + f.flow(e) + "/" + f.capacity(e) + " | ");
                }
                System.out.println();
            }
            System.out.println("Min cut: S = " + f.minCutS());
            System.out.println("Min cut: T = " + f.minCutT());
        }*/
/*
        System.out.println(timer.end());
    }
    static public class Timer {
        long startTime, endTime, elapsedTime, memAvailable, memUsed;
        boolean ready;

        public Timer() {
            startTime = System.currentTimeMillis();
            ready = false;
        }

        public void start() {
            startTime = System.currentTimeMillis();
            ready = false;
        }

        public Flow.Timer end() {
            endTime = System.currentTimeMillis();
            elapsedTime = endTime-startTime;
            memAvailable = Runtime.getRuntime().totalMemory();
            memUsed = memAvailable - Runtime.getRuntime().freeMemory();
            ready = true;
            return this;
        }

        public long duration() { if(!ready) { end(); }  return elapsedTime; }

        public long memory()   { if(!ready) { end(); }  return memUsed; }

        public String toString() {
            if(!ready) { end(); }
            return "Time: " + elapsedTime + " msec.\n" + "Memory: " + (memUsed/1048576) + " MB / " + (memAvailable/1048576) + " MB.";
        }

    }
    */
}
