package tiles;

import java.util.Set;

import cells.Direction;
import cells.Square;
import orient.Partitioner;

public class PentominoMaker {


	public static Set<Square> getTileF() {
		
		Square square1 = new Square(); square1.setLabel("af");
		Square square2 = new Square(); square2.setLabel("bf");
		Square square3 = new Square(); square3.setLabel("cf");
		Square square4 = new Square(); square4.setLabel("df");
		Square square5 = new Square(); square5.setLabel("ef");
		
		Tile.attach(square1, Direction.EAST,  square2);
		Tile.attach(square2, Direction.SOUTH,  square3);
		Tile.attach(square3, Direction.EAST, square4);
		Tile.attach(square3, Direction.SOUTH, square5);

		return Partitioner.getTileContaining(square1);
	}

	

	public static Set<Square> getTileP() {
		
		Square square1 = new Square(); square1.setLabel("ap");
		Square square2 = new Square(); square2.setLabel("bp");
		Square square3 = new Square(); square3.setLabel("cp");
		Square square4 = new Square(); square4.setLabel("dp");
		Square square5 = new Square(); square5.setLabel("ep");
		
		Tile.attach(square1, Direction.EAST,  square2);
		Tile.attach(square2, Direction.EAST,  square3);
		Tile.attach(square1, Direction.SOUTH, square4);
		Tile.attach(square2, Direction.SOUTH, square5);

		return Partitioner.getTileContaining(square1);
	}

	
	public static Set<Square> getTileU() {
			
		Square square1 = new Square(); square1.setLabel("au");
		Square square2 = new Square(); square2.setLabel("bu");
		Square square3 = new Square(); square3.setLabel("cu");
		Square square4 = new Square(); square4.setLabel("du");
		Square square5 = new Square(); square5.setLabel("eu");
		
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
				
		Square square1 = new Square(); square1.setLabel("az");
		Square square2 = new Square(); square2.setLabel("bz");
		Square square3 = new Square(); square3.setLabel("cz");
		Square square4 = new Square(); square4.setLabel("dz");
		Square square5 = new Square(); square5.setLabel("ez");
		
		Tile.attach(square1, Direction.SOUTH, square2);
		Tile.attach(square2, Direction.EAST, square3);
		Tile.attach(square3, Direction.EAST, square4);
		Tile.attach(square4, Direction.SOUTH, square5);
		
		return Partitioner.getTileContaining(square1);
	}

}
