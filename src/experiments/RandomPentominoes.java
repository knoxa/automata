package experiments;

import java.io.FileOutputStream;
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

public class RandomPentominoes {

	public static void main(String[] args) {

		
		Board board = new Board(6,10);
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		
		for ( int trial = 2000; trial < 10000; trial++ ) {
			
			board.clear();
			Chooser chooser = new Chooser(trial);

			int moves = Pentomino.formPentominoes(board, chooser, environment);
			System.out.println("Trial: " + trial);
			System.out.println("MOVES " + moves);

			Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
			Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
			System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
			System.out.println("---");
			if ( pentominoes.keySet().size() > 9 ) break;
		}
			
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		

		Pipeline p = new Pipeline();
		try {
			p.setOutput(new FileOutputStream("experiments/out.xml"));
			BoardManager.serialize(board, p.getContentHandler());

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
