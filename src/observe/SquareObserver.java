package observe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cakes.category.Maps;
import cells.Direction;
import cells.Sense;
import cells.Square;
import orient.Partitioner;
import worlds.Compass;

public class SquareObserver {

	public static Set<Sense> diagonal(Square square, Direction forward) {
		
		Set<Sense> retval = new HashSet<Sense>();
		
		// look for squares that you can get to by going one step forward (in given direction), 
		// then either left or right, then one step back.

		Set<Direction> orthogonal = Compass.getOrthogonalDirections(forward);
		Direction back = Compass.getReturnDirection(forward);
		
		Square inFront = square.getNeighbour(forward);
		
		if ( inFront != null ) {
			
			for ( Direction leftOrRight: orthogonal ) {
				
				Square corner = inFront.getNeighbour(leftOrRight);
				
				if ( corner != null && corner.hasNeighbour(back) ) {

					// found a square to report ...
					
					Sense sense = new Sense();
					sense.setDirection(leftOrRight);;
					sense.setSquare(corner.getNeighbour(back));
					retval.add(sense);
				}
			}
		}
				
		return retval;
	}

	
	public static Map<Square, Set<Sense>> sense(Map<Square, Integer[]> coordinates, Map<Integer, Map<Integer, Set<Square>>> layout) {
		
		// The "coordinates" and "layout" maps encode tiles laid out in a grid. There are assumed to be two tiles.
		// Find squares in the first tile that are adjacent to squares that aren't in this tile (so are in the second tile).
	
		// Return a map of observations made by each square in the first tile
		Map<Square, Set<Sense>> observations = new HashMap<>();
			
		// Partition the located squares to make tiles
		Map<Integer, Set<Square>> partitions = Partitioner.partition(coordinates.keySet());
		
		// just work with the first tile (need to change this later?)
		
		Set<Square> tile = partitions.get(1);
		
		for ( Square square: tile ) {
			
			// Each square "looks" in all neighbouring directions.
			// If a square is seen, that isn't part of the same tile, then add a Sense object to "observations". 
			
			Integer[] coords = coordinates.get(square);
			
			for ( Direction direction: Compass.compass ) {
				
				Map<Integer, Set<Square>> row = layout.get(coords[1] + Compass.getOffsetY(direction));
				
				if ( row != null ) {
					
					Set<Square> locatedSquares = row.get(coords[0] + Compass.getOffsetX(direction));
					
					if ( locatedSquares != null ) {
						
						for ( Square sensed: locatedSquares ) {
							
							if ( !tile.contains(sensed) ) {
								
								Sense sense = new Sense(direction, sensed);
								Maps.addMapValue(observations, square, sense);
							}
						}
					}
				}
			}
		}
		
		return observations;
	}

	
	public static Map<Square, Set<Sense>> sense(Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment, Set<Integer> tiles) {
		
		// Find squares in 'tiles' that are adjacent to squares that aren't in the same tile.
		// Return a map of observations made by each square of the supplied tiles.
		
		Map<Square, Set<Sense>> observations = new HashMap<Square, Set<Sense>>();

		Map<Square, Set<Integer>> reverse = cakes.category.Maps.invertMap(partitionMap);
		
		for ( Integer tile: tiles ) {
			
			for ( Square square: partitionMap.get(tile) ) {
				
				Set<Sense> sensed = environment.get(square);
				
				for ( Sense sense: sensed ) {
					
					Integer neighbourPart = reverse.get(sense.getSquare()).iterator().next();
					
					if ( neighbourPart != tile && tiles.contains(neighbourPart) )  {
						
						Maps.addMapValue(observations, square, sense);
					}
				}
			}
		}
						
		return observations;
	}

}
