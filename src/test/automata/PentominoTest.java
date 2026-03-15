package test.automata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cakes.category.Maps;
import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import observe.SquareObserver;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import tiles.Tile;
import worlds.Board;
import worlds.BoardManager;
import xslt.Pipeline;

class PentominoTest {

	@Test
	void U() {
		
		Square square1 = new Square(); square1.setLabel("(3,0)");
		Square square2 = new Square(); square2.setLabel("(4,0)");
		Square square3 = new Square(); square3.setLabel("(4,1)");
		Square square4 = new Square(); square4.setLabel("(4,2)");
		Square square5 = new Square(); square5.setLabel("(3,2)");
		
		Tile.attach(square1, Direction.EAST, square2);
		Tile.attach(square2, Direction.SOUTH, square3);
		Tile.attach(square3, Direction.SOUTH, square4);
		Tile.attach(square4, Direction.WEST, square5);

		Set<Square> setA = new HashSet<Square>();
		setA.add(square1);	
		Map<Integer, Set<Square>> partitionMapA = Partitioner.partition(setA);
		assertEquals(1, partitionMapA.keySet().size());
		assertEquals(5, partitionMapA.get(1).size());
		
		Set<Square> tile = partitionMapA.get(1);
		PentominoType type = Pentomino.identifyPentomino(tile);
		assertEquals(PentominoType.U, type);
	}

	@Test
	void Y() {
		
		Square square1 = new Square();
		Square square2 = new Square();
		Square square3 = new Square();
		Square square4 = new Square();
		Square square5 = new Square();
		
		Tile.attach(square1, Direction.EAST, square2);
		Tile.attach(square2, Direction.EAST, square3);
		Tile.attach(square3, Direction.EAST, square4);
		Tile.attach(square3, Direction.NORTH, square5);

		Set<Square> setA = new HashSet<Square>();
		setA.add(square1);	
		Map<Integer, Set<Square>> partitionMapA = Partitioner.partition(setA);
		assertEquals(1, partitionMapA.keySet().size());
		assertEquals(5, partitionMapA.get(1).size());
		
		Set<Square> tile = partitionMapA.get(1);
		PentominoType type = Pentomino.identifyPentomino(tile);
		assertEquals(PentominoType.Y, type);
	}

	@Test	
	void Z() {
		
		Square square1 = new Square(); square1.setLabel("(0,0)");
		Square square2 = new Square(); square2.setLabel("(0,1)");
		Square square3 = new Square(); square3.setLabel("(1,1)");
		Square square4 = new Square(); square4.setLabel("(1,2)");
		Square square5 = new Square(); square5.setLabel("(2,2)");
		
		Tile.attach(square1, Direction.SOUTH, square2);
		Tile.attach(square2, Direction.EAST, square3);
		Tile.attach(square3, Direction.EAST, square4);
		Tile.attach(square4, Direction.SOUTH, square5);

		Set<Square> setA = new HashSet<Square>();
		setA.add(square1);	
		Map<Integer, Set<Square>> partitionMapA = Partitioner.partition(setA);
		assertEquals(1, partitionMapA.keySet().size());
		assertEquals(5, partitionMapA.get(1).size());
		
		Set<Square> tile = partitionMapA.get(1);
		PentominoType type = Pentomino.identifyPentomino(tile);
		assertEquals(PentominoType.Z, type);
	}
	
	@Test	
	void pair() {
		
		Board board = new Board(4,3);	
		Square[][] grid = board.getGrid();
		
		// a P tile
		Tile.attach(grid[0][0], Direction.EAST, grid[0][1]);
		Tile.attach(grid[0][1], Direction.EAST, grid[0][2]);
		Tile.attach(grid[0][0], Direction.SOUTH, grid[1][0]);
		Tile.attach(grid[0][1], Direction.SOUTH, grid[1][1]);
		
		Set<Square> tileA = Partitioner.getTileContaining(grid[0][0]);
		PentominoType typeA = Pentomino.identifyPentomino(tileA);
		assertEquals(PentominoType.P, typeA);

		// a Y tile
		Tile.attach(grid[2][0], Direction.EAST, grid[2][1]);
		Tile.attach(grid[2][1], Direction.EAST, grid[2][2]);
		Tile.attach(grid[2][2], Direction.EAST, grid[2][3]);
		Tile.attach(grid[2][2], Direction.NORTH, grid[1][2]);

		Set<Square> tileB = Partitioner.getTileContaining(grid[2][0]);
		PentominoType typeB = Pentomino.identifyPentomino(tileB);
		assertEquals(PentominoType.Y, typeB);
		
		Set<Integer> tiles = new HashSet<>();
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<Square, Set<Integer>> reverse = Maps.invertMap(partitionMap);
		tiles.addAll(reverse.get(grid[0][0])); tiles.addAll(reverse.get(grid[2][0]));
		Map<Square, Set<Sense>> contacts = SquareObserver.sense(partitionMap, BoardObserver.lookAbout(board), tiles);	
		// 6 square are in contact
		assertEquals(6, contacts.keySet().size());
		// square at 0,0 is not in contact
		assertNull(contacts.get(grid[0][0]));	
		// square at 1,0 is in contact with 1 square
		assertEquals(1, contacts.get(grid[1][0]).size());
		// square at 1,1 is in contact with 2 squares
		assertEquals(2, contacts.get(grid[1][1]).size());
		
		// square at 1,1 sees squares 1,2 and 2,1 EAST and SOUTH
		Set<Direction> directions = new HashSet<>();
		Set<Square> squares = new HashSet<>();
		
		for ( Sense sense: contacts.get(grid[1][1]) ) {
			
			squares.add(sense.getSquare());
			directions.add(sense.getDirection());
		}
		
		assertTrue( directions.contains(Direction.EAST) );
		assertTrue( directions.contains(Direction.SOUTH) );
		assertTrue( squares.contains(grid[1][2]) );
		assertTrue( squares.contains(grid[2][1]) );

		// square at 2,0 is detachable, square at 2,1 isn't
		assertTrue(Tile.isDetachable(grid[2][0]));
		assertFalse(Tile.isDetachable(grid[2][1]));
		
		// square at 0,0 is detachable
		assertTrue(Tile.isDetachable(grid[0][0]));
		
		// there are 4 detachable squares in the P tile and 3 in the Y tile
		Map<Integer, Set<Square>> detachableSquares = Pentomino.getDetachableSquares(partitionMap);
		assertEquals(4, detachableSquares.get(1).size());
		assertEquals(3, detachableSquares.get(2).size());
		
		Map<Square, Set<Square>> squareContacts = SquareObserver.getSensedSquaresBySquare(contacts);
		// square at 1,0 is in contact with 1 square (at 2,0)
		assertEquals(1, squareContacts.get(grid[1][0]).size());
		assertEquals(grid[2][0], squareContacts.get(grid[1][0]).iterator().next());
		// square at 1,1 is in contact with 2 squares (at 2,1 and 1,2)
		assertEquals(2, squareContacts.get(grid[1][1]).size());
		
		Map<Integer, Set<Square>> tileContacts = SquareObserver.getSensedSquaresByTile(contacts, partitionMap);
		// the P tile sees 3 squares of the Y tile
		assertEquals(3, tileContacts.get(1).size());
		assertTrue(tileContacts.get(1).contains(grid[2][0]));
		assertTrue(tileContacts.get(1).contains(grid[2][1]));
		assertTrue(tileContacts.get(1).contains(grid[1][2]));
		// the Y tile sees 3 squares of the P tile
		assertEquals(3, tileContacts.get(2).size());
		assertTrue(tileContacts.get(2).contains(grid[1][0]));
		assertTrue(tileContacts.get(2).contains(grid[1][1]));
		assertTrue(tileContacts.get(2).contains(grid[0][2]));

		Set<Square> contacts1 = tileContacts.get(1);
		// restrict to contacts in tile2
		contacts1.retainAll(partitionMap.get(2));
		// should still be 3 contacts
		assertEquals(3, contacts1.size());
		
		Map<Integer, Set<Set<Square>>> tileFragments = Pentomino.getDetachableFragments(contacts);
		// there are 6 detachable fragments in the P tile (square at 0,0 is detachable - but not a fragment because it has no contacts)
		assertEquals(6, tileFragments.get(1).size());
		assertTrue(Tile.isDetachable(grid[0][0]));
		// there are 3 detachable fragments in the Y tile (square at 3,2 is detachable - but not a fragment because it has no contacts)
		assertEquals(3, tileFragments.get(2).size());
		assertTrue(Tile.isDetachable(grid[2][3]));
		
		Pipeline p = new Pipeline();
		
		try {
			p.setOutput(new FileOutputStream("/D:/GitHub/automata/experiments/out.xml"));
			BoardManager.serialize(board, p.getContentHandler());

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}	

}
