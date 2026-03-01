package tiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cells.Compass;
import cells.Direction;
import cells.Square;
import observe.SquareObserver;

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

}
