package test.automata;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cells.Direction;
import worlds.Compass;

class CompassTest {

	@Test
	void returnDirection() {

		assertEquals(Direction.SOUTH, Compass.getReturnDirection(Direction.NORTH));
		assertEquals(Direction.NORTH, Compass.getReturnDirection(Direction.SOUTH));
		assertEquals(Direction.EAST,  Compass.getReturnDirection(Direction.WEST));
		assertEquals(Direction.WEST,  Compass.getReturnDirection(Direction.EAST));
		assertEquals(Direction.NORTHWEST, Compass.getReturnDirection(Direction.SOUTHEAST));
		assertEquals(Direction.SOUTHEAST, Compass.getReturnDirection(Direction.NORTHWEST));
		assertEquals(Direction.NORTHEAST, Compass.getReturnDirection(Direction.SOUTHWEST));
		assertEquals(Direction.SOUTHWEST, Compass.getReturnDirection(Direction.NORTHEAST));
	}

	@Test
	void offset() {

		Integer forward = 1, backward = -1, stationary = 0;
		
		assertEquals(forward,    Compass.getOffsetX(Direction.EAST));
		assertEquals(backward,   Compass.getOffsetX(Direction.WEST));
		assertEquals(stationary, Compass.getOffsetX(Direction.NORTH));
		assertEquals(stationary, Compass.getOffsetX(Direction.SOUTH));
		assertEquals(forward,    Compass.getOffsetX(Direction.NORTHEAST));
		assertEquals(backward,   Compass.getOffsetX(Direction.NORTHWEST));
		assertEquals(forward,    Compass.getOffsetX(Direction.SOUTHEAST));
		assertEquals(backward,   Compass.getOffsetX(Direction.SOUTHWEST));

		assertEquals(stationary, Compass.getOffsetY(Direction.EAST));
		assertEquals(stationary, Compass.getOffsetY(Direction.WEST));
		assertEquals(backward,   Compass.getOffsetY(Direction.NORTH));
		assertEquals(forward,    Compass.getOffsetY(Direction.SOUTH));
		assertEquals(backward,   Compass.getOffsetY(Direction.NORTHEAST));
		assertEquals(forward,    Compass.getOffsetY(Direction.SOUTHWEST));
		assertEquals(backward,   Compass.getOffsetY(Direction.NORTHWEST));
		assertEquals(forward,    Compass.getOffsetY(Direction.SOUTHEAST));
	}
	
	@Test
	void orthogonal() {
		
		Set<Direction> orthogonal = Compass.getOrthogonalDirections(Direction.NORTH);
		assertEquals(2, orthogonal.size());
		assertTrue(orthogonal.contains(Direction.EAST));
		assertTrue(orthogonal.contains(Direction.WEST));
		
		orthogonal = Compass.getOrthogonalDirections(Direction.SOUTH);
		assertEquals(2, orthogonal.size());
		assertTrue(orthogonal.contains(Direction.EAST));
		assertTrue(orthogonal.contains(Direction.WEST));
		
		orthogonal = Compass.getOrthogonalDirections(Direction.EAST);
		assertEquals(2, orthogonal.size());
		assertTrue(orthogonal.contains(Direction.NORTH));
		assertTrue(orthogonal.contains(Direction.SOUTH));
		
		orthogonal = Compass.getOrthogonalDirections(Direction.WEST);
		assertEquals(2, orthogonal.size());
		assertTrue(orthogonal.contains(Direction.NORTH));
		assertTrue(orthogonal.contains(Direction.SOUTH));
		
		orthogonal = Compass.getOrthogonalDirections(Direction.NORTHWEST);
		assertEquals(2, orthogonal.size());
		assertTrue(orthogonal.contains(Direction.NORTHEAST));
		assertTrue(orthogonal.contains(Direction.SOUTHWEST));
		
		orthogonal = Compass.getOrthogonalDirections(Direction.NORTHEAST);
		assertEquals(2, orthogonal.size());
		assertTrue(orthogonal.contains(Direction.NORTHWEST));
		assertTrue(orthogonal.contains(Direction.SOUTHEAST));
		
		orthogonal = Compass.getOrthogonalDirections(Direction.SOUTHEAST);
		assertEquals(2, orthogonal.size());
		assertTrue(orthogonal.contains(Direction.NORTHEAST));
		assertTrue(orthogonal.contains(Direction.SOUTHWEST));
		
		orthogonal = Compass.getOrthogonalDirections(Direction.SOUTHWEST);
		assertEquals(2, orthogonal.size());
		assertTrue(orthogonal.contains(Direction.NORTHWEST));
		assertTrue(orthogonal.contains(Direction.SOUTHEAST));
	}

	@Test
	void rotate90() {
	
		Map<Direction, Direction> map = Compass.rotate90();
		assertEquals(Direction.EAST,  map.get(Direction.NORTH));
		assertEquals(Direction.SOUTH, map.get(Direction.EAST));
		assertEquals(Direction.WEST,  map.get(Direction.SOUTH));
		assertEquals(Direction.NORTH, map.get(Direction.WEST));
		
		assertEquals(Direction.NORTHEAST, map.get(Direction.NORTHWEST));
		assertEquals(Direction.NORTHWEST, map.get(Direction.SOUTHWEST));
		assertEquals(Direction.SOUTHWEST, map.get(Direction.SOUTHEAST));
		assertEquals(Direction.SOUTHEAST, map.get(Direction.NORTHEAST));
	}

	@Test
	void rotate270() {
	
		Map<Direction, Direction> map = Compass.rotate270();
		assertEquals(Direction.WEST,  map.get(Direction.NORTH));
		assertEquals(Direction.NORTH, map.get(Direction.EAST));
		assertEquals(Direction.EAST,  map.get(Direction.SOUTH));
		assertEquals(Direction.SOUTH, map.get(Direction.WEST));
		
		assertEquals(Direction.NORTHEAST, map.get(Direction.SOUTHEAST));
		assertEquals(Direction.NORTHWEST, map.get(Direction.NORTHEAST));
		assertEquals(Direction.SOUTHWEST, map.get(Direction.NORTHWEST));
		assertEquals(Direction.SOUTHEAST, map.get(Direction.SOUTHWEST));
	}
	
	@Test
	void reflectHorizontal() {
		
		Map<Direction, Direction> map = Compass.reflectHorizontal();
		assertEquals(Direction.SOUTH,  map.get(Direction.NORTH));
		assertEquals(Direction.EAST, map.get(Direction.EAST));
		assertEquals(Direction.NORTH,  map.get(Direction.SOUTH));
		assertEquals(Direction.WEST, map.get(Direction.WEST));

		assertEquals(Direction.NORTHWEST, map.get(Direction.SOUTHWEST));
		assertEquals(Direction.NORTHEAST, map.get(Direction.SOUTHEAST));
		assertEquals(Direction.SOUTHWEST, map.get(Direction.NORTHWEST));
		assertEquals(Direction.SOUTHEAST, map.get(Direction.NORTHEAST));
	}
	
	@Test
	void reflectVertical() {
		
		Map<Direction, Direction> map = Compass.reflectVertical();
		assertEquals(Direction.NORTH,  map.get(Direction.NORTH));
		assertEquals(Direction.WEST, map.get(Direction.EAST));
		assertEquals(Direction.SOUTH,  map.get(Direction.SOUTH));
		assertEquals(Direction.EAST, map.get(Direction.WEST));		

		assertEquals(Direction.NORTHWEST, map.get(Direction.NORTHEAST));
		assertEquals(Direction.NORTHEAST, map.get(Direction.NORTHWEST));
		assertEquals(Direction.SOUTHWEST, map.get(Direction.SOUTHEAST));
		assertEquals(Direction.SOUTHEAST, map.get(Direction.SOUTHWEST));
	}
	
	@Test
	void cardinal() {
		
		assertTrue(Compass.isCardinal(Direction.NORTH));
		assertTrue(Compass.isCardinal(Direction.SOUTH));
		assertTrue(Compass.isCardinal(Direction.EAST));
		assertTrue(Compass.isCardinal(Direction.WEST));
		
		assertFalse(Compass.isCardinal(Direction.NORTHEAST));
		assertFalse(Compass.isCardinal(Direction.NORTHWEST));		
		assertFalse(Compass.isCardinal(Direction.SOUTHEAST));
		assertFalse(Compass.isCardinal(Direction.SOUTHWEST));
	}
}
