package test.automata;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import act.Action;
import cells.Direction;
import cells.Square;

public class ActionTest {

	@Test
	void buidTile() {
		
		Square square1 = new Square();
		Square square2 = new Square();
		Square square3 = new Square();
		Square square4 = new Square();
		
		Action.attach(square1, Direction.EAST, square2);
		assertEquals(1, square1.neighbourCount());
		assertEquals(1, square2.neighbourCount());
		assertEquals(true, square2.hasNeighbour(Direction.WEST));
		
		Action.attach(square3, Direction.NORTH, square2);
		assertEquals(2, square2.neighbourCount());
		assertEquals(true, square2.hasNeighbour(Direction.SOUTH));
		
		Action.attach(square4, Direction.EAST, square3); // should also link NORTH to square1	
		assertEquals(2, square4.neighbourCount());
		assertEquals(true, square4.hasNeighbour(Direction.NORTH));
		assertEquals(square1, square4.getNeighbour(Direction.NORTH));
		
		Action.detach(square3);
		assertEquals(0, square3.neighbourCount());
		assertEquals(1, square2.neighbourCount());
		assertEquals(1, square4.neighbourCount());
		
		Action.attach(square3, Direction.NORTH, square2); // should also link WEST to square4
		assertEquals(2, square3.neighbourCount());
		assertEquals(true, square3.hasNeighbour(Direction.WEST));
		assertEquals(square4, square3.getNeighbour(Direction.WEST));
	}
	
	@Test
	void tiles() {
		
		Square square1 = new Square(); square1.setLabel("1");
		Square square2 = new Square(); square2.setLabel("2");
		Square square3 = new Square(); square3.setLabel("3");
		Square square4 = new Square(); square4.setLabel("4");
		
		Action.attach(square1, Direction.EAST, square2);
		Action.attach(square3, Direction.EAST, square4);

		Action.attach(square2, Direction.SOUTH, square4);
		
		// TO DO - diagonal attachments only work for connecting squares - not other square in the tiles
		// TO DO - no check if attachment between tiles causes an overlap
		// Are these thing responsibility of Action? Perhaps need Tile actions vice Square actions?
		
		//System.out.println(square1);
		//System.out.println(square2);
		//System.out.println(square3);
		//System.out.println(square4);
	
	}
	
}
