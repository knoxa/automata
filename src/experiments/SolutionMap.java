package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import observe.SquareObserver;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import worlds.Board;
import worlds.BoardManager;

public class SolutionMap {

	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {

		Board board = BoardManager.loadFromXml(new FileInputStream("/D:/GitHub/automata/experiments/solution14.xml"));
		
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);

		Map<Integer, Set<Integer>> graph = Partitioner.getPartitionGraph(partitionMap, environment);
		System.out.println(graph);
		
		link(1, 2, partitionMap, environment);
		link(1, 3, partitionMap, environment);
		link(1, 4, partitionMap, environment);
		link(1, 8, partitionMap, environment);
		link(1, 10, partitionMap, environment);
		
	}
	
	
	public static void link(int a, int b, Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment) {
		
		HashSet<Integer> tiles = new HashSet<Integer>();
		tiles.add(a); tiles.add(b);
		
		Map<Square, Set<Sense>> contacts = SquareObserver.sense(partitionMap, environment, tiles);
		List<Set<Set<Square>>> possibleMoves = Pentomino.getPossibleSwaps(contacts);
		System.out.println(a + "," + b + ": " + possibleMoves.size());
	}

}
