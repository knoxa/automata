package worlds;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cells.Direction;

public class Compass {

	public static final Direction[] compass = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
	
	private static Map<Direction, Integer> compassMap;
	
	private static Map<Direction, Integer> offsetsX;
	private static Map<Direction, Integer> offsetsY;


	static {
		
		compassMap = new EnumMap<Direction, Integer>(Direction.class);
		compassMap.put(Direction.NORTH, 0);
		compassMap.put(Direction.EAST, 1);
		compassMap.put(Direction.SOUTH, 2);
		compassMap.put(Direction.WEST, 3);
		
		offsetsX = new EnumMap<Direction,Integer>(Direction.class);
		offsetsX.put(Direction.NORTH, 0); offsetsX.put(Direction.EAST, 1); offsetsX.put(Direction.SOUTH, 0); offsetsX.put(Direction.WEST, -1);
		
		offsetsY = new EnumMap<Direction,Integer>(Direction.class);
		offsetsY.put(Direction.NORTH, -1); offsetsY.put(Direction.EAST, 0); offsetsY.put(Direction.SOUTH, 1); offsetsY.put(Direction.WEST, 0);
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
	
	public static Integer getOffsetX(Direction direction) {
		
		return offsetsX.get(direction);
	}
	
	public static Integer getOffsetY(Direction direction) {
		
		return offsetsY.get(direction);
	}
	
	private static Map<Direction, Direction> rotate(int amount) {
		
		Map<Direction, Direction> transform = new HashMap<>();
		for ( Direction direction: compass )  transform.put(direction, compass[(compassMap.get(direction) + amount) % 4]);
		return transform;
	}
	
	public static Map<Direction, Direction> rotate90() {
		return rotate(1);
	}
	
	public static Map<Direction, Direction> rotate270() {
		
		return rotate(3);
	}
	
	public static Map<Direction, Direction> reflectHorizontal() {
		
		Map<Direction, Direction> transform = new HashMap<>();
		transform.put(Direction.NORTH, Direction.SOUTH); transform.put(Direction.SOUTH, Direction.NORTH);
		transform.put(Direction.EAST, Direction.EAST);   transform.put(Direction.WEST, Direction.WEST);
		return transform;
	}
	
	public static Map<Direction, Direction> reflectVertical() {
		
		Map<Direction, Direction> transform = new HashMap<>();
		transform.put(Direction.NORTH, Direction.NORTH); transform.put(Direction.SOUTH, Direction.SOUTH);
		transform.put(Direction.EAST, Direction.WEST);   transform.put(Direction.WEST, Direction.EAST);
		return transform;
	}

}
