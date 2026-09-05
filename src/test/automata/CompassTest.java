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
	}

	@Test
	void offset() {

		Integer forward = 1, backward = -1, stationary = 0;
		
		assertEquals(forward,    Compass.getOffsetX(Direction.EAST));
		assertEquals(backward,   Compass.getOffsetX(Direction.WEST));
		assertEquals(stationary, Compass.getOffsetX(Direction.NORTH));
		assertEquals(stationary, Compass.getOffsetX(Direction.SOUTH));

		assertEquals(stationary, Compass.getOffsetY(Direction.EAST));
		assertEquals(stationary, Compass.getOffsetY(Direction.WEST));
		assertEquals(backward,   Compass.getOffsetY(Direction.NORTH));
		assertEquals(forward,    Compass.getOffsetY(Direction.SOUTH));
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
	}

	@Test
	void rotate90() {
	
		Map<Direction, Direction> map = Compass.rotate90();
		assertEquals(Direction.EAST,  map.get(Direction.NORTH));
		assertEquals(Direction.SOUTH, map.get(Direction.EAST));
		assertEquals(Direction.WEST,  map.get(Direction.SOUTH));
		assertEquals(Direction.NORTH, map.get(Direction.WEST));
	}

	@Test
	void rotate270() {
	
		Map<Direction, Direction> map = Compass.rotate270();
		assertEquals(Direction.WEST,  map.get(Direction.NORTH));
		assertEquals(Direction.NORTH, map.get(Direction.EAST));
		assertEquals(Direction.EAST,  map.get(Direction.SOUTH));
		assertEquals(Direction.SOUTH, map.get(Direction.WEST));
	}
	
	@Test
	void reflectHorizontal() {
		
		Map<Direction, Direction> map = Compass.reflectHorizontal();
		assertEquals(Direction.SOUTH,  map.get(Direction.NORTH));
		assertEquals(Direction.EAST, map.get(Direction.EAST));
		assertEquals(Direction.NORTH,  map.get(Direction.SOUTH));
		assertEquals(Direction.WEST, map.get(Direction.WEST));
	}
	
	@Test
	void reflectVertical() {
		
		Map<Direction, Direction> map = Compass.reflectVertical();
		assertEquals(Direction.NORTH,  map.get(Direction.NORTH));
		assertEquals(Direction.WEST, map.get(Direction.EAST));
		assertEquals(Direction.SOUTH,  map.get(Direction.SOUTH));
		assertEquals(Direction.EAST, map.get(Direction.WEST));		
	}
}
