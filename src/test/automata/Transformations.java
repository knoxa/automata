package test.automata;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import observe.SquareObserver;
import orient.Chooser;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoMaker;
import tiles.PentominoType;
import tiles.Tile;
import worlds.Board;
import worlds.BoardManager;
import worlds.Compass;
import worlds.Plane;
import xslt.Pipeline;

class Transformations {
	
	@Test	
	void YL() {
		
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
		Pentomino.exchangeSquares(partitionMap, environment, tileY, tileL, new Chooser(1));
		
		// ...
	}
	
	@Test	
	void FP() {
		
		Set<Square> tileF = PentominoMaker.getTileF();
		Set<Square> tileP = PentominoMaker.getTileP();

		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		Tile.transform(tileP, Compass.rotate270());
		Tile.transform(tileP, Compass.reflectVertical());
		
		// position tile so that square1 is at 0,0
		Tile.position(tileF.iterator().next(), 0, 0, coordinates, positions);	
		Tile.position(tileP.iterator().next(), 0, 4, coordinates, positions);	

		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(coordinates.keySet());
		Map<Square, Set<Sense>> environment = SquareObserver.sense(coordinates, positions);
		Pentomino.exchangeSquares(partitionMap, environment, 1, 2, new Chooser(2));

		Pipeline p = new Pipeline();
		
		try {
			p.setOutput(new FileOutputStream("/D:/GitHub/automata/experiments/out.xml"));
			Plane.serialize(coordinates, positions, p.getContentHandler());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
