package test.automata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import act.Action;
import cakes.category.Maps;
import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import observe.SquareObserver;
import orient.Chooser;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import tiles.Tile;
import worlds.Board;
import worlds.BoardManager;

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
	void pair() throws FileNotFoundException {
		
		Board board = new Board(4,3);	
		Square[][] grid = board.getGrid();
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		
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
		Set<Square> p = Partitioner.getTileContaining(grid[0][0]);
		assertEquals(4, Tile.getDetachableSquares(p).size());
		Set<Square> y = Partitioner.getTileContaining(grid[2][0]);
		assertEquals(3, Tile.getDetachableSquares(y).size());
		
		Map<PentominoType, Integer> lookupByType = new HashMap<>();
		
		for ( Integer partNo: partitionMap.keySet() ) {
			
			lookupByType.put(Pentomino.identifyPentomino(partitionMap.get(partNo)), partNo);
		}
		
		Map<Square, Set<Square>> squareContacts = SquareObserver.getSensedSquaresBySquare(contacts);
		// square at 1,0 is in contact with 1 square (at 2,0)
		assertEquals(1, squareContacts.get(grid[1][0]).size());
		assertEquals(grid[2][0], squareContacts.get(grid[1][0]).iterator().next());
		// square at 1,1 is in contact with 2 squares (at 2,1 and 1,2)
		assertEquals(2, squareContacts.get(grid[1][1]).size());
		
		Map<Integer, Set<Square>> tileContacts = SquareObserver.getSensedSquaresByTile(contacts, partitionMap);
		// the P tile sees 3 squares of the Y tile
		int pNo = lookupByType.get(PentominoType.P); int yNo = lookupByType.get(PentominoType.Y);
		assertEquals(3, tileContacts.get(pNo).size());
		assertTrue(tileContacts.get(pNo).contains(grid[2][0]));
		assertTrue(tileContacts.get(pNo).contains(grid[2][1]));
		assertTrue(tileContacts.get(pNo).contains(grid[1][2]));
		// the Y tile sees 3 squares of the P tile
		assertEquals(3, tileContacts.get(yNo).size());
		assertTrue(tileContacts.get(yNo).contains(grid[1][0]));
		assertTrue(tileContacts.get(yNo).contains(grid[1][1]));
		assertTrue(tileContacts.get(yNo).contains(grid[0][2]));

		Set<Square> contacts1 = tileContacts.get(pNo);
		// restrict to contacts in tile2
		contacts1.retainAll(partitionMap.get(yNo));
		// should still be 3 contacts
		assertEquals(3, contacts1.size());
		Set<Square> contacts2 = tileContacts.get(yNo);
		// restrict to contacts in tile1
		contacts2.retainAll(partitionMap.get(pNo));
		// should still be 3 contacts
		assertEquals(3, contacts2.size());
		
		// only 1 of the squares in the P tile is in contact with more that one square in the Y tile
		Set<Square> contacts1a = SquareObserver.moreThanOneContact(contacts1, squareContacts);
		assertEquals(1, contacts1a.size());	
		// only 1 of the squares in the Y tile is in contact with more that one square in the P tile
		Set<Square> contacts2a = SquareObserver.moreThanOneContact(contacts2, squareContacts);
		assertEquals(1, contacts2a.size());
		// each of the squares "sees" the other
		assertTrue(squareContacts.get(contacts1a.iterator().next()).containsAll(contacts2a));
		assertTrue(squareContacts.get(contacts2a.iterator().next()).containsAll(contacts1a));
		
		Map<Integer, Set<Set<Square>>> tileFragments = Pentomino.getDetachableFragments(contacts);
		// there are 6 detachable fragments in the P tile (square at 0,0 is detachable - but not a fragment because it has no contacts)
		assertEquals(6, tileFragments.get(pNo).size());
		assertTrue(Tile.isDetachable(grid[0][0]));
		// there are 3 detachable fragments in the Y tile (square at 3,2 is detachable - but not a fragment because it has no contacts)
		assertEquals(3, tileFragments.get(yNo).size());
		assertTrue(Tile.isDetachable(grid[2][3]));
		
		List<Set<Set<Square>>> options = Pentomino.getPossibleSwaps(contacts);
		// there are 5 options for swapping fragments between P and Y
		assertEquals(5, options.size());

		// one of the options should be to swap (2,0) and (0,2)
		Set<Square> pSet = new HashSet<>(); pSet.add(grid[0][2]);
		Set<Square> ySet = new HashSet<>(); ySet.add(grid[2][0]);
		Set<Set<Square>> anOption = new HashSet<>(); anOption.add(pSet); anOption.add(ySet);
		assertTrue(options.contains(anOption));

		List<Action> actions = Pentomino.getSwapActions(anOption, contacts);
		
		// make moves - result is a P and a T
		Action.makeMoves(actions);

		partitionMap = Partitioner.partition(board.getSquares());
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		assertTrue(pentominoes.keySet().contains(PentominoType.P));
		assertTrue(pentominoes.keySet().contains(PentominoType.T));
		
		// random mutation (with seed = 2) - creates [P, Z]
		Integer tileP = pentominoes.get(PentominoType.P).iterator().next();
		Integer tileT = pentominoes.get(PentominoType.T).iterator().next();
		Pentomino.transform(partitionMap, environment, tileP, tileT, new Chooser(2));
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		assertTrue(pentominoes.keySet().contains(PentominoType.P));
		assertTrue(pentominoes.keySet().contains(PentominoType.Z));
		
		BoardManager.writeBoardToFile(board, new FileOutputStream("/D:/GitHub/automata/experiments/out.xml"));
	}	

}
