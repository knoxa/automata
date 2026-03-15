package tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import act.TileAction;
import cakes.category.Maps;
import cells.Sense;
import cells.Square;
import orient.Chooser;
import orient.Partitioner;
import worlds.Board;

public class Pentomino {
	
	private static final int MAX_ITERATIONS = 2000;

	public static PentominoType identifyPentomino(Set<Square> tile) {
		
		PentominoType retval = null;
		
		Map<Integer, Integer> counts = Tile.countNeighbours(tile);
		int straightThree = Tile.straightThree(tile);
		int area = Tile.area(tile);
		
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

	public static boolean haveOnlyPentominoes(Map<Integer, Set<Square>> partitionMap) {
		
		boolean haveOnlyPentominoes = true;
		
		for ( Integer partition: partitionMap.keySet() ) {
			
			if ( partitionMap.get(partition).size() != 5 ) {
				
				haveOnlyPentominoes = false;
				break;
			}
		}
		
		return haveOnlyPentominoes;
	}

	public static Map<PentominoType, Set<Integer>> getPentominoes(Map<Integer, Set<Square>> partitionMap) {
		
		Map<PentominoType, Set<Integer>> pentominoMap = new HashMap<PentominoType, Set<Integer>>();
		
		for ( Integer partition: partitionMap.keySet() ) {
			
			Set<Square> tile = partitionMap.get(partition);
			
			if ( tile.size() == 5 ) {
				
				PentominoType p = identifyPentomino(tile);
				Maps.addMapValue(pentominoMap, p, partition);
			}
		}
		
		return pentominoMap;
	}
	
	
	public static int formPentominoes(Board board, Chooser chooser, Map<Square, Set<Sense>> environment) {
		
		int moves = 0;
		
		for ( int i = 0; i < MAX_ITERATIONS; i++ ) {
			
			// partition the board into tiles and get the tile sizes
			Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
			Map<Integer, Set<Integer>> sizeMap = Partitioner.collectBySize(partitionMap);

			// finished if we only have pentominoes
			if ( Pentomino.haveOnlyPentominoes(partitionMap) )  break;
			
			// otherwise, find the largest tile size
			List<Integer> sizes = new ArrayList<Integer>();
			sizes.addAll(sizeMap.keySet());
			Collections.sort(sizes);		
			int largestSize = sizes.get(sizes.size()-1);
			
			if ( largestSize > 5 ) {
				
				// dissolve one of the largest size tiles
				Square next = chooser.randomFromLargestPartion(partitionMap, sizeMap, sizes);
				TileAction.dissolve(next);
			}
			else {
				
				// a square from one of the smallest tiles defects (to its smallest neighbour)
				Integer p = chooser.randomSmallestPartion(sizeMap, sizes);
				TileAction.oneSquareDefects(partitionMap.get(p), environment, chooser);
			}

			moves++;
		}
		
		
		return moves;
	}

}
