package decide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import act.Action;

public class Decide {

	
	public static Set<Action> choose(Map<Integer, Set<Action>> actions, Random random) {
		
		Set<Action> choices = new HashSet<Action>();
		
		for ( Integer colony: actions.keySet() ) {
			
			List<Action> options = new ArrayList<Action>();
			options.addAll(actions.get(colony));
			int n = random.nextInt(options.size());
			Action action = options.get(n);  // one action per partition
			if ( random.nextDouble() < action.getProbability() )  choices.add(action);
		}
		
		return choices;
	}
}
