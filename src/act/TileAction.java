package act;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cakes.category.Maps;
import cells.Sense;
import cells.Square;
import orient.Chooser;
import orient.Partitioner;

public class TileAction {

	public static void dissolve(Square active) {
		
		Set<Action> moves = new HashSet<Action>();
		
		// starting with the active square ...
		Set<Square> activeTile = new HashSet<Square>();
		activeTile.add(active);
		
		// add all the squares in the same tile
		Map<Integer, Set<Square>> activeMap = Partitioner.partition(activeTile);
		activeTile.addAll(activeMap.get(1));
		
		for ( Square square: activeTile ) {
			
			Action action = new Action();
			action.setAct(Act.DETACH); action.setActor(square); action.setSense(new Sense());
			moves.add(action);
		}
		
		Action.makeMoves(moves);
	}

	public static void oneSquareDefects(Set<Square> activeTile, Map<Square, Set<Sense>> environment, Chooser chooser) {
		
		Set<Action> moves = new HashSet<Action>();
		
		// key = squares in neighbouring tiles, values = squares in this tile that can see them
		Map<Square, Set<Square>> squaresThatCanSee = new HashMap<>();
				
		// find all the squares that can be "seen" from any tile in the active tile
		Set<Square> visible = new HashSet<Square>();
		
		for ( Square square: activeTile ) {
			
			Set<Sense> seen = environment.get(square);
			for ( Sense sense: seen ) {
				
				visible.add(sense.getSquare());
				Maps.addMapValue(squaresThatCanSee, sense.getSquare(), square);
			}
		}
		
		// construct the local neighbourhood, excluding the active tile
		Set<Square> neighbourhood = new HashSet<Square>();
		neighbourhood.addAll(visible);		
		neighbourhood.removeAll(activeTile);
		
		// partition it
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(neighbourhood);
		Map<Integer, Set<Integer>> sizeMap = Partitioner.collectBySize(partitionMap);
		
		Map<Square, Set<Integer>> partitionLookup = Maps.invertMap(partitionMap);
		//Integer activePart = partitionLookup.get(active).iterator().next();
		
		// get smallest and largest tile sizes ..
		List<Integer> sizes = new ArrayList<Integer>();
		sizes.addAll(sizeMap.keySet());
		Collections.sort(sizes);		
		int smallestSize = sizes.get(0);
				
		// a cell in the active tile defects to smallest neighbouring tile 
		
		// choose the target tile
		Set<Integer> candidates = new HashSet<Integer>();
		candidates.addAll(sizeMap.get(smallestSize));	
		Integer chosenTile = chooser.randomFromSet(candidates);
		
		// find squares in the active tile that can see the target
		Map<Square, Set<Sense>> connectable = new HashMap<>();
	
		for ( Square square: activeTile ) {
			
			Set<Sense> sensed = environment.get(square);
			
			for ( Sense sense: sensed ) {
				
				if ( !(activeTile.contains(sense.getSquare())) && partitionLookup.get(sense.getSquare()).iterator().next() == chosenTile ) {
					
					Maps.addMapValue(connectable, square, sense);
				}
			}
		}
	
		// choose one of the visible target squares ...
		Square from = chooser.randomFromSet(connectable.keySet());
		// ... and a suitable direction from that square
		Sense sense = chooser.randomFromSet(connectable.get(from));
		
		// make the move
		Action action = new Action();
		action.setAct(Act.DEFECT); action.setActor(from); action.setSense(sense);
		moves.add(action);		
	
		Action.makeMoves(moves);
	}

}
