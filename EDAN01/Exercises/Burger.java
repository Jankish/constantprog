import java.util.ArrayList;

import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Burger  {

    Store store;

    public static void main(String args[]) {

	Burger burger = new Burger();
		
	burger.model();		

    }
	
	
    /**
     * 1. Every CP program consists of two parts. The first one is a model and
     * the second one is the specification of the search. 
     * This creates a model which uses global constraints to provide consize modeling. 
     * The model consists of variables and constraints. 
     */

    public void model() {
		
	ArrayList<IntVar> vars = new ArrayList<IntVar>();

	/**
	 * A constraint store can be considered as a manager of constraints 
	 * and variables. It is always required to have a constraint store 
	 * in order to create a CP model of the problem. 
	 */
	store = new Store();

	/**
	 * First, we need to specify the variables. In this problem we 
	 * will for certain have variables representing the value of different
	 * letters.
	 */

	/**
	 * We create variables. Each variable is created in a given 
	 * constraint store. We provide information about variable name. 
	 * However, the most part part of the specification is about the initial
	 * domain. The initial domain is specified to be in between 0 and 9. 
	 * 
	 * One important feature of CP is that a variable must take a value from
	 * the initial domain. The actual value of the variable can not be outside
	 * the initial domain. In some sense, the initial domain is also like a
	 * constraint.
	 * 
	 *  A variable is an integer variable, so eventually it will have an integer
	 *  value.
	 *  
	 *  FDV - Finite Domain Variable.
	 */

	IntVar beef = new IntVar(store, "BEEF", 1, 5);
	IntVar bun = new IntVar(store, "BUN", 1, 5);
	IntVar cheese = new IntVar(store, "CHEESE", 1, 5);
	IntVar onions = new IntVar(store, "ONIONS", 1, 5);
	IntVar pickles = new IntVar(store, "PICKLES", 1, 5);
	IntVar lettuce = new IntVar(store, "LETTUCE", 1, 5);
	IntVar ketchup = new IntVar(store, "KETCHUP", 1, 5);
	IntVar tomatos = new IntVar(store, "TOMATOS", 1, 5);

	// Creating arrays for FDVs
	IntVar ingredients[] = { beef, bun, cheese, onions, pickles, lettuce, ketchup, tomatos };

	for (IntVar v : ingredients)
	    vars.add(v);
			
	int[] cost = { 25, 15, 10, 9, 3, 4, 2, 4 };
	int[] sodium = { 50, 330, 310, 1, 260, 3, 160, 3 };
	int[] fat = { 17, 9, 6, 2, 0, 0, 0, 0 };
	int[] calories = { 220, 260, 70, 10, 5, 4, 20, 9 };

	/**
	 * We create auxilary variables in order to make it easier to express some of the
	 * constraints. Each auxilary variable holds a value for a given word.
	 */
	IntVar totalSODIUM = new IntVar(store, "v(SODIUM)", 1, 3000);
	IntVar totalFAT = new IntVar(store, "v(FAT)", 0, 150);
	IntVar totalCALORIES = new IntVar(store, "v(CALORIES)", 4, 3000);
	IntVar totalCOST= new IntVar(store, "v(COST)", 0, 3000);

	store.impose(new SumWeight(ingredients, cost, totalCOST));
	store.impose(new SumWeight(ingredients, sodium, totalSODIUM));
	store.impose(new SumWeight(ingredients, fat, totalFAT));
	store.impose(new SumWeight(ingredients, calories, totalCALORIES));

	store.impose(new XeqY(ketchup, lettuce));
	store.impose(new XeqY(pickles, tomatos));
			
	Search<IntVar> label = new DepthFirstSearch<IntVar>(); 
	SelectChoicePoint<IntVar> select =	
		new SimpleSelect<IntVar>(totalCOST, new LargestDomain<IntVar>(), new IndomainMax<IntVar>());
	boolean result = label.labeling(store, select);

	System.out.println (result);

    }

}
