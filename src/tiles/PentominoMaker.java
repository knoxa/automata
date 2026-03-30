package tiles;

import java.util.Set;

import cells.Direction;
import cells.Square;
import orient.Partitioner;

public class PentominoMaker {


	public static Set<Square> getTileP() {
		
		Square square1 = new Square(); square1.setLabel("(0,0)");
		Square square2 = new Square(); square2.setLabel("(0,1)");
		Square square3 = new Square(); square3.setLabel("(0,2)");
		Square square4 = new Square(); square4.setLabel("(1,0)");
		Square square5 = new Square(); square5.setLabel("(1,1)");
		
		Tile.attach(square1, Direction.EAST,  square2);
		Tile.attach(square2, Direction.EAST,  square3);
		Tile.attach(square1, Direction.SOUTH, square4);
		Tile.attach(square2, Direction.SOUTH, square5);

		return Partitioner.getTileContaining(square1);
	}

	
	public static Set<Square> getTileU() {
			
		Square square1 = new Square(); square1.setLabel("(3,0)");
		Square square2 = new Square(); square2.setLabel("(4,0)");
		Square square3 = new Square(); square3.setLabel("(4,1)");
		Square square4 = new Square(); square4.setLabel("(4,2)");
		Square square5 = new Square(); square5.setLabel("(3,2)");
		
		Tile.attach(square1, Direction.EAST, square2);
		Tile.attach(square2, Direction.SOUTH, square3);
		Tile.attach(square3, Direction.SOUTH, square4);
		Tile.attach(square4, Direction.WEST, square5);
		
		return Partitioner.getTileContaining(square1);
	}

	
	public static Set<Square> getTileX() {
			
		Square square1 = new Square();
		Square square2 = new Square();
		Square square3 = new Square();
		Square square4 = new Square();
		Square square5 = new Square();
		
		Tile.attach(square1, Direction.NORTH, square2);
		Tile.attach(square1, Direction.SOUTH, square3);
		Tile.attach(square1, Direction.EAST, square4);
		Tile.attach(square1, Direction.WEST, square5);
		
		return Partitioner.getTileContaining(square1);
	}

	
	public static Set<Square> getTileY() {
				
		Square square1 = new Square();
		Square square2 = new Square();
		Square square3 = new Square();
		Square square4 = new Square();
		Square square5 = new Square();
		
		Tile.attach(square1, Direction.EAST, square2);
		Tile.attach(square2, Direction.EAST, square3);
		Tile.attach(square3, Direction.EAST, square4);
		Tile.attach(square3, Direction.NORTH, square5);
		
		return Partitioner.getTileContaining(square1);
	}

	
	public static Set<Square> getTileZ() {
				
		Square square1 = new Square(); square1.setLabel("(0,0)");
		Square square2 = new Square(); square2.setLabel("(0,1)");
		Square square3 = new Square(); square3.setLabel("(1,1)");
		Square square4 = new Square(); square4.setLabel("(1,2)");
		Square square5 = new Square(); square5.setLabel("(2,2)");
		
		Tile.attach(square1, Direction.SOUTH, square2);
		Tile.attach(square2, Direction.EAST, square3);
		Tile.attach(square3, Direction.EAST, square4);
		Tile.attach(square4, Direction.SOUTH, square5);
		
		return Partitioner.getTileContaining(square1);
	}

}
