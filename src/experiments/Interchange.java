package experiments;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import orient.Chooser;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import worlds.Board;
import worlds.BoardManager;
import xslt.Pipeline;

public class Interchange {

	public static void main(String[] args) {

		// Find random proper pentomino puzzle solutions solutions by creating a degenerate solution,
		// then transforming pairs of tiles to different tiles that cover the same ground until there are 12 distinct tile types.
		
		// Could parameterise the board size, random number seed, max iterations to try, and output file.
		// A degenerate solution will always be found in the first stage. A proper solution is not necessarily found in the second stage.
		
		Board board = new Board(5,12);
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		Chooser chooser = new Chooser(112);

		int moves = Pentomino.formPentominoes(board, chooser, environment);
		System.out.println("pentominoes formed in " + moves + " moves");
				
		 for ( int trial = 1; trial <= 20000; trial++ ) {
			 
			Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
			 if ( !randomSwap(partitionMap, environment, chooser) ) break;
			 System.out.println(trial + " exchanges");
		 }

		Pipeline p = new Pipeline();
		try {
			p.setOutput(new FileOutputStream("experiments/out.xml"));
			BoardManager.serialize(board, p.getContentHandler());

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static boolean randomSwap(Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment, Chooser chooser) {
		
		boolean notFinished = true;
		
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		if ( pentominoes.keySet().size() > 11 ) {
			
			System.out.println("SOLUTION "  + pentominoes.keySet().size());
			return false;
		}
		System.out.println("---");
		Set<PentominoType> missing = EnumSet.allOf(PentominoType.class);missing.removeAll(pentominoes.keySet());
		System.out.println(pentominoes);
		System.out.println("missing: " + missing);
		Map<Integer, Set<Integer>> graph = Partitioner.getPartitionGraph(partitionMap, environment);
		System.out.println(graph);
		
		// filter graph to just keep edges between tiles that can legally exchange squares.
		Map<Integer, Set<Integer>> filtered = SolutionMap.filterGraph(graph, partitionMap, environment);
		
		// choose the first of a pair of tiles to exchange ...
		List<Integer> fromOptions = new ArrayList<>();
		
		for ( PentominoType type: pentominoes.keySet()) {
			
			// list all the tiles whose type appears more than once.
			if ( pentominoes.get(type).size() > 1 )  fromOptions.addAll(pentominoes.get(type));
		}
		
		// in random order ...
		Collections.shuffle(fromOptions, chooser.getRandom());
		Iterator<Integer> iter = fromOptions.iterator();
		
		while ( iter.hasNext() ) {
			
			// pick the first tile
			Integer a = iter.next();
			
			// find neighbours (in filtered graph) that aren't X
			Set<Integer> options = filtered.get(a);
			Set<Integer> x = pentominoes.get(PentominoType.X);
			if ( x != null && x.size() > 0 )  options.remove(x.iterator().next());

			if ( options.size() > 0 ) {
				
				// pick the second tile, and exchange the two
				Integer b = chooser.randomFromSet(options);
				System.out.println("swap " + a + " and " + b);
				Pentomino.exchangeSquares(partitionMap, environment, a, b, chooser);
				notFinished = true;
				// stop if we've swapped 2 tiles (otherwise, go on to select another pair)
				break;
			}
			else {
				// can't find a tile that can exchange squares with the first one
				System.out.println("no options for tile " + a);
				// continue with the loop, but indicate we're finished unless there's another tile in "fromOptions" that can exchange squares
				notFinished = false;
			}
		}
		
		return notFinished;
	}

}
