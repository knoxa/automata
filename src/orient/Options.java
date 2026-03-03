package orient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import act.Act;
import act.Action;
import cakes.category.Maps;
import cells.Sense;
import cells.Square;

public class Options {

	public static Map<Integer, Set<Action>> makePentominoes(State state) {
		
		Map<Integer, Set<Action>> options = new HashMap<Integer, Set<Action>>();
		
		Map<Square, Set<Integer>> reverse = cakes.category.Maps.invertMap(state.getPartitionMap());

		for ( Square square: reverse.keySet() ) {
			
			Integer partNo = reverse.get(square).iterator().next();
			int partSize = state.getPartitionMap().get(partNo).size();
			
			Set<Sense> sensed = state.getEnvironment().get(square);

			Set<Integer> neighbours = state.getPartitionGraph().get(partNo);
						
			for ( Sense sense: sensed ) {
				
				Integer neighbourPart = reverse.get(sense.getSquare()).iterator().next();
				int neighbourSize = state.getPartitionMap().get(neighbourPart).size();

				if ( neighbours != null && neighbours.contains(neighbourPart) ) {
					
					if ( partSize == 1 ) {
						
						Action action = new Action();
						action.setActor(square);
						action.setSense(sense);
						action.setAct(Act.ATTACH);
						action.setProbability(0.3);
						Maps.addMapValue(options, reverse.get(square).iterator().next(), action);
					}
					else if ( partSize <= 3 && partSize <= neighbourSize && neighbourSize <= 4 ) {
						
						Action action = new Action();
						action.setActor(square);
						action.setSense(sense);
						action.setAct(Act.ATTACH);
						Maps.addMapValue(options, reverse.get(square).iterator().next(), action);						
					}
					else if ( partSize > neighbourSize && partSize != 5 ) {
											
						Action action = new Action();
						action.setActor(square);
						action.setSense(sense);
						action.setAct(Act.DEFECT);
						Maps.addMapValue(options, reverse.get(square).iterator().next(), action);						
					}
					else if ( partSize < 5 && partNo != neighbourPart ) {
						
						Action action = new Action();
						action.setActor(square);
						action.setSense(sense);
						action.setAct(Act.CAPTURE);
						Maps.addMapValue(options, reverse.get(square).iterator().next(), action);						
					}
					else if ( partSize > 5 && partNo != neighbourPart ) {
						
						Action action = new Action();
						action.setActor(square);
						action.setSense(sense);
						action.setAct(Act.DEFECT);
						Maps.addMapValue(options, reverse.get(square).iterator().next(), action);						
					}
				}
			}
		}
		
		return options;
	}
}
