package experiments;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import act.Act;
import act.Action;
import act.TileAction;
import cakes.category.Maps;
import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import observe.SquareObserver;
import orient.Chooser;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import tiles.Tile;
import worlds.Board;
import worlds.BoardManager;
import worlds.Plane;
import xslt.Pipeline;


public class TileBuilder {
	
	
	public static void main(String[] args) {

		fillBoard(args);
	}
	
	
	public static void fillBoard(String[] args) {

		Board board = new Board(6,10);		
		
		Chooser chooser = new Chooser(9);
			
		for ( int trial = 0; trial < 50000; trial++ ) {
			
			board.clear();
			fillBoardWithPentominoes(board, chooser);

			System.out.println("Trial: " + trial);
			Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
			Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
			System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
			System.out.println("---");
			if ( pentominoes.keySet().size() > 9 ) {
				
				Set<PentominoType> missing = EnumSet.allOf(PentominoType.class);missing.removeAll(pentominoes.keySet());
				System.out.println("missing: " + missing);
				break;
			}
		}

		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);

/*
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);

		Map<Integer, Set<Integer>> graph = Partitioner.getPartitionGraph(partitionMap, environment);
		System.out.println(graph);
		
		Square displaced = TileAction.displaceSquare(partitionMap.get(10), environment, chooser, new HashSet<Square>());
		Pentomino.chaseTheAce(displaced, partitionMap.get(10), environment, new Chooser(5));
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		
		displaced = TileAction.displaceSquare(partitionMap.get(12), environment, chooser, new HashSet<Square>());
		Pentomino.chaseTheAce(displaced, partitionMap.get(12), environment, new Chooser(1));
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		
		Pentomino.mutate(partitionMap, environment, 1, 4, new Chooser(1));
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		
		Pentomino.mutate(partitionMap, environment, 1, 4, new Chooser(5));
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);

		Set<Square> locality = new HashSet<Square>();
		locality.addAll(partitionMap.get(2)); locality.addAll(partitionMap.get(3)); locality.addAll(partitionMap.get(6)); locality.addAll(partitionMap.get(7));
		Map<Square, Set<Sense>> localEnv = SquareObserver.restrictEnvironment(environment, locality);
		
		displaced = TileAction.displaceSquare(partitionMap.get(6), localEnv, chooser, new HashSet<Square>());
		Pentomino.chaseTheAce(displaced, partitionMap.get(6), localEnv, new Chooser(4));	
		Pentomino.formPentominoes(board, chooser, localEnv);
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		
		Chooser cx = new Chooser(101); cx.getRandom().nextInt();
		Pentomino.mutate(partitionMap, environment, 2, 3, cx);
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		
		Pentomino.mutate(partitionMap, environment, 6, 7, new Chooser(2));
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		
		cx = new Chooser(103); cx.getRandom().nextInt(); cx.getRandom().nextInt();
		Pentomino.mutate(partitionMap, environment, 6, 3, cx);
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		
		Pentomino.mutate(partitionMap, environment, 6, 7, new Chooser(3));
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
		
		Pentomino.mutate(partitionMap, environment, 6, 3, new Chooser(3));
		
		partitionMap = Partitioner.partition(board.getSquares());
		pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);
		System.out.println("distinct pentominoes: " + pentominoes.keySet().size());
		BoardManager.reportPartitions(partitionMap, board);
*/
		Pipeline p = new Pipeline();

		try {
			p.setOutput(new FileOutputStream("experiments/out.xml"));
			BoardManager.serialize(board, p.getContentHandler());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public static void freeTiles(String[] args) {

		Chooser chooser = new Chooser(1);
		Square seed = null;
		
		Map<PentominoType, Integer> counts = new EnumMap<>(PentominoType.class);
		
		for ( int x = 0; x < 20000; x++ ) {
			
			seed = new Square();
			Set<Square> tile = new HashSet<>(); tile.add(seed);

			for ( int i = 0; i < 4; i++) {
				
				Map<Square, Set<Sense>> environment = getFreeEnvironment(tile);
				List<Action> possibleActions = getPossibleActions(tile, environment);	
				Set<Action> choice = new HashSet<>();		
				choice.add(chooser.randomFromList(possibleActions));
				Action.makeMoves(choice);
				tile = Partitioner.getTileContaining(seed);
			}
			
			PentominoType type = Pentomino.identifyPentomino(tile);
			Integer count = counts.get(type);
			if ( count == null )  count = new Integer(0);
			counts.put(type, ++count);
		}
		
		System.out.println(counts);
			
		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		Tile.position(seed, 0, 0, coordinates, positions);

		Pipeline p = new Pipeline();
		try {
			p.setOutput(new FileOutputStream("experiments/out.xml"));
			Plane.serialize(coordinates, positions, p.getContentHandler());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<Square, Set<Sense>> getFreeEnvironment(Set<Square> tile) {
		
		// surround the tile with squares it can see
		
		Map<Square, Set<Sense>> environment = new HashMap<>();
		
		for ( Square square: tile ) {
			
			for ( Direction direction: Direction.values() ) {
				
				if ( !square.hasNeighbour(direction) ) {
					
					Square neighbour = new Square();
					Sense sense = new Sense(direction, neighbour);
					Maps.addMapValue(environment, square, sense);
				}
			}
		}
		
		return environment;
	}
	
	
	public static List<Action> getPossibleActions(Set<Square> tile, Map<Square, Set<Sense>> environment) {
		
		List<Action> possibleActions = new ArrayList<>();
		
		for ( Square square: tile ) {
			
			for ( Sense sense: environment.get(square) ) {
				
				Action action = new Action(square, Act.ATTACH, sense);
				possibleActions.add(action);
			}
		}
		
		return possibleActions;
	}
	
	public static Set<Square> placePentomino(Board board, Square seed, Map<Square, Set<Sense>> environment, Chooser chooser) {
		
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Set<Square> locality = new HashSet<>();
		
		for ( Integer partNo: partitionMap.keySet() ) { 
			
			if ( partitionMap.get(partNo).size() == 1 )  locality.addAll(partitionMap.get(partNo));
		}
		
		Map<Square, Set<Sense>> env = SquareObserver.restrictEnvironment(environment, locality);
		
		Set<Square> tile = Partitioner.getTileContaining(seed);
		
		for ( int i = 0; i < 4; i++) {
			
			List<Action> possibleActions = getPossibleActions(tile, SquareObserver.sensedByTile(env, tile));	
			Set<Action> choice = new HashSet<>();		
			choice.add(chooser.randomFromList(possibleActions));
			Action.makeMoves(choice);
			tile = Partitioner.getTileContaining(seed);
		}
		
		return tile;
	}
	
	
	public static boolean isBoardValid(Board board) {
			
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Set<Square> locality = new HashSet<>();
		
		for ( Integer partNo: partitionMap.keySet() ) { 
			
			if ( partitionMap.get(partNo).size() == 1 )  locality.addAll(partitionMap.get(partNo));
		}
		
		Map<Square, Set<Sense>> environment = SquareObserver.restrictEnvironment(BoardObserver.lookAbout(board), locality);
		Map<Square, Set<Square>> contacts = SquareObserver.getSensedSquaresBySquare(environment);
		return partitionEnvironment(contacts);
	}
	
	
	public static boolean partitionEnvironment(Map<Square, Set<Square>> contacts) {
		
		Set<Square> work = new HashSet<>();
		work.addAll(contacts.keySet());
		
		boolean isValid = true;
		
		while ( work.size() > 0 ) {
			
			Set<Square> partition = new HashSet<Square>();			
			Square nextSquare = work.iterator().next();
			partition.add(nextSquare);
			
			extendPartitionX(nextSquare, partition, contacts);
			work.removeAll(partition);

			if ( partition.size() % 5 != 0 ) isValid = false;
		}

		return isValid;
	}
	
		
	private static void extendPartitionX(Square square, Set<Square> partition, Map<Square, Set<Square>> contacts) {
		
		Set<Square> neighbours = contacts.get(square);
		if ( neighbours.size() == 0 ) return;
		
		neighbours.removeAll(partition);
		
		for ( Square s: neighbours ) {
			
			partition.add(s);
			extendPartitionX(s, partition, contacts);
		}	
	}
	
	
	public static void fillBoardWithPentominoes(Board board, Chooser chooser) {
		
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		Set<PentominoType> placed = EnumSet.noneOf(PentominoType.class);
			
		// Order the squares on the board by increasing number of the squares they see.
		// this should put board corners at the top of the list, followed by edges, followed by interior.
		
		List<Square> order = new ArrayList<>();
		order.addAll(board.getSquares());
		Collections.sort(order, new Comparator<Square>() {

			@Override
			public int compare(Square a, Square b) {
				return environment.get(a).size() - environment.get(b).size();
			}		
		});

		boolean emergencyStop = false;
		
		int nextSquareIndex = 0; int loop = 0;
		
		while ( order.size() > 0 && !emergencyStop ) {
			
			Set<Square> tile = placePentomino(board, order.get(nextSquareIndex), environment, chooser);
			PentominoType type = Pentomino.identifyPentomino(tile);
			
			//if ( isBoardValid(board) && !(type == PentominoType.P && placed.contains(PentominoType.P)) ) {
			if ( isBoardValid(board) && !placed.contains(type) ) {
				
				placed.add(type);
				order.removeAll(tile);
				nextSquareIndex = 0;
			}
			else {
				
				TileAction.dissolveTile(tile);
				
				// It's possible to reach a point where starting a square in any of the remaining positions
				// must inevitably close off an invalid portion of the board,whatever tile is produced.
				// Add some 'jitter' to the tile starting point selection to avoid this:

				nextSquareIndex = (nextSquareIndex + 1) % order.size();
				if ( nextSquareIndex == 0 ) loop++;
				
				 if ( loop == 2 ) {
					 
					// It's also possible to be in this situation regardless of starting square.
				 	// Stop when we've tried all the possible starting points left in the 'order' list -
				 	// i.e. when nextSquareIndex goes back rounfd to 0.
						
					 emergencyStop = true;
				 }
			}
		}
		
		System.out.println(placed);
	}

}
