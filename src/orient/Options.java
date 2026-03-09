package orient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import act.Act;
import act.Action;
import cakes.category.Maps;
import cells.Sense;
import cells.Square;

public class Options {

	public static Map<Integer, Set<Action>> unitTiles(State state, Random random) {
		
		Map<Integer, Set<Action>> options = new HashMap<Integer, Set<Action>>();
		
		Map<Integer, Set<Square>> partitionMap = state.getPartitionMap();
		List<Integer> tiles = new ArrayList<Integer>();
		tiles.addAll(partitionMap.keySet());
		Collections.shuffle(tiles, random);
		
		Map<Square, Set<Integer>> reverse = cakes.category.Maps.invertMap(state.getPartitionMap());
		
		Set<Integer> done = new HashSet<Integer>();
		
		for ( Integer partNo: tiles ) {
			
			Set<Square> tile = partitionMap.get(partNo);
			int partSize = tile.size();
			
			for ( Square square: tile ) {
				
				Set<Sense> sensed = state.getEnvironment().get(square);
				Set<Integer> neighbours = state.getPartitionGraph().get(partNo);

				for ( Sense sense: sensed ) {
					
					Integer neighbourPart = reverse.get(sense.getSquare()).iterator().next();
					int neighbourSize = state.getPartitionMap().get(neighbourPart).size();

					if ( neighbours != null && neighbours.contains(neighbourPart) && !done.contains(neighbourPart) ) {
						
						if ( partSize == 1 && neighbourSize == 1 && !done.contains(partNo) ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.ATTACH);
							action.setProbability(0.6);
							Maps.addMapValue(options, reverse.get(square).iterator().next(), action);
							done.add(neighbourPart);
						}
					}
				}							
			}
		}
		
		return options;
	}

	
	public static List<Operation> makePentominoes(State state, Random random) {
		
		List<Operation> options = new ArrayList<>();
		
		Map<Integer, Set<Square>> partitionMap = state.getPartitionMap();
		List<Integer> tiles = new ArrayList<Integer>();
		tiles.addAll(partitionMap.keySet());
		Collections.shuffle(tiles, random);
		
		Map<Square, Set<Integer>> reverse = cakes.category.Maps.invertMap(state.getPartitionMap());
		
		for ( Integer partNo: tiles ) {
			
			Set<Square> tile = partitionMap.get(partNo);
			int partSize = tile.size();
			
			for ( Square square: tile ) {
				
				Set<Sense> sensed = state.getEnvironment().get(square);
				Set<Integer> neighbours = state.getPartitionGraph().get(partNo);

				for ( Sense sense: sensed ) {
					
					Integer neighbourPart = reverse.get(sense.getSquare()).iterator().next();
					int neighbourSize = state.getPartitionMap().get(neighbourPart).size();

					if ( neighbours != null && neighbours.contains(neighbourPart) ) {
						
						if ( partSize < neighbourSize && partSize + neighbourSize == 5 ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.ATTACH);
							action.setProbability(1.0);
							Operation op = new Operation(); op.setPreference(1.0);
							op.setLabel(String.format("Attach % d squares (tile %d) to %d squares (tile %d)", partSize, partNo, neighbourSize, neighbourPart));
							op.addAction(action); op.addAgent(partNo); op.addAgent(neighbourPart);
							options.add(op);
						}
						
						else if ( partSize == 1 && neighbourSize == 1 ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.ATTACH);
							action.setProbability(0.6);
							Operation op = new Operation(); op.setPreference(random.nextDouble());
							op.setLabel(String.format("Attach % d squares (tile %d) to %d squares (tile %d)", partSize, partNo, neighbourSize, neighbourPart));
							op.addAction(action); op.addAgent(neighbourPart);
							options.add(op); op.addAgent(partNo); op.addAgent(neighbourPart);
						}
						
						else if ( partSize == 2 && neighbourSize == 6 ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.CAPTURE);
							action.setProbability(1.0);
							Operation op = new Operation(); op.setPreference(random.nextDouble());
							op.setLabel(String.format("Attach % d squares (tile %d) to %d squares (tile %d)", partSize, partNo, neighbourSize, neighbourPart));
							op.addAction(action); op.addAgent(neighbourPart);
							options.add(op); op.addAgent(partNo); op.addAgent(neighbourPart);
						}
						
						else if ( partSize == 4 && neighbourSize == 4 ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.DEFECT);
							action.setProbability(1.0);
							Operation op = new Operation(); op.setPreference(0.1);
							op.setLabel(String.format("DEFECT % d squares (tile %d) to %d squares (tile %d)", partSize, partNo, neighbourSize, neighbourPart));
							op.addAction(action); op.addAgent(partNo); op.addAgent(neighbourPart);
							options.add(op); op.addAgent(partNo); op.addAgent(neighbourPart);
						}
						
						else if ( partSize == 1 && neighbours.size() < 3 ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.ATTACH);
							action.setProbability(1.0);
							Operation op = new Operation(); op.setPreference(1.0);
							op.setLabel(String.format("Attach % d squares (tile %d) to %d squares (tile %d)", partSize, partNo, neighbourSize, neighbourPart));
							op.addAction(action); op.addAgent(partNo); op.addAgent(neighbourPart);
							options.add(op); op.addAgent(partNo); op.addAgent(neighbourPart);
						}
						
						else if ( partSize == 4 && neighbours.size() < 3 ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.CAPTURE);
							action.setProbability(1.0);
							Operation op = new Operation(); op.setPreference(1.0);
							op.setLabel(String.format("Attach % d squares (tile %d) to %d squares (tile %d)", partSize, partNo, neighbourSize, neighbourPart));
							op.addAction(action); op.addAgent(neighbourPart);
							options.add(op); op.addAgent(partNo); op.addAgent(neighbourPart);
						}
					}
				}											
			}
		}
		
		return options;
	}
	
	public static List<Operation> makePentominoesX(State state, Random random) {
		
		List<Operation> options = new ArrayList<>();
		
		Map<Integer, Set<Square>> partitionMap = state.getPartitionMap();
		List<Integer> tiles = new ArrayList<Integer>();
		tiles.addAll(partitionMap.keySet());
		Collections.shuffle(tiles, random);
		
		Map<Square, Set<Integer>> reverse = cakes.category.Maps.invertMap(state.getPartitionMap());
		
		for ( Integer partNo: tiles ) {
			
			Set<Square> tile = partitionMap.get(partNo);
			int partSize = tile.size();
			
			for ( Square square: tile ) {
				
				Set<Sense> sensed = state.getEnvironment().get(square);
				Set<Integer> neighbours = state.getPartitionGraph().get(partNo);

				for ( Sense sense: sensed ) {
					
					Integer neighbourPart = reverse.get(sense.getSquare()).iterator().next();
					int neighbourSize = state.getPartitionMap().get(neighbourPart).size();

					if ( neighbours != null && neighbours.contains(neighbourPart) ) {
						
						if ( partSize < neighbourSize && partSize + neighbourSize == 5 ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.ATTACH);
							action.setProbability(1.0);
							Operation op = new Operation(); op.setPreference(1.0);
							op.setLabel(String.format("Attach % d squares (tile %d) to %d squares (tile %d)", partSize, partNo, neighbourSize, neighbourPart));
							op.addAction(action); op.addAgent(partNo); op.addAgent(neighbourPart);
							options.add(op);
						}
						
						else if ( partSize == 1 && neighbourSize == 1 ) {
							
							Action action = new Action();
							action.setActor(square);
							action.setSense(sense);
							action.setAct(Act.ATTACH);
							action.setProbability(0.6);
							Operation op = new Operation(); op.setPreference(random.nextDouble());
							op.setLabel(String.format("Attach % d squares (tile %d) to %d squares (tile %d)", partSize, partNo, neighbourSize, neighbourPart));
							op.addAction(action); op.addAgent(neighbourPart);
							options.add(op);
						}
					}
				}											
			}
		}
		
		return options;
	}


}
