package test.automata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import act.Action;
import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.SquareObserver;
import orient.Partitioner;
import tiles.Tile;

public class TileTest {

	@Before
	public void setUp() throws Exception {
		
	}

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
		Action.makeMoves(Tile.detachTileActions(fragment));
		
		Set<Square> setX = new HashSet<Square>();
		setX.add(square1); // one of the three left behind
		
		partitionMapA = Partitioner.partition(setX);
		partitionMapB = Partitioner.partition(fragment);
		assertEquals(1,partitionMapA.keySet().size());
		assertEquals(3,partitionMapA.get(1).size());
		assertEquals(1,partitionMapB.keySet().size());
		assertEquals(2,partitionMapB.get(1).size());
	}
	
	@Test
	void position() {
		
		Square square1 = new Square();
		Square square2 = new Square();
		Square square3 = new Square();
		Square square4 = new Square();
		Square square5 = new Square();
		Square square6 = new Square();
				
		Tile.attach(square1, Direction.EAST, square2);
		Tile.attach(square2, Direction.NORTH, square3);
		
		Tile.attach(square4, Direction.WEST, square5);
		Tile.attach(square5, Direction.SOUTH, square6);
	
		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		// position tile so that square1 is at 0,0
		Tile.position(square1, 0, 0, coordinates, positions);	
		// then square3 = 1,-1
		Integer[] coords = coordinates.get(square3);
		assertEquals(1, (int) coords[0]);
		assertEquals(-1, (int) coords[1]);
		
		// position tile so that square6 is at 0,0
		Tile.position(square6, 0, 0, coordinates, positions);
		// then square4 = 1,-1
		coords = coordinates.get(square4);
		assertEquals(1, (int) coords[0]);
		assertEquals(-1, (int) coords[1]);

		// the two tiles overlap at 0,0 and 1,-1
		Set<Square> tile = positions.get(0).get(0);
		assertEquals(2, tile.size());
		assertTrue(tile.contains(square1));
		assertTrue(tile.contains(square6));	
		tile = positions.get(-1).get(1);
		assertEquals(2, tile.size());
		assertTrue(tile.contains(square3));
		assertTrue(tile.contains(square4));
	}
	
	@Test
	void sense() {
		
		Square square1 = new Square(); square1.setLabel("1");
		Square square2 = new Square(); square2.setLabel("2");
		Square square3 = new Square(); square3.setLabel("3");
		Square square4 = new Square(); square4.setLabel("4");
		Square square5 = new Square(); square5.setLabel("5");
		Square square6 = new Square(); square6.setLabel("6");
				
		Tile.attach(square1, Direction.EAST, square2);
		Tile.attach(square2, Direction.NORTH, square3);
		
		Tile.attach(square4, Direction.WEST, square5);
		Tile.attach(square5, Direction.SOUTH, square6);
	
		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		// position tiles so that square5 is at 0,0 and square2 is at 2,1
		Tile.position(square5, 0, 0, coordinates, positions);
		Tile.position(square2, 2, 1, coordinates, positions);
		
		// square1 should now have square6 west and square4 north; square3 should have square4 west
		Map<Square, Set<Sense>> observations = SquareObserver.sense(coordinates, positions);
		assertEquals(2, observations.keySet().size());
		Set<Sense> sensed = observations.get(square1);
		assertEquals(2, sensed.size());
		Set<Square> sensedSquares = new HashSet<Square>();
		for ( Sense sense: sensed ) sensedSquares.add(sense.getSquare());
		assertEquals(2, sensedSquares.size());
		assertTrue(sensedSquares.contains(square6));
		assertTrue(sensedSquares.contains(square4));
		sensed = observations.get(square3);
		assertEquals(1, sensed.size());
		sensedSquares = new HashSet<Square>();
		for ( Sense sense: sensed ) sensedSquares.add(sense.getSquare());
		assertEquals(1, sensedSquares.size());
		assertTrue(sensedSquares.contains(square4));
	}	
}
