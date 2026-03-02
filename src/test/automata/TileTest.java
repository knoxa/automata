package test.automata;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cells.Direction;
import cells.Square;
import orient.Partitioner;
import tiles.Tile;

public class TileTest {

	@Test
	void buidTile() {
		
		Square square1 = new Square();
		Square square2 = new Square();
		Square square3 = new Square();
		Square square4 = new Square();
		
		Tile.attach(square1, Direction.EAST, square2);
		assertEquals(1, square1.neighbourCount());
		assertEquals(1, square2.neighbourCount());
		assertEquals(true, square2.hasNeighbour(Direction.WEST));
		
		Tile.attach(square3, Direction.NORTH, square2);
		assertEquals(2, square2.neighbourCount());
		assertEquals(true, square2.hasNeighbour(Direction.SOUTH));
		
		Tile.attach(square4, Direction.EAST, square3); // should also link NORTH to square1	
		assertEquals(2, square4.neighbourCount());
		assertEquals(true, square4.hasNeighbour(Direction.NORTH));
		assertEquals(square1, square4.getNeighbour(Direction.NORTH));
		
		Tile.detach(square3);
		assertEquals(0, square3.neighbourCount());
		assertEquals(1, square2.neighbourCount());
		assertEquals(1, square4.neighbourCount());
		
		Tile.attach(square3, Direction.NORTH, square2); // should also link WEST to square4
		assertEquals(2, square3.neighbourCount());
		assertEquals(true, square3.hasNeighbour(Direction.WEST));
		assertEquals(square4, square3.getNeighbour(Direction.WEST));
	}
		
	@Test
	void tiles() {
		
		Square square1 = new Square(); square1.setLabel("(2,4)");
		Square square2 = new Square(); square2.setLabel("(3,4)");
		Square square3 = new Square(); square3.setLabel("(3,3)");
		Square square4 = new Square(); square4.setLabel("(4,3)");
		Square square5 = new Square(); square5.setLabel("(4,4)");
		
		// a 3-tile
		Tile.attach(square1, Direction.EAST, square2);
		Tile.attach(square5, Direction.WEST, square2);		

		Set<Square> setA = new HashSet<Square>();
		setA.add(square1);	
		Map<Integer, Set<Square>> partitionMapA = Partitioner.partition(setA);
		assertEquals(1,partitionMapA.keySet().size());
		assertEquals(3,partitionMapA.get(1).size());
		
		// a 2-tile
		Tile.attach(square3, Direction.EAST, square4);

		Set<Square> setB = new HashSet<Square>();
		setB.add(square3); setB.add(square4);
		Map<Integer, Set<Square>> partitionMapB = Partitioner.partition(setB);
		assertEquals(1,partitionMapB.keySet().size());
		assertEquals(2,partitionMapB.get(1).size());

		// attach the 3-tile to the 2-tile at (3,4)->(3,3). Should also link (4,4)->(4,3) (square5->square4)
		Tile.attach(square2, Direction.NORTH, square3);
		assertEquals(square3, square2.getNeighbour(Direction.NORTH));		
		assertEquals(square4, square5.getNeighbour(Direction.NORTH));
		
		// result is a 5-tile (recoverable from either setA or SetB);
		partitionMapA = Partitioner.partition(setA);
		assertEquals(1,partitionMapA.keySet().size());
		assertEquals(5,partitionMapA.get(1).size());
		partitionMapB = Partitioner.partition(setB);
		assertEquals(1,partitionMapB.keySet().size());
		assertEquals(5,partitionMapB.get(1).size());
		
		// TO DO - attaching 2 squares in same partition - get a collision, but no connections made
		
		// split off square4,square 5 as a 2-tile
		Set<Square> fragment = new HashSet<Square>();
		fragment.add(square4); fragment.add(square5);		
		Tile.detachTile(fragment);
		
		Set<Square> setX = new HashSet<Square>();
		setX.add(square1); // one of the three left behind
		
		partitionMapA = Partitioner.partition(setX);
		partitionMapB = Partitioner.partition(fragment);
		assertEquals(1,partitionMapA.keySet().size());
		assertEquals(3,partitionMapA.get(1).size());
		assertEquals(1,partitionMapB.keySet().size());
		assertEquals(2,partitionMapB.get(1).size());
	}
	
}
