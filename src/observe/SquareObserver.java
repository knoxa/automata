package observe;

import java.util.HashSet;
import java.util.Set;

import cells.Compass;
import cells.Direction;
import cells.Sense;
import cells.Square;

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
}
