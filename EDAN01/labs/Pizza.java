import java.util.ArrayList;

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

		store = new Store();

		IntVar[] paid = IntVar[n];
		IntVar[] bonus = IntVar[n];

		for(int i = 0; i < n; i++) {
			paid[i] = new IntVar(store, "paid"+i, 0, 1);
			bonus[i] = new IntVar(store, "bonus"+i, 0, 1);
			IntVar[] v = {paid[i], free[i]};

			IntVar sum = new IntVar(store, "sum", 1, 1);
			store.impose(new SumInt(v, "==", sum));
		}


		IntVar[] coupons = IntVar[m];
		for(int i = 0; i < m ; i++) {
			coupons[i] = new IntVar(store, "coupon"+i, buy[i], buy[i]);
			store.impose(new SumInt(paid, ">=", coupons[i]));
		}

		IntVar[][] coup_matrix = IntVar[m][m];	
		for (int i = 0; i < m; i++) {
			
		}


	}
