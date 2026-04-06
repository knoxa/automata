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
					sense.setDirection(leftOrRight);
					sense.setSquare(corner.getNeighbour(back));
					retval.add(sense);
				}
			}
		}
				
		return retval;
	}

	
	public static Map<Square, Set<Sense>> sense(Map<Square, Integer[]> coordinates, Map<Integer, Map<Integer, Set<Square>>> layout) {
		
		// The "coordinates" and "layout" maps encode tiles laid out in a grid.
		// Find squares in each tile that are adjacent to squares that aren't in the tile (so are in a different tile).
	
		// Return a map of observations made by each square
		Map<Square, Set<Sense>> observations = new HashMap<>();
			
		// Partition the located squares to make tiles
		Map<Integer, Set<Square>> partitions = Partitioner.partition(coordinates.keySet());

		for ( Integer partNo: partitions.keySet() ) {
			
			Set<Square> tile = partitions.get(partNo);
			
			for ( Square square: tile ) {
				
				// Each square "looks" in all neighbouring directions.
				// If a square is seen, that isn't part of the same tile, then add a Sense object to "observations".
				
				Set<Sense> seenByThisTile = new HashSet<>();
				
				Integer[] coords = coordinates.get(square);
				
				for ( Direction direction: Compass.compass ) {
					
					Map<Integer, Set<Square>> row = layout.get(coords[1] + Compass.getOffsetY(direction));
					
					if ( row != null ) {
						
						Set<Square> locatedSquares = row.get(coords[0] + Compass.getOffsetX(direction));
						
						if ( locatedSquares != null ) {
							
							for ( Square sensed: locatedSquares ) {
								
								if ( !tile.contains(sensed) ) {
									
									Sense sense = new Sense(direction, sensed);
									seenByThisTile.add(sense);
								}
							}
						}
					}
				}
				
				observations.put(square, seenByThisTile);
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
	
	public static Set<Square> getSquaresThatCanSee(Set<Square> squares, Map<Square, Set<Sense>> observations) {
		
		Set<Square> results = new HashSet<>();
		
		for ( Square square: squares ) {
			
			if ( observations.get(square) != null )  results.add(square);
		}
		
		return results;
	}

	
	public static Map<Square, Set<Square>> getSensedSquaresBySquare(Map<Square, Set<Sense>> contacts) {
		
		// take a map of observations keyed by squares and create a map where the key is the same but
		// the values are the sensed squares.
		
		Map<Square, Set<Square>> sensedSquares = new HashMap<>();	
		
		for ( Square square: contacts.keySet() )  {
			
			Set<Square> squares = new HashSet<Square>();
			for ( Sense sense: contacts.get(square) )  squares.add(sense.getSquare());
			sensedSquares.put(square, squares);
		}
		
		return sensedSquares;
	}

	
	public static Map<Integer, Set<Square>> getSensedSquaresByTile(Map<Square, Set<Sense>> contacts, Map<Integer, Set<Square>> partitionMap) {
		
		// Take a map of observations keyed by squares and a map of squares partitioned into tiles.
		// Create a map where the key is the tile number and the value is the set of squares contactable from that tile. 
		
		Map<Integer, Set<Square>> sensedSquares = new HashMap<>();	
		
		for ( Integer partNo: partitionMap.keySet() ) {
			
			Set<Square> squares = new HashSet<Square>();

			for ( Square square: partitionMap.get(partNo) )  {
				
				Set<Sense> seen = contacts.get(square);
				
				if ( seen != null ) {
					
					for ( Sense sense: seen )  {
						squares.add(sense.getSquare());
					}
				}
			}
			
			sensedSquares.put(partNo, squares);
		}
		
		return sensedSquares;
	}
	
	
	public static Set<Square> moreThanOneContact(Set<Square> squares, Map<Square, Set<Square>> contacts) {
		
		// get the set of squares that are in contact with 2 or more squares in a neighbouring tile.
		
		Set<Square> results = new HashSet<>();	
		for ( Square square: squares )   if ( contacts.get(square).size() > 1 ) results.add(square);
		return results;
	}
	
	
	public static Map<Square, Set<Sense>> restrictEnvironment(Map<Square, Set<Sense>> environment, Set<Square> locality) {
		
		// restrict the environment to the given locality - squares outside do not sense, and are not sensed
		
		Map<Square, Set<Sense>> filtered = new HashMap<>();
		
		for ( Square square: environment.keySet() ) {
			
			if ( locality.contains(square) ) {
				
				Set<Sense> visible = new HashSet<Sense>();
				
				for ( Sense sense: environment.get(square) ) {
					
					if ( locality.contains(sense.getSquare()) )  visible.add(sense);
				}
				
				filtered.put(square, visible);
			}
		}
		
		return filtered;
	}
	
	
	public static Map<Square, Set<Sense>> sensedByTile(Map<Square, Set<Sense>> environment, Set<Square> tile) {
		
		Map<Square, Set<Sense>> filtered = new HashMap<>();
		
		for ( Square square: tile ) {
			
			Set<Sense> visible = new HashSet<Sense>();
			
			for ( Sense sense: environment.get(square) ) {
				
				if ( !tile.contains(sense.getSquare()) )  visible.add(sense);
			}
			
			filtered.put(square, visible);
		}
		
		return filtered;
	}
	
}
