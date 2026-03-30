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
import observe.SquareObserver;
import orient.Chooser;
import orient.Partitioner;
import tiles.Tile;

public class TileAction {

	public static void dissolveTile(Set<Square> tile) {
		
		Set<Action> moves = new HashSet<Action>();
		
		for ( Square square: tile ) {
			
			Action action = new Action();
			action.setAct(Act.DETACH); action.setActor(square); action.setSense(new Sense());
			moves.add(action);
		}
		
		Action.makeMoves(moves);
	}

	public static Square anySquareDefects(Set<Square> activeTile, Map<Square, Set<Sense>> environment, Chooser chooser) {
		
		Square defector = null;
		
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
		
		// get smallest tile size
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
		defector = action.getActor();
	
		Action.makeMoves(moves);
		
		return defector;
	}

	public static Square detachableSquareDefects(Set<Square> activeTile, Map<Square, Set<Sense>> environment, Chooser chooser) {
		
		Square defector = null;
		
		Map<Integer, Set<Square>> ppMap = Partitioner.partition(activeTile);
		Map<Integer, Set<Square>> xxMap = Tile.getDetachableSquares(ppMap);

		Set<Action> moves = new HashSet<Action>();
		
		// key = squares in neighbouring tiles, values = squares in this tile that can see them
		Map<Square, Set<Square>> squaresThatCanSee = new HashMap<>();
				
		// find all the squares that can be "seen" from any square in the active tile
		Set<Square> visible = new HashSet<Square>();
		
		activeTile =  xxMap.get(1);
		
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
		
		// get smallest tile size
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
		defector = action.getActor();
	
		Action.makeMoves(moves);
		
		return defector;
	}

	public static Square displaceSquare(Set<Square> activeTile, Map<Square, Set<Sense>> environment, Chooser chooser, Set<Square> except) {
			
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(environment.keySet());
		Map<Square, Set<Sense>> observations = SquareObserver.sense(partitionMap, environment, partitionMap.keySet());
		Set<Square> squaresBorderingNeighbouringTiles = SquareObserver.getSquaresThatCanSee(activeTile, observations);
		
		Map<Integer, Set<Square>> detachableMap = Tile.getDetachableSquares(Partitioner.partition(activeTile));
		Set<Square> options = detachableMap.get(1); // assumption is that there is only one tile
		
		System.out.println("o1: " + options);
		
		options.removeAll(except);
		System.out.println("o2: " + options);
		options.retainAll(squaresBorderingNeighbouringTiles);
		System.out.println("o3: " + options);
		if ( options.size() == 0 ) return null; // !!!!!!
		Square toDetach = chooser.randomFromSet(options);

		List<Action> actions = new ArrayList<>();
		Action action = new Action();
		action.setAct(Act.DETACH); action.setActor(toDetach); action.setSense(new Sense());
		actions.add(action);
		
		Action.makeMoves(actions);
		return toDetach;
	}

	
	public static Square displaceSquare(Set<Square> activeTile, Map<Square, Set<Sense>> environment, Chooser chooser) {
		
		Square defector = detachableSquareDefects(activeTile, environment, chooser);
		Set<Square> modifiedTile = new HashSet<Square>();
		modifiedTile.add(defector);
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(modifiedTile);
		modifiedTile.addAll(partitionMap.get(1));

		Map<Integer, Set<Square>> detachableMap = Tile.getDetachableSquares(partitionMap);
		Set<Square> options = detachableMap.get(1);
		options.remove(defector);		
		Square toDetach = chooser.randomFromSet(options);
		
		List<Action> actions = new ArrayList<>();
		Action action = new Action();
		action.setAct(Act.DETACH); action.setActor(toDetach); action.setSense(new Sense());
		actions.add(action);
		
		Action.makeMoves(actions);
		return toDetach;
	}

}
