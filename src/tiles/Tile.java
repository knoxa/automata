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
import worlds.Compass;

public class Tile {

	public static boolean isDetachable(Square square) {
		
		// Can the square be removed from a tile without disconnecting the squares left behind?
		
		if ( square.neighbourCount() == 1 ) return true;
		
		else if ( square.neighbourCount() == 2 ) {
			
			for ( Square neighbour: square.getNeighbours() ) {
				
				if ( neighbour.neighbourCount() == 1 ) return false;
			}
			
			return true;
		}
		
		return false;
	}

	
	public static int area(Set<Square> tile) {
		
		// return the area of the smallest rectangle that covers the tile
		
		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		position(tile.iterator().next(), 0, 0, coordinates, positions);

		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE, xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE;
		
		for ( Square square: coordinates.keySet() ) {
			
			Integer[] coords = coordinates.get(square);			
			xMin = coords[0] < xMin ? coords[0] : xMin;
			xMax = coords[0] > xMax ? coords[0] : xMax;
			yMin = coords[1] < yMin ? coords[1] : yMin;
			yMax = coords[1] > yMax ? coords[1] : yMax;
		}

		int height = yMax - yMin + 1;
		int width  = xMax - xMin + 1;
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
		
		Map<Square, Set<Sense>> observations = SquareObserver.sense(coordinates, positions);

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

}
