package cells;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Square {

	Map<Direction, Square> neighbours;
	String label = "*";

	public Square() {

		neighbours = new EnumMap<Direction, Square>(Direction.class);
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
		for ( Direction d: neighbours.keySet() )  retval.add(neighbours.get(d));
		return retval;		
	}
	
	public Map<Direction, Square> getNeighbourMap() {
		
		Map<Direction, Square> retval = new EnumMap<>(Direction.class);
		retval.putAll(neighbours);		
		return retval;		
	}
		
	public void clearNeighbour(Direction d) {
		
		neighbours.remove(d);
	}	

}
