package world;

import java.util.HashSet;
import java.util.Set;

import cells.Square;

public class Board {

	private Square[][] grid;
	private int width, height;
	private Set<Square> squares;

	public Board(int width, int height) {

		this.width = width; this.height = height;
		grid = new Square[height][width];
		
		initialize();
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void initialize() {
		
		squares = new HashSet<Square>();
		
		for ( int col = 0; col < width; col++) {		
			
			for ( int row = 0; row < height; row++) {

				grid[row][col] = new Square();
				String label = String.format("(%d,%d)", col, row);
				grid[row][col].setLabel(label);
				squares.add(grid[row][col]);
			}
		}
	}

	public Set<Square> getSquares() {
		return squares;
	}

	public Square[][] getGrid() {
		return grid;
	}
	
}
