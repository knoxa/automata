package observe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cakes.category.Maps;
import cells.Direction;
import cells.Sense;
import cells.Square;
import world.Board;

public class BoardObserver {

	public static Map<Square, Set<Sense>> lookAbout(Board board) {
		
		// create a map of squares pointing to neighbours they can "see".
		
		final Direction[] compass = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
		
		Square[][] grid = board.getGrid();
		Map<Square, Set<Sense>> environment = new HashMap<Square, Set<Sense>>();

		int boardHeight = grid.length;
		int boardWidth  = grid[0].length;
		
		for ( int col = 0; col < boardWidth; col++ ) {
			
			for ( int row = 0; row < boardHeight; row++ ) {
				
				Square square = grid[row][col];
				Set<Sense> sensed = new HashSet<Sense>();
				
				// the maths works with 8 compass points, but here we restrict to 4 by adding 2 at each step
				
				//for ( int step = 0; step < 8; step++ ) {
				for ( int step = 0; step < 8; step+=2 ) {
					
					int xoffset = Integer.signum((step - 4) % 4 * -1);
					int yoffset = Integer.signum(((step + 2) % 8 - 4) % 4);
					
					int neighbourCol = col + xoffset;
					int neighbourRow = row + yoffset;
					
					if (neighbourCol >= 0 && neighbourRow >= 0 && neighbourCol < boardWidth && neighbourRow < boardHeight ) {
						
						Square neighbour = grid[neighbourRow][neighbourCol];
						Sense sense = new Sense(); sense.setSquare(neighbour); sense.setDirection(compass[step/2]);
						sensed.add(sense);
					}
				}
				
				Maps.addMapValues(environment, square, sensed);
			}
		}
		
		return environment;
	}
	
}
