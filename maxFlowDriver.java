// Driver code for max flow
package txk190012;
import txk190012.Graph;
import txk190012.Graph.Edge;
import txk190012.Graph.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import txk190012.Flow;


public class maxFlowDriver {


    public static void main(String[] args) throws FileNotFoundException {

        Scanner in;
        if (args.length > 0 && !args[0].equals("-")) {
            File file = new File(args[0]);
            in = new Scanner(file);
        } else {
            in = new Scanner(System.in);
        }
        Graph g = Graph.readDirectedGraph(in);
        Timer timer = new Timer();
        int s = in.nextInt();
        int t = in.nextInt();
        HashMap<Edge, Integer> capacity = new HashMap<>();
        int[] arr = new int[1 + g.edgeSize()];
        for (int i = 1; i <= g.edgeSize(); i++) {
            arr[i] = 1;   // default capacity
        }
        while (in.hasNextInt()) {
            int i = in.nextInt();
            int cap = in.nextInt();
            arr[i] = cap;
        }
        for (Vertex u : g) {
            for (Edge e : g.outEdges(u)) {
                capacity.put(e, arr[e.getName()]);
            }
        }

        Flow f = new Flow(g, g.getVertex(s), g.getVertex(t), capacity);
        //f.setVerbose(VERBOSE);
        int value = f.preflowPush();


        System.out.println(value);


            for (Vertex u : g) {
                System.out.print(u + " : ");
				for(Edge e: g.outEdges(u)) {
                    System.out.print(e + ":" + f.flow(e) + "/" + f.capacity(e) + " | ");
                }
                System.out.println();
            }
            System.out.println("Min cut: S = " + f.minCutS());
            System.out.println("Min cut: T = " + f.minCutT());


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
	
