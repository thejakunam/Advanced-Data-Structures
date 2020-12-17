// Driver for Postman Tour
package txk190012;
import txk190012.Graph.*;

import java.util.List;
import java.util.Scanner;

// Find a minimum weight postman tour that goes through every edge of g at least once

public class PostmanDriver {
    static int VERBOSE = 0;
    public static void main(String[] args) throws Exception {
	Scanner in = new Scanner(System.in);
	Graph g = Graph.readDirectedGraph(in);
	if(args.length > 0) { VERBOSE = Integer.parseInt(args[0]); }
	
	Timer timer = new Timer();
	Postman p = new Postman(g);
	long result = p.postmanTour();
	System.out.println(result);
	System.out.println(timer.end());
	if(VERBOSE > 0) {
	    List<Edge> tour = p.getTour();
	    if(tour != null) {
		for(Edge e: tour) { System.out.print(e); }
		System.out.println();
	    }
	}
    }
}
