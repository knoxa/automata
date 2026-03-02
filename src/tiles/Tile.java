package tiles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cakes.category.Maps;
import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.SquareObserver;
import orient.Partitioner;
import worlds.Compass;

public class Tile {

	public static boolean isDetachable(Square square, Direction direction) {
		
		// Can the square be removed from a tile without disconnecting the squares left behind?		
		return square.neighbourCount() == 1 || SquareObserver.diagonal(square, direction).size() > 0 ? true : false;
	}

	
	public static int area(Set<Square> tile) {
		
		// return the area of the smallest rectangle that covers the tile
		
		int height = 0;
		int width  = 0;
	
		for ( Square s: tile ) {
			
			if ( !s.hasNeighbour(Direction.EAST ) )  height++;
			if ( !s.hasNeighbour(Direction.NORTH ) ) width++;
		}
		
		return width * height;
	}

	
	public static int straightThree(Set<Square> tile) {
		
		// return the number of occurrences of 3 tiles in a straight line
		
		int n = 0;
		
		for ( Square square: tile ) {
			
			for ( Direction direction: new Direction[] {Direction.NORTH, Direction.EAST} ) {
				
				Direction oppositeDirection = Compass.getReturnDirection(direction);
				
				if ( square.getNeighbour(direction) != null &&  square.getNeighbour(oppositeDirection) != null ) n++;
			}
		}
		
		return n;
	}


	public static Map<Integer, Integer> countNeighbours(Set<Square> tile) {
		
		// return a map of counts of squares in the tile that have the given number of neighbours
		
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		counts.put(0, 0); counts.put(1, 0); counts.put(2, 0); counts.put(3, 0); counts.put(4, 0);
		
		for ( Square s: tile ) {
			
			int n = s.neighbourCount();		
			Integer count = counts.get(n);
			count++;
			counts.put(n, count);
		}
		return counts;
	}


	public static void connect(Square from, Direction step, Square to) {
		
		from.setNeighbour(step, to);
		Direction returnDirection = Compass.getReturnDirection(step);
		to.setNeighbour(returnDirection, from);
		
	}


	public static void detach(Square from, Direction step, Square to) {
		
		from.clearNeighbour(step);
		to.clearNeighbour(Compass.getReturnDirection(step));
	}


	public static void detach(Square square) {
		
		clearNeighbours(square);
	}


	public static void detachTile(Set<Square> squares) {
		
		Map<Square, Set<Sense>> observations = new HashMap<>();
		
		for ( Square square: squares ) {
			
			for (Direction direction: Compass.compass ) {
				
				if ( square.hasNeighbour(direction) ) {
					
					Square neighbour = square.getNeighbour(direction);
					
					if ( !squares.contains(neighbour) ) {
						
						Sense sense = new Sense(direction, neighbour);
						Maps.addMapValue(observations, square, sense);
					}
				}
			}
		}
		
		for ( Square square: observations.keySet() ) {
			
			for ( Sense sense: observations.get(square) ) {
				
				detach(square, sense.getDirection(), sense.getSquare());
			}
		}
	}


	public static void capture(Square from, Direction step, Square to) {
		
		detach(to);
		attach(from, step, to);
	}


	public static void defect(Square from, Direction step, Square to) {
		
		detach(from);
		attach(from, step, to);
	}


	private static void clearNeighbours(Square square) {
		
		Map<Direction, Square> neighbours = square.getNeighbourMap();
		
		for ( Direction direction: neighbours.keySet() ) {
			
			square.clearNeighbour(direction);
			neighbours.get(direction).clearNeighbour(Compass.getReturnDirection(direction));
		}
	}
	
	public static void attach(Square from, Direction direction, Square to) {
			
		// layout both tiles with square "from" at position 0,0 and square "to" at the neighbouring position in the given direction. 

		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		position(from, 0, 0, coordinates, positions);
		position(to, Compass.getOffsetX(direction), Compass.getOffsetY(direction), coordinates, positions);
		
		Map<Square, Set<Sense>> observations = sense(coordinates, positions);

		for ( Square square: observations.keySet() ) {
			
			for ( Sense sense: observations.get(square) ) {
				
				square.setNeighbour(sense.getDirection(), sense.getSquare());
				Direction returnDirection = Compass.getReturnDirection(sense.getDirection());
				sense.getSquare().setNeighbour(returnDirection, square);
			}
		}
	}

	public static boolean position(Square square, Integer x, Integer y, Map<Square, Integer[]> coordinates, Map<Integer, Map<Integer, Set<Square>>> layout) {
		
		// A recursive method to position all squares that can be reached from the given one.
		
		// The square "from" is places at the given coordinates, then the method is called again with all neighbours of "from", with coordinates adjusted accordingly.
		// The "coordinates" map stored already located squares.
		// The "layout" map stores squares at locations. There will be more than one square (a collision) at a location if the tiles overlap.
		// the return value is "true" if the tile (square and linked neighbours) can be positioned without causing a collision.
		
		boolean tileFits = true;
		
		Integer[] coords = new Integer[2];
		coords[0] = x; coords[1] = y;
		coordinates.put(square, coords);

		Map<Integer, Set<Square>> row = layout.get(y);
		
		if ( row == null ) {
			
			// haven't seen a square for this row yet ...
			row = new HashMap<Integer, Set<Square>>();
		}
		
		Set<Square> locatedSquare = row.get(x);
		
		if ( locatedSquare == null ) {
			
			// nothing in this location - add the square
			locatedSquare = new HashSet<Square>();
		}
		else {
			
			// tiles collide
			// System.out.println("COLLISION: " + locatedSquare + " <> " + square + " at " + y + "," + x);
			tileFits = false;
		}
		
		locatedSquare.add(square);
		row.put(x, locatedSquare);
		layout.put(y, row);		
		
		Map<Direction, Square> neighbours = square.getNeighbourMap();
		
		for ( Direction direction: neighbours.keySet() ) {
			
			Square neighbour = neighbours.get(direction);
			
			if ( coordinates.get(neighbour) == null ) {
				
				boolean fits = position(neighbour, x + Compass.getOffsetX(direction), y + Compass.getOffsetY(direction), coordinates, layout);
				if ( !fits )  tileFits = false;
			}
		}
		
		return tileFits;
	}

	
	public static Map<Square, Set<Sense>> sense(Map<Square, Integer[]> coordinates, Map<Integer, Map<Integer, Set<Square>>> layout) {
		
		// The "coordinates" and "layout" maps encode tiles laid out in a grid. There are assumed to be two tiles.
		// Find squares in the first tile that are adjacent to squares that aren't in this tile (so are in the second tile).

		// Return a map of observations made by each square in the first tile
		Map<Square, Set<Sense>> observations = new HashMap<>();
			
		// Partion the located squares to make tiles
		Map<Integer, Set<Square>> partitions = Partitioner.partition(coordinates.keySet());
		//System.out.println("number of partitions is " + partitions.size());
		
		
		// just work with the first tile (need to change this later?)
		
		Set<Square> tile = partitions.get(1);
		
		for ( Square square: tile ) {
			
			// Each square "looks" in all neighbouring directions.
			// If a square is seen, that isn't part of the same tile, then add a Sense object to "observatnios". 
			
			Integer[] coords = coordinates.get(square);
			
			for (Direction direction: Compass.compass ) {
				
				Map<Integer, Set<Square>> row = layout.get(coords[1] + Compass.getOffsetY(direction));
				
				if ( row != null ) {
					
					Set<Square> locatedSquares = row.get(coords[0] + Compass.getOffsetX(direction));
					
					if ( locatedSquares != null ) {
						
						for ( Square sensed: locatedSquares ) {
							
							if ( !tile.contains(sensed) ) {
								
								//System.out.println(square + " SEES " + locatedSquares + " in direction " + direction);
								
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

}
