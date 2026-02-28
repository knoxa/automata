package cells;

import java.util.HashMap;
import java.util.Map;

public class World {

	private static final Direction[] compass = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
	
	Map<Direction, Integer> compassMap; 

	public World() {
		
		compassMap = new HashMap<Direction, Integer>();
		compassMap.put(Direction.NORTH, 0);
		compassMap.put(Direction.EAST, 1);
		compassMap.put(Direction.SOUTH, 2);
		compassMap.put(Direction.WEST, 3);
	}

	public Direction getReturnDirection(Direction direction) {
		
		int step = compassMap.get(direction);
		int returnDirection = (step + 2) % 4;
		return compass[returnDirection];
	}

}
