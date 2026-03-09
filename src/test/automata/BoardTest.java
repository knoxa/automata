package test.automata;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import worlds.Board;

class BoardTest {

	@Test
	void board() {

		Board board = new Board(6,5);

		assertEquals(6, board.getWidth());
		assertEquals(5, board.getHeight());
		assertEquals(30, board.getSquares().size());
		
		Square[][] grid = board.getGrid();
		assertEquals(5, grid.length);
		assertEquals(6, grid[0].length);
	}

	@Test
	void look() {

		Board board = new Board(6,5);
		Square[][] grid = board.getGrid();
		
		Map<Square, Set<Sense>> visible = BoardObserver.lookAbout(board);

		// can only see EAST and SOUTH from 0,0
		Set<Sense> sensed = visible.get(grid[0][0]);
		Set<Direction> directions = new HashSet<Direction>();
		for (Sense sense: sensed )  directions.add(sense.getDirection());
		assertEquals(2, directions.size());
		assertTrue(directions.contains(Direction.EAST));
		assertTrue(directions.contains(Direction.SOUTH));

		// can only see WEST and SOUTH from 0,5
		sensed = visible.get(grid[0][5]);
		directions = new HashSet<Direction>();
		for (Sense sense: sensed )  directions.add(sense.getDirection());
		assertEquals(2, directions.size());
		assertTrue(directions.contains(Direction.WEST));
		assertTrue(directions.contains(Direction.SOUTH));

		// can only see WEST and NORTH from 4,5
		sensed = visible.get(grid[4][5]);
		directions = new HashSet<Direction>();
		for (Sense sense: sensed )  directions.add(sense.getDirection());
		assertEquals(2, directions.size());
		assertTrue(directions.contains(Direction.WEST));
		assertTrue(directions.contains(Direction.NORTH));

		// can only see EAST and NORTH from 4,0
		sensed = visible.get(grid[4][0]);
		directions = new HashSet<Direction>();
		for (Sense sense: sensed )  directions.add(sense.getDirection());
		assertEquals(2, directions.size());
		assertTrue(directions.contains(Direction.EAST));
		assertTrue(directions.contains(Direction.NORTH));
	}

}
