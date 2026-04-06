package experiments;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
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

		Board board = new Board(6,10);
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		Chooser chooser = new Chooser(80); // 12, 37 (35 partial), 60 (3x20)

		int moves = Pentomino.formPentominoes(board, chooser, environment);
		System.out.println("MOVES " + moves);
				
		 for ( int trial = 0; trial < 50000; trial++ ) {
			 
			Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
			 if ( !randomSwap(partitionMap, environment, chooser) ) break;
			 System.out.println(trial);
		 }
		
//		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
//		Pentomino.exchangeSquares(partitionMap, environment, 1, 7, new Chooser(2));

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
		
		Map<Integer, Set<Integer>> filtered = SolutionMap.filterGraph(graph, partitionMap, environment);
		
		List<Integer> fromOptions = new ArrayList<>();
		
		for ( PentominoType type: pentominoes.keySet()) {
			
			if ( pentominoes.get(type).size() > 1 )  fromOptions.addAll( pentominoes.get(type));
		}
		Integer a = chooser.randomFromList(fromOptions);
		
		Set<Integer> options = filtered.get(a);
		Set<Integer> x = pentominoes.get(PentominoType.X);
		if ( x != null && x.size() > 0 )  options.remove(x.iterator().next());

		//if ( (x == null || !x.contains(a)) && options.size() > 0) {
		if ( options.size() > 0 ) {
			
			Integer b = chooser.randomFromSet(options);
			System.out.println("swap " + a + " and " + b);
			if ( !Pentomino.exchangeSquares(partitionMap, environment, a, b, chooser) ) System.out.println("QQQQQQQQQQQQQQQQAAAA ");
		}
		else if ( pentominoes.keySet().size() == 11 ) {
			//notFinished = false;
		}
		

		return notFinished;
	}

}
