package txk190012;// Starter code for mincost flow

import txk190012.Graph.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;



// Inital flow should be max flow
public class MinCostFlow {
    Vertex source;
    Vertex sink;
    int excess[];
    int height[];
    int price[];
    HashMap<Edge, Integer> edgesFlow;
    HashMap<Edge, Integer> capacity;
    HashMap<Edge, Integer> costmap;
    HashMap<Edge, Integer> reducedcost;
    int supply[];
    int eps = Integer.MIN_VALUE;

    Graph g;
    List<Vertex> list;

    public MinCostFlow(Graph g, Vertex s, Vertex t, HashMap<Edge, Integer> capacity, HashMap<Edge, Integer> cost) {
        this.g = g;
        this.source = s;
        this.sink = t;
        this.capacity = capacity;
        this.costmap = cost;
        edgesFlow = new HashMap<>();
        reducedcost = new HashMap<>();
        list = new LinkedList<>();
        excess = new int[g.n];
        height = new int[g.n];
        price = new int[g.n];
        supply = new int[g.n];
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

    void buildResidualGraph(Graph g){
        int i=0;
        for(Edge e: g.getEdgeArray()){
            capacity.put(e, (capacity.getOrDefault(e,0) - edgesFlow.getOrDefault(e,0)));
            Edge enew = new Edge(e.toVertex(), e.fromVertex(),e.weight,i);
            i++;
            capacity.put(enew,edgesFlow.getOrDefault(e,0) );
            costmap.put(enew, -1* costmap.getOrDefault(enew,0) );
        }
    }

    //Using cost scaling algorithm to return cost of pushing max flow
    int costScalingMinCostFlow() {
        int res = 0;
        //intializeReducedCost();
        for(int key: costmap.values()){
            if(key > eps){
                eps = key;
            }
        }
        for(Vertex u:g) {
            int vi = u.getIndex();
            price[vi] = 0;
        }
        int maxFlow = preflowPush();
        System.out.println("Flow :"+maxFlow);
        //intializeReducedCost();


        buildResidualGraph(g);
        intializeReducedCost();
        for(Edge e: reducedcost.keySet()){
            //System.out.println("Reduced cost: "+reducedcost.getOrDefault(e,0));
            res += reducedcost.getOrDefault(e,0);

        }
        System.out.println("Reduced cost: "+res);

        supply[source.getIndex()] = maxFlow;
        supply[sink.getIndex()] = -1 * maxFlow;

        //eps = eps/2;

        while(eps > 0){
            refine();
            eps = eps/2;
        }

        res = 0;

        for(Edge e: reducedcost.keySet()){
            System.out.println("Reduced cost: "+reducedcost.getOrDefault(e,0));
            res += reducedcost.getOrDefault(e,0);
        }

        return res;
    }

    private void intializeReducedCost() {
        for(Edge e : g.getEdgeArray()){
            int value = cost(e);

            value += price[e.fromVertex().getIndex()];
            value -= price[e.toVertex().getIndex()];

            reducedcost.put(e,value );
        }
    }

    public void refine(){
        for(Edge e: g.getEdgeArray()){
            if(reducedcost.getOrDefault(e,0) < 0){
                edgesFlow.put(e,capacity.get(e));
            }
            else{
                edgesFlow.put(e, 0);
            }
        }
        for(Vertex u : g){
            excess[u.getIndex()] = supply[u.getIndex()];
            for(Edge e: edgesFlow.keySet()){
                if(e.fromVertex() == u){
                    excess[u.getIndex()] -= edgesFlow.getOrDefault(e,0);
                }
                else if(e.toVertex() == u){
                    excess[u.getIndex()] += edgesFlow.getOrDefault(e,0);
                }
            }
            if(excess[u.getIndex()] > 0){
                discharge_cost(u);
                intializeReducedCost();
            }
        }
    }

    private void discharge_cost(Vertex u) {
        discharge(u);
        while(excess[u.getIndex()] > 0) {
            for(Edge e: g.adj(u).outEdges) {
                if(reducedcost.get(e) < 0){
                    push(e, e.fromVertex(), e.toVertex());
                }
            }
            if(excess[u.getIndex()] > 0){
                relabel_cost(u);
                //intializeReducedCost();
            }
        }
    }

    private void relabel_cost(Vertex u) {
        relabel(u);
        price[u.getIndex()] = price[u.getIndex()] - eps;
        //intializeReducedCost();
    }

    // flow going through edge e
    public int flow(Edge e) {
        return edgesFlow.containsKey(e) ? edgesFlow.get(e) : 0;
    }

    // capacity of edge e
    public int capacity(Edge e) {
        return capacity.containsKey(e) ? capacity.get(e): 0;
    }

    // cost of edge e
    public int cost(Edge e) {
        return costmap.containsKey(e)?capacity.get(e):0;
    }


    public static void main(String[] args) throws Exception {
        Scanner in;
        if (args.length > 0 && !args[0].equals("-")) {
            File file = new File(args[0]);
            in = new java.util.Scanner(file);
        } else {
            in = new Scanner(System.in);
        }

        Graph g = Graph.readDirectedGraph(in);

        int s = in.nextInt();
        int t = in.nextInt();
        HashMap<Edge,Integer> capacity = new HashMap<>();
        HashMap<Edge,Integer> cost = new HashMap<>();
        int[] arr = new int[1 + g.edgeSize()];
        for(int i=1; i<=g.edgeSize(); i++) {
            arr[i] = 1;   // default capacity
        }
        while(in.hasNextInt()) {
            int i = in.nextInt();
            int cap = in.nextInt();
            arr[i] = cap;
        }

        Vertex src = g.getVertex(s);
        Vertex target = g.getVertex(t);

        for(Vertex u: g) {
            for(Edge e: g.outEdges(u)) {
                capacity.put(e, arr[e.getName()]);
                cost.put(e, e.getWeight());
                //System.out.println("Capacity: "+capacity.getOrDefault(e,0));
                //System.out.println("Cost: "+cost.getOrDefault(e,0));
            }
        }


        Timer timer = new Timer();
        MinCostFlow mcf = new MinCostFlow(g, src, target, capacity, cost);

        int result = mcf.costScalingMinCostFlow();

        System.out.println(result);


         /*   for(Vertex u: g) {
                System.out.print(u + " : ");
                for(Edge e: g.outEdges(u)) {
                    if(mcf.flow(e) != 0) { System.out.print(e + ":" + mcf.flow(e) + "/" + mcf.capacity(e) + "@" + mcf.cost(e) + "| "); }
                }
                System.out.println();
            } */


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

        public Timer end() {
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
}
