import java.util.ArrayList;
import java.util.Arrays;

import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Pizza {

	Store store;
	int n = 4;
	int[] price = {10,5,20,15}; 
	int m = 2;
	int[] buy = {1,2};
	int[] free = {1,1};
	
	public static void main(String args[]) {

		Pizza pizza = new Pizza();

		pizza.search();

	}

	public void search() {
		
		Arrays.sort(price);	
		store = new Store();

		//Matrix creation
		IntVar[][] paid_matrix = new IntVar[m][n];	
		IntVar[][] free_matrix = new IntVar[m][n];
		for (int i = 0; i < m; i++) {
			for (int o = 0; o < n; o++) {
				paid_matrix[i][o] = new IntVar(store, "paid_m"+i+o, 0, 1);
				free_matrix[i][o] = new IntVar(store, "free_m"+i+o, 0, 1);
			}	
		}

		//Paid matrix coupon has to be equal to buy in order to be valid.

		IntVar[] buyIntVar = new IntVar[m];
		for(int i = 0; i < m; i++){
			buyIntVar[i] = new IntVar(store, "buy"+i, buy[i], buy[i]);
		}

		IntVar[] paid_coupon = new IntVar[n];
		for(int i = 0; i < m; i++) {
			for(int o = 0; o < n; o++) {
				paid_coupon[o] = paid_matrix[i][o];
			}
			store.impose(new SumInt(store, paid_coupon, "==", buyIntVar[i]));
		}


		//Total amount of pizzas bought must be equal to n.
		IntVar pizzaConstraint = new IntVar(store, "pizzaConstraint", 1, 1);

		IntVar[] pizzaCount = new IntVar[m*2];
		for (int i = 0; i < n; i++) {
			for(int o = 0; o < m; o++) {
				pizzaCount[o] = paid_matrix[o][i];
				pizzaCount[2*m-1-o] = free_matrix[o][i];
			}
			store.impose(new SumInt(store, pizzaCount, "==", pizzaConstraint));
		}

		//Free_matrix coupon has to be less or equal to free.
	
		IntVar[] freeIntVar = new IntVar[m];
		for(int i = 0; i < m; i++){
			freeIntVar[i] = new IntVar(store, "free"+i, free[i], free[i]);
		}	

		IntVar[] free_coupon = new IntVar[n];
		for(int i = 0; i < m; i++) {
			for(int o = 0; o < n; o++) {
				free_coupon[o] = free_matrix[i][o];
			}
			store.impose(new SumInt(store, free_coupon, "<=", freeIntVar[i]));
		}		

		//TOTAL PRICE
		int maxPrice = 0;
		for(int i = 0; i < price.length; i++) {
			maxPrice += price[i];	
		}

		IntVar totalCost = new IntVar(store, "totalCost", 1, maxPrice); 
		IntVar[] coupon = new IntVar[n];

		IntVar[] coupon_cost = new IntVar[m];
		for(int i = 0; i < m; i++) {
			coupon_cost[i] = new IntVar(store, "coupon_cost"+i, 1, maxPrice);
			for(int o = 0; o < n; o++){
				coupon[o] = paid_matrix[i][o];
			}
			store.impose(new SumWeight(coupon, price, coupon_cost[i]));
		}

		store.impose(new SumInt(store, coupon_cost, "==", totalCost));


		//Free must be less or equal to min(buy).
		/*
		IntVar[] paidWeighted = new IntVar[n];
		IntVar[] freeWeighted = new IntVar[n];
		IntVar[] tempPaidArray = new IntVar[n];
		IntVar[] tempFreeArray = new IntVar[n];
		
		IntVar minPrice = new IntVar(store, "minPrice", 1, maxPrice);

		for(int i = 0; i < n; i++) {
			paidWeighted[i] = new IntVar(store, "paidWeighted"+i, 0 , 10000);
			freeWeighted[i] = new IntVar(store, "freeWeighted"+i, 0 , 10000);
			tempPaidArray[i] = new IntVar(store, "tempPaidArray"+i, 0 , 10000);
			tempFreeArray[i] = new IntVar(store, "tempFreeArray"+i, 0 , 10000);
		}

		for(int i = 0; i < m; i++) {
			for(int o = 0; o < n; o++) {
				PrimitiveConstraint a = new XeqC(paid_matrix[i][o], 0);
				PrimitiveConstraint b = new XeqC(tempPaidArray[o], 100);
				PrimitiveConstraint c = new XeqC(tempPaidArray[o], 1);
				store.impose(new IfThenElse(a, b, c));
			}

			for(int y = 0; y < n; y++) {
				store.impose(new XmulCeqZ(tempPaidArray[y], price[y], paidWeighted[y]));
				store.impose(new XmulCeqZ(free_matrix[i][y], price[y], freeWeighted[y]));
			}
			store.impose(new Min(paidWeighted, minPrice));

			System.out.println(minPrice.toString());
			for(int u = 0; u < n; u++) {
				store.impose(new XlteqY(freeWeighted[u], minPrice));
			}
		}*/
		
		for(int i = 0; i < m; i++) {
			for(int o = 0; o < n; o++) {
				PrimitiveConstraint a = new XeqC(paid_matrix[i][o], 0);
				for(int u = o; u < n; u++) {
					store.impose(new IfThen(a, new XeqC(free_matrix[i][u], 0)));
				}
			}
		}	
		Search<IntVar> label = new DepthFirstSearch<IntVar>(); 
		SelectChoicePoint<IntVar> select =	
		new SimpleMatrixSelect<IntVar>(paid_matrix, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
		boolean result = label.labeling(store, select, totalCost);

		System.out.println (result);
		
		for(int i = 0; i < m; i++) {
			for(int o = 0; o < n; o++) {
				System.out.print(free_matrix[i][o].toString() + "	");
			}
			System.out.println();
		}

	}

}
