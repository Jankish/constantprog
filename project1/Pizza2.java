import java.util.ArrayList;
import java.util.Arrays;

import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Pizza2 {

	Store store;

	int n = 4;
	int[] price = {10,5,20,15}; 
	int m = 2;
	int[] buy = {1,2};
	int[] free = {1,1};
	/*
	int n = 4;
	int[] price = {10,15,20,15};
	int m = 7;
	int[] buy = {1,2,2,8,3,1,4};
	int[] free = {1,1,2,9,1,0,1};

	int n = 10;
	int[] price = {70,10,60,60,30,100,60,40,60,20}; 
	int m = 4;
	int[] buy = {1,2,1,1}; 
	int[] free = {1,1,1,0};
*/
	public static void main(String args[]) {

		Pizza2 pizza = new Pizza2();

		pizza.search();

	}

	public void search() {
		
		Arrays.sort(price);	
		store = new Store();

		//Matrix creation
		IntVar[][] matrix = new IntVar[m*2+1][n];	

		for (int i = 0; i < m*2+1; i++) {
			for (int o = 0; o < n; o++) {
				matrix[i][o] = new IntVar(store, "matrix"+i+o, 0, 1);
			}	
		}

		//Free must be less or equal to min(buy).
		
		for(int i = 0; i < m; i++) {
			for(int o = 0; o < n; o++) {
				PrimitiveConstraint a = new XeqC(matrix[i][o], 1);
				for(int u = o; u < n; u++) {
					store.impose(new IfThen(a, new XeqC(matrix[i+m][u], 0)));
				}
			}
		}	

		//Paid matrix coupon has to be equal to buy in order to be valid.

		IntVar[] buyIntVar = new IntVar[m];
		IntVar[] freeIntVar = new IntVar[m];

		for(int i = 0; i < m; i++){
			buyIntVar[i] = new IntVar(store, "buy"+i, buy[i], buy[i]); 
			buyIntVar[i].addDom(0, 0);
			freeIntVar[i] = new IntVar(store, "free"+i, 0, free[i]);
		}

		IntVar[] paid_coupon = new IntVar[n];
		IntVar[] free_coupon = new IntVar[n];
		for(int i = 0; i < m; i++) {
			for(int o = 0; o < n; o++) {
				paid_coupon[o] = matrix[i][o];
				free_coupon[o] = matrix[i+m][o];
			}
			store.impose(new SumInt(store, paid_coupon, "==", buyIntVar[i]));	
			store.impose(new SumInt(store, free_coupon, "==", freeIntVar[i]));	
			PrimitiveConstraint a = new XeqC(buyIntVar[i], buy[i]);
			PrimitiveConstraint b = new XlteqC(freeIntVar[i], free[i]);
			PrimitiveConstraint c = new XeqC(freeIntVar[i], 0);
			store.impose(new IfThenElse(a, b, c));
		}

		//Total amount of pizzas bought must be equal to n.
		IntVar pizzaConstraint = new IntVar(store, "pizzaConstraint", 1, 1);

		IntVar[] pizzaCount = new IntVar[m*2+1];
		for (int i = 0; i < n; i++) {
			for(int o = 0; o < m*2+1; o++) {
				pizzaCount[o] = matrix[o][i];
			}
			store.impose(new SumInt(store, pizzaCount, "==", pizzaConstraint));
		}

		//TOTAL PRICE
		int maxPrice = 0;
		for(int i = 0; i < price.length; i++) {
			maxPrice += price[i];	
		}

		IntVar totalCost = new IntVar(store, "totalCost", 0, maxPrice); 
		IntVar[] coupon = new IntVar[n];

		IntVar[] coupon_cost = new IntVar[m+1];
		for(int i = 0; i < m; i++) {
			coupon_cost[i] = new IntVar(store, "coupon_cost"+i, 0, maxPrice);
			for(int o = 0; o < n; o++){
				coupon[o] = matrix[i][o];
			}
			store.impose(new SumWeight(coupon, price, coupon_cost[i]));
		}
		
		int p = m*2;
		coupon_cost[m] = new IntVar(store, "coupon_cost"+m, 0, maxPrice);
		for(int i = 0; i < n; i++) {
			coupon[i] = matrix[p][i];
		}
		store.impose(new SumWeight(coupon, price, coupon_cost[m]));
	
		store.impose(new SumInt(store, coupon_cost, "==", totalCost));

		//Search
		Search<IntVar> label = new DepthFirstSearch<IntVar>(); 
		SelectChoicePoint<IntVar> select =	
		new SimpleMatrixSelect<IntVar>(matrix, new SmallestMin<IntVar>(), new IndomainMin<IntVar>());
		boolean result = label.labeling(store, select, totalCost);


		for(int i = 0; i < m*2+1; i++) {
			for(int o = 0; o < n; o++) {
				System.out.print(matrix[i][o]+"\t");
			}
			System.out.println();
		}	

	}

}
