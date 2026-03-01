package tiles;

import java.util.Map;
import java.util.Set;

import cells.Square;

public class Pentomino {

	public static PentominoType identifyPentomino(Set<Square> tile) {
		
		PentominoType retval = null;
		
		Map<Integer, Integer> counts = Tile.countNeighbours(tile);
		int straightThree = Tile.straightThree(tile);
		int area = tiles.Tile.area(tile);
		
		if ( counts.get(4) > 0 ) {
			
			retval = PentominoType.X;
		}
		else if (  straightThree == 0 ) {
			
			retval = PentominoType.W;
		}
		else if ( counts.get(3) == 1 && straightThree == 2 && area == 9 ) {
			
			retval = PentominoType.T;
		}
		else if ( counts.get(3) == 1 && straightThree == 2 && area == 8 ) {
			
			retval = PentominoType.Y;
		}
		else if ( counts.get(3) == 1 && counts.get(1) == 3  && straightThree == 1 ) {
			
			retval = PentominoType.F;
		}
		else if ( counts.get(3) == 1 && counts.get(1) == 1 ) {
			
			retval = PentominoType.P;
		}
		else if ( straightThree == 1 && counts.get(1) == 2 && area == 6 ) {
			
			retval = PentominoType.U;
		}
		else if ( straightThree == 1 && counts.get(1) == 2 && area == 9 ) {
			
			retval = PentominoType.Z;
		}
		else if ( straightThree == 1 && counts.get(1) == 2 && area == 8 ) {
			
			retval = PentominoType.N;
		}
		else if ( straightThree == 2 && counts.get(3) == 0 && area == 8 ) {
			
			retval = PentominoType.L;
		}
		else if ( straightThree == 2 && counts.get(3) == 0 && area == 9 ) {
			
			retval = PentominoType.V;
		}
		else if ( Tile.straightThree(tile) == 3 ) {
			
			retval = PentominoType.I;
		}
		else {
			
			System.err.println("unidentified tile");
		}
		
		return retval;
	}
}
