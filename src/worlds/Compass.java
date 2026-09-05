package worlds;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cells.Direction;

public class Compass {

	public static final Direction[] compass = { Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST };
	
	private static Map<Direction, Integer> compassMap;
	
	private static Map<Direction, Integer> offsetsX;
	private static Map<Direction, Integer> offsetsY;


	static {
		
		compassMap = new EnumMap<Direction, Integer>(Direction.class);
		compassMap.put(Direction.NORTH, 0);
		compassMap.put(Direction.NORTHEAST, 1);
		compassMap.put(Direction.EAST, 2);
		compassMap.put(Direction.SOUTHEAST, 3);
		compassMap.put(Direction.SOUTH, 4);
		compassMap.put(Direction.SOUTHWEST, 5);
		compassMap.put(Direction.WEST, 6);
		compassMap.put(Direction.NORTHWEST, 7);
		
		offsetsX = new EnumMap<Direction,Integer>(Direction.class);
		offsetsX.put(Direction.NORTH, 0); offsetsX.put(Direction.EAST, 1); offsetsX.put(Direction.SOUTH, 0); offsetsX.put(Direction.WEST, -1);
		offsetsX.put(Direction.NORTHEAST, 1); offsetsX.put(Direction.NORTHWEST, -1); offsetsX.put(Direction.SOUTHEAST, 1); offsetsX.put(Direction.SOUTHWEST, -1);
		
		offsetsY = new EnumMap<Direction,Integer>(Direction.class);
		offsetsY.put(Direction.NORTH, -1); offsetsY.put(Direction.EAST, 0); offsetsY.put(Direction.SOUTH, 1); offsetsY.put(Direction.WEST, 0);
		offsetsY.put(Direction.NORTHEAST, -1); offsetsY.put(Direction.NORTHWEST, -1); offsetsY.put(Direction.SOUTHEAST, 1); offsetsY.put(Direction.SOUTHWEST, 1);
	}

	public static Direction getReturnDirection(Direction direction) {
		
		int step = compassMap.get(direction);
		int returnDirection = (step + 4) % 8;
		return compass[returnDirection];
	}

	
	public static Set<Direction> getOrthogonalDirections(Direction direction) {
		
		Set<Direction> retval = new HashSet<Direction>();
		
		int a = (compassMap.get(direction) + 2) % 8;
		int b = (compassMap.get(direction) + 6) % 8;

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
		for ( Direction direction: compass )  transform.put(direction, compass[(compassMap.get(direction) + amount) % 8]);
		return transform;
	}
	
	public static Map<Direction, Direction> rotate90() {
		return rotate(2);
	}
	
	public static Map<Direction, Direction> rotate270() {
		
		return rotate(6);
	}
	
	public static Map<Direction, Direction> reflectHorizontal() {
		
		Map<Direction, Direction> transform = new HashMap<>();
		transform.put(Direction.NORTH, Direction.SOUTH); transform.put(Direction.SOUTH, Direction.NORTH);
		transform.put(Direction.EAST, Direction.EAST);   transform.put(Direction.WEST, Direction.WEST);
		transform.put(Direction.NORTHEAST, Direction.SOUTHEAST);  transform.put(Direction.SOUTHEAST, Direction.NORTHEAST);
		transform.put(Direction.NORTHWEST, Direction.SOUTHWEST);  transform.put(Direction.SOUTHWEST, Direction.NORTHWEST);
		return transform;
	}
	
	public static Map<Direction, Direction> reflectVertical() {
		
		Map<Direction, Direction> transform = new HashMap<>();
		transform.put(Direction.NORTH, Direction.NORTH); transform.put(Direction.SOUTH, Direction.SOUTH);
		transform.put(Direction.EAST, Direction.WEST);   transform.put(Direction.WEST, Direction.EAST);
		transform.put(Direction.NORTHEAST, Direction.NORTHWEST);  transform.put(Direction.NORTHWEST, Direction.NORTHEAST);
		transform.put(Direction.SOUTHEAST, Direction.SOUTHWEST);   transform.put(Direction.SOUTHWEST, Direction.SOUTHEAST);
		return transform;
	}
	
	public static boolean isCardinal(Direction direction) {
		
		Integer value = compassMap.get(direction);
		return value %2 == 0;
	}

}
