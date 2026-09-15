package tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cells.Square;
import orient.Partitioner;
import worlds.BoardIdentifier;

public class PentominoIdentifier implements BoardIdentifier {

	@Override
	public String identifySolution(Square[][] grid) {
		
		int rows = grid.length;
		int cols = grid[0].length;
		
		StringBuffer bufferA = new StringBuffer();
		StringBuffer bufferB = new StringBuffer();
		StringBuffer bufferC = new StringBuffer();
		StringBuffer bufferD = new StringBuffer();
		
		for ( int col = 0; col < cols; col++ ) {
			
			for ( int row = 0; row < rows; row++ ) {
				
				bufferA.append(Pentomino.identifyPentomino(Partitioner.getTileContaining(grid[row][col])));
				bufferB.append(Pentomino.identifyPentomino(Partitioner.getTileContaining(grid[row][cols - col - 1])));
				bufferC.append(Pentomino.identifyPentomino(Partitioner.getTileContaining(grid[rows - row - 1][col])));
				bufferD.append(Pentomino.identifyPentomino(Partitioner.getTileContaining(grid[rows - row - 1][cols - col - 1])));
			}
			
			bufferA.append(' '); bufferB.append(' '); bufferC.append(' '); bufferD.append(' ');
		}
		
		List<String> list = new ArrayList<>();
		list.add(bufferA.toString()); list.add(bufferB.toString()); list.add(bufferC.toString()); list.add(bufferD.toString());
		Collections.sort(list);
		return list.get(0).trim();
	}

}
