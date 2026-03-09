package decide;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import act.Action;
import cells.Square;

public class Decide {

	
	public static Set<Action> choose(Map<Integer, Set<Action>> actions, Random random) {
		
		Set<Action> choices = new HashSet<Action>();
		
		for ( Integer colony: actions.keySet() ) {
			
			List<Action> options = new ArrayList<Action>();
			options.addAll(actions.get(colony));
			int n = random.nextInt(options.size());
			Action action = options.get(n);  // at most, one action per partition
			if ( random.nextDouble() < action.getProbability() )  choices.add(action);
		}
		
		return choices;
	}
	
	
	public static Set<Action> accept(Map<Integer, Set<Action>> actions, Random random) {
		
		// filter a map of actions keyed by square
		// no rules about partitions applied here ...
		
		Set<Action> choices = new HashSet<Action>();
		
		for ( Integer tileNo: actions.keySet() ) {
			
			for ( Action action: actions.get(tileNo) ) {
				
				if ( random.nextDouble() < action.getProbability() )  choices.add(action);
			}
		}
		
		return choices;
	}
	
	
	public static Set<Action> acceptX(Map<Square, Set<Action>> actions, Random random) {
		
		// filter a map of actions keyed by square
		// no rules about partitions applied here ...
		
		Set<Action> choices = new HashSet<Action>();
		
		for ( Square square: actions.keySet() ) {
			
			List<Action> options = new ArrayList<Action>();
			options.addAll(actions.get(square));
			
			options.sort(new Comparator<Action> () {

				public int compare(Action arg0, Action arg1) {
					return Double.compare(arg1.getProbability(), arg0.getProbability()); // ascending
				}
			});
			
			//int n = random.nextInt(options.size());
			Action action = options.get(0);  // most probable action
			if ( random.nextDouble() < action.getProbability() )  choices.add(action);
		}
		
		return choices;
	}

}
