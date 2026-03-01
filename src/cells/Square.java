package cells;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Square {

	Map<Direction, Square> neighbours;
	String label = "*";
	private Direction[] compass = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
	Map<Direction, Integer> compassMap; 

	public Square() {

		neighbours = new EnumMap<Direction, Square>(Direction.class);
		compassMap = new HashMap<Direction, Integer>();
		compassMap.put(Direction.NORTH, 0);
		compassMap.put(Direction.EAST, 1);
		compassMap.put(Direction.SOUTH, 2);
		compassMap.put(Direction.WEST, 3);
	}
	
	public void setLabel(String label) {
		
		this.label = label;
	}
	
	public String getLabel() {
		
		return label;
	}

	public boolean hasNeighbour(Direction direction) {
		
		return neighbours.get(direction) != null;
	}

	public void setNeighbour(Direction direction, Square square) {

		neighbours.put(direction, square);		
	}

	public Square getNeighbour(Direction direction) {

		return neighbours.get(direction);
	}
	

	public Set<Sense> diagonal(Direction forward) {
		
		Set<Sense> retval = new HashSet<Sense>();
		
		// look for squares that you can get to by going one step forward (in given direction), 
		// then either left or right, then one step back.
		
		Set<Direction> orthogonal = getOrthogonalDirections(forward);
		Direction back = getReturnDirection(forward);
		
		Square inFront = neighbours.get(forward);
		
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

	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		
		for ( Direction step: neighbours.keySet() ) {
			
			if ( neighbours.get(step) != null )  buffer.append(" " + step + " ");
		}
		
		String directions = buffer.toString().trim();
		
		String retval = String.format("%s:{%s}", label, directions);
		return retval;
	}
	
	public int neighbourCount() {
		
		int n = 0;
		
		for ( Square neighbour : neighbours.values() ) {
			
			if ( neighbour != null ) n++;
		}
		
		return n;
	}
	
	public Set<Square> getNeighbours() {
		
		Set<Square> retval = new HashSet<Square>();
		
		for ( int i = 0; i < 4; i++ ) {
			
			if (neighbours.get(compass[i]) != null ) retval.add(neighbours.get(compass[i]));
		}

		return retval;		
	}
	
	public void clearNeighbours() {
		
		for ( Direction d: neighbours.keySet() ) {
			
			Square neighbour = neighbours.get(d);
			neighbour.clearNeighbour(getReturnDirection(d));
			neighbours.remove(d);
		}
	}
	
	public void clearNeighbour(Direction d) {
		
	//	System.out.println("clear " + this + "  " + d);
		neighbours.remove(d);
	//	System.out.println("cleared " + this + "  " + d);
	}
	
	public Direction getReturnDirection(Direction direction) {
		
		int step = compassMap.get(direction);
		int returnDirection = (step + 2) % 4;
		return compass[returnDirection];
	}
	
	
	public Set<Direction> getOrthogonalDirections(Direction direction) {
		
		Set<Direction> retval = new HashSet<Direction>();
		
		int a = (compassMap.get(direction) + 1) % 4;
		int b = (compassMap.get(direction) + 3) % 4;

		retval.add(compass[a]); retval.add(compass[b]);
		return retval;
	}

	
	public int convertDirection(Direction direction) {
		
		return compassMap.get(direction);
	}
}
