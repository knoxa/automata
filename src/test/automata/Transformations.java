package test.automata;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import orient.Chooser;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import tiles.Tile;
import worlds.Board;
import worlds.BoardManager;
import xslt.Pipeline;

class Transformations {
	
	@Test	
	void pair() {
		
		Board board = new Board(3,5);	
		Square[][] grid = board.getGrid();
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);

		// Y tile
		Tile.attach(grid[0][1], Direction.SOUTH, grid[1][1]);
		Tile.attach(grid[1][1], Direction.SOUTH, grid[2][1]);
		Tile.attach(grid[2][1], Direction.SOUTH, grid[3][1]);
		Tile.attach(grid[1][1], Direction.WEST,  grid[1][0]);
		
		// L tile
		Tile.attach(grid[1][2], Direction.SOUTH, grid[2][2]);
		Tile.attach(grid[2][2], Direction.SOUTH, grid[3][2]);
		Tile.attach(grid[3][2], Direction.SOUTH, grid[4][2]);
		Tile.attach(grid[4][2], Direction.WEST,  grid[4][1]);

		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		Integer tileY = pentominoes.get(PentominoType.Y).iterator().next();
		Integer tileL = pentominoes.get(PentominoType.L).iterator().next();
		Pentomino.transform(partitionMap, environment, tileY, tileL, new Chooser(1));

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
