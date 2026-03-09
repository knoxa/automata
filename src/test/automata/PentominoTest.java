package test.automata;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cells.Direction;
import cells.Square;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import tiles.Tile;

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

}
