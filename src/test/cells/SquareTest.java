package test.cells;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

import cells.Direction;
import cells.Square;

class SquareTest {

	@Test
	void neighbours() {
		
		Square square1 = new Square();
		Square square2 = new Square();
		Square square3 = new Square();
		
		square2.setNeighbour(Direction.WEST, square1);		
		assertEquals(1, square2.neighbourCount());
		
		Set<Square> neighbours = square2.getNeighbours();
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(square1));		
		assertEquals(square1, square2.getNeighbour(Direction.WEST));
		
		square2.setNeighbour(Direction.NORTH, square3);		
		assertEquals(2, square2.neighbourCount());
		neighbours = square2.getNeighbours();
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(square1));		
		assertTrue(neighbours.contains(square3));		
		
		square2.clearNeighbour(Direction.WEST);
		assertEquals(1, square2.neighbourCount());
		neighbours = square2.getNeighbours();
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(square3));		
		assertEquals(square3, square2.getNeighbour(Direction.NORTH));
		
	}

}
