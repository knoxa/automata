package cells;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Compass {

	private static final Direction[] compass = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
	
	private static Map<Direction, Integer> compassMap; 

	static {
		
		compassMap = new HashMap<Direction, Integer>();
		compassMap.put(Direction.NORTH, 0);
		compassMap.put(Direction.EAST, 1);
		compassMap.put(Direction.SOUTH, 2);
		compassMap.put(Direction.WEST, 3);
	}

	public static Direction getReturnDirection(Direction direction) {
		
		int step = compassMap.get(direction);
		int returnDirection = (step + 2) % 4;
		return compass[returnDirection];
	}

	
	public static Set<Direction> getOrthogonalDirections(Direction direction) {
		
		Set<Direction> retval = new HashSet<Direction>();
		
		int a = (compassMap.get(direction) + 1) % 4;
		int b = (compassMap.get(direction) + 3) % 4;

		retval.add(compass[a]); retval.add(compass[b]);
		return retval;
	}

}
