import java.util.ArrayList;
import java.util.Arrays;

import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Logistics {

	Store store;
/*
	int graph_size = 6;
	int start = 1;
	int n_dests = 1;
	int[] dest = {6};
	int n_edges = 7;
	int[] from = {1,1,2,2,3,4,4}; 
	int[] to = {2,3,3,4,5,5,6}; 
	int[] cost = {4,2,5,10,3,4,11};

	int graph_size = 6;
	int start = 1;
	int n_dests = 2;
	int[] dest = {5,6};
	int n_edges = 7;
	int[] from = {1,1,2, 2,3,4, 4}; 
	int[] to = {2,3,3, 4,5,5, 6}; 
	int[] cost = {4,2,5,10,3,4,11};
*/
	int graph_size = 6;
	int start = 1;
	int n_dests = 2;
	int[] dest = {5,6};
	int n_edges = 9;
	int[] from = {1,1,1,2,2,3,3,3,4}; 
	int[] to = {2,3,4,3,5,4,5,6,6}; 
	int[] cost = {6,1,5,5,3,5,6,4,2};

	public static void main(String args[]) {

		Logistics log = new Logistics();

		log.search();

	}

	public void search() {

		store = new Store();

		IntVar[][] cities = new IntVar[n_dests][graph_size];
		
		//loop through destinations
		for(int x = 0; x < n_dests; x++) {
			//loop through cities and add themselves to the domain,  except dest and start
			for(int i = 0; i < graph_size; i++) {
				if(i == start - 1 || i == dest[x] - 1)
					cities[x][i] = new IntVar(store, "city"+(i+1));
				else 
					cities[x][i] = new IntVar(store, "city"+(i+1), i+1, i+1);
			}
			//loop through cities and add all connections.
			for(int i = 0; i < n_edges; i++) {
				cities[x][from[i]-1].addDom(to[i], to[i]);	
				cities[x][to[i]-1].addDom(from[i], from[i]);
			}

			//add connection from dest to start
			cities[x][dest[x]-1].addDom(start, start);
			
			//Create subcircuit for this dest
			IntVar[] circuit = new IntVar[graph_size];
			for(int i = 0; i < graph_size; i++) {
				circuit[i] = cities[x][i];
			}

			Constraint ctr = new Subcircuit(circuit);
			store.impose(ctr);
		}

		IntVar[] edgesUsed = new IntVar[n_edges];
		for(int i = 0; i < n_edges; i++) {
			edgesUsed[i] = new IntVar(store, "edgeUsed"+i, 0, 1);	
		}

		//for each destination loop through each city, check edge and then weight the cost.
		for(int x = 0; x < n_dests; x++) {
			for(int i = 0; i < graph_size; i++) {
				for(int j = 0; j < n_edges; j++) {
					if(from[j] == i + 1) {
						PrimitiveConstraint a = new XeqC(cities[x][i], to[j]);
						PrimitiveConstraint b = new XeqC(edgesUsed[j], 1);
						store.impose(new IfThen(a, b));
					}
					if(to[j] == i + 1) {
						PrimitiveConstraint a = new XeqC(cities[x][i], from[j]);
						PrimitiveConstraint b = new XeqC(edgesUsed[j], 1);
						store.impose(new IfThen(a, b));
					}

				}	
			}
		}

		IntVar totalCost = new IntVar(store, "totalCost", 0, 100000);
		store.impose(new SumWeight(edgesUsed, cost, totalCost));

		Search<IntVar> label = new DepthFirstSearch<IntVar>(); 
		SelectChoicePoint<IntVar> select =	
			new SimpleMatrixSelect<IntVar>(cities, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
		boolean result = label.labeling(store, select, totalCost);
	}

}
