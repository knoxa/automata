package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import act.Action;
import argumentation.engine.Backtrack;
import argumentation.engine.Label;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import observe.SquareObserver;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoMove;
import tiles.PentominoType;
import tiles.Tile;
import uk.ac.kent.dover.fastGraph.EdgeStructure;
import uk.ac.kent.dover.fastGraph.FastGraph;
import uk.ac.kent.dover.fastGraph.NodeStructure;
import worlds.Board;
import worlds.BoardManager;
import worlds.Compass;
import worlds.Plane;
import xslt.Pipeline;

public class SolutionMap {

	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, TransformerException {

		//Board board = BoardManager.loadFromXml(new FileInputStream("/D:/GitHub/knoxa.github.io/pentominoes/solutions/6x10/60.xml"));
		Board board = BoardManager.loadFromXml(new FileInputStream("/D:/GitHub/knoxa.github.io/pentominoes/solutions/4x15/213.xml"));
		//Board board = BoardManager.loadFromXml(new FileInputStream("experiments/out.xml"));
		System.out.println(BoardManager.identifySolution(board.getGrid()));
		
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);

		Map<Integer, Set<Integer>> graph = Partitioner.getPartitionGraph(partitionMap, environment);
		System.out.println(graph);
		
		System.out.println("---------");
		
		Set<PentominoType> fromOptions = new HashSet<>();
		
		for ( PentominoType type: pentominoes.keySet()) {
			
			// list all the tiles whose type appears more than once.
			if ( pentominoes.get(type).size() > 1 )  fromOptions.add(type);
		}
	
		Map<Integer, Set<Integer>> potentialSwaps = LookAhead.filterGraph(graph, partitionMap,environment);
		System.out.println(potentialSwaps);
		
		///
		List<Set<Integer>> pairs = new ArrayList<>();
		
		for ( Integer from: potentialSwaps.keySet() ) {
			
			for ( Integer to: potentialSwaps.get(from) ) {
				
				if ( from < to )  {
					
					Set<Integer> pair = new HashSet<>();
					pair.add(from); pair.add(to);
					pairs.add(pair);
				}
			}
		}
		
		System.out.println(pairs);
				
		///
		Map<Set<Integer>, Set<Set<Integer>>> argument = new HashMap<>();
		
		for ( Set<Integer> pair: pairs ) {
			
			Set<Set<Integer>> attacks = new HashSet<>();
			
			for ( Set<Integer> swap: pairs ) {
				
				Set<Integer> temp = new HashSet<>();
				temp.addAll(swap);
				
				temp.removeAll(pair);
				//System.out.println(pair + " -- " + swap + " -- " + temp);
				if ( temp.size() == 1 ) attacks.add(swap);			
			}
			
			argument.put(pair, attacks);
		}
		///
		
		//System.out.println("###\n" + argument);
		System.out.println("###\n");
		
		
		List<NodeStructure> nodes = new ArrayList<NodeStructure>();
		List<EdgeStructure> edges = new ArrayList<EdgeStructure>();
		
		Map<Set<Integer>, Integer> nodeMap = new HashMap<>();
		Map<Integer, Set<Integer>> pairMap = new HashMap<>();
		int nodeNum = 0;
		
		for ( Set<Integer> pair: argument.keySet() ) {
			
			String label = pair.toString();
			nodeMap.put(pair, nodeNum);
			pairMap.put(nodeNum, pair);
			nodes.add(new NodeStructure(nodeNum++, label, 0, (byte) 0, (byte) 0));
		}

		for ( Set<Integer> pair: argument.keySet() ) {
			
			for ( Set<Integer> notAcceptable: argument.get(pair) ) {
				
				int from = nodeMap.get(pair); int to = nodeMap.get(notAcceptable);
				edges.add(new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, from, to));
			}
		}

		FastGraph framework = FastGraph.structureFactory("PENTOMINO", (byte) 0, nodes, edges, true);


		Label[] labels = new Label[framework.getNumberOfNodes()];
	//	System.out.println(framework);
		Arrays.fill(labels, Label.BLANK);
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();

		Backtrack.admissible(framework, labels, extensions);
       
		System.out.println(extensions.size() + " extensions");
		
		Pipeline p = new Pipeline();

		p.setOutput(new FileOutputStream("/D:/GitHub/automata/experiments/out.xml"));
		ContentHandler serializer = p.getContentHandler();
		serializer.startDocument();
		serializer.startElement("", "analysis", "analysis", new AttributesImpl());
		
		Map<Set<Integer>, List<PentominoType>> inputMap = new HashMap<>();
		Map<Set<Integer>, List<PentominoMove>> outputMap = new HashMap<>();

		for ( Integer from: potentialSwaps.keySet() ) {
			
			for ( Integer to: potentialSwaps.get(from) ) {
				
				if ( from < to )  {
					
					List<PentominoMove> options = swap(partitionMap, environment, from, to, serializer);
					Set<Integer> pair = new HashSet<>(); pair.add(from); pair.add(to);
					List<PentominoType> inputTypes = new ArrayList<>();
					inputTypes.add(Pentomino.identifyPentomino(partitionMap.get(from))); inputTypes.add(Pentomino.identifyPentomino(partitionMap.get(to)));
					inputMap.put(pair, inputTypes);
					outputMap.put(pair, options);
				}
			}
		}
		
		serializer.endElement("", "analysis", "analysis");
		serializer.endDocument();
		
	}

	
	public static List<PentominoMove> swap(Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment, int a, int b, ContentHandler serializer) throws ParserConfigurationException, SAXException, FileNotFoundException, TransformerException {
		
		List<PentominoMove> possibileSwaps = new ArrayList<PentominoMove>();
		
		HashSet<Integer> tiles = new HashSet<Integer>(); tiles.add(a); tiles.add(b);
		Map<Square, Set<Sense>> contacts = SquareObserver.sense(partitionMap, environment, tiles);
		List<Set<Set<Square>>> possibleMoves = Pentomino.getPossibleSwaps(contacts);

		Set<Square> tileA = partitionMap.get(a);
		Set<Square> tileB = partitionMap.get(b);

		List<PentominoType> inputTypes = new ArrayList<>();
		inputTypes.add(Pentomino.identifyPentomino(tileA));
		inputTypes.add(Pentomino.identifyPentomino(tileB));
		Set<Integer> pair = new HashSet<>(); pair.add(a); pair.add(b);

		serializer.startElement("", "equivalent", "equivalent", new AttributesImpl());

		Set<Square> temp = new HashSet<Square>();
		temp.addAll(tileA);
		temp.retainAll(contacts.keySet());
		Square squareA = temp.iterator().next();
		Sense sense = contacts.get(squareA).iterator().next();		

		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		Tile.position(squareA, 0, 0, coordinates, positions);	
		Tile.position(sense.getSquare(), Compass.getOffsetX(sense.getDirection()), Compass.getOffsetY(sense.getDirection()), coordinates, positions);
		Plane.serialize(coordinates, positions, serializer);
		
		Set<List<PentominoType>> allOutputTypes = new HashSet<>();
		
		for ( Set<Set<Square>> option: possibleMoves ) {
			
			coordinates = new HashMap<Square, Integer[]>();		
			positions = new HashMap<>();

			Map<Square, Square> map = new HashMap<>();
			Map<Square, Square> newA = Tile.copy(tileA);
			Map<Square, Square> newB = Tile.copy(tileB);
			map.putAll(newA); map.putAll(newB);

			Tile.position(map.get(squareA), 0, 0, coordinates, positions);	
			Tile.position(map.get(sense.getSquare()), Compass.getOffsetX(sense.getDirection()), Compass.getOffsetY(sense.getDirection()), coordinates, positions);

			List<Action> moves = Pentomino.getSwapActions(option, contacts);
			List<Action> copy = LookAhead.copyActions(moves, map); 
			
			Action.makeMoves(copy);
			
			Plane.serialize(coordinates, positions, serializer);
			
			Map<Integer, Set<Square>> newPartitions = Partitioner.partition(coordinates.keySet());
			List<PentominoType> outputTypes = new ArrayList<>();
			
			for ( Integer tile: newPartitions.keySet() ) {
				
				outputTypes.add(Pentomino.identifyPentomino(newPartitions.get(tile)));
			}
			allOutputTypes.add(outputTypes);
			
			PentominoMove opt = new PentominoMove();
			opt.setActions(moves);
			opt.setResult(outputTypes);
			possibileSwaps.add(opt);
		}
		
		serializer.endElement("", "equivalent", "equivalent");

		return possibileSwaps;
	}
	
	
	private static void isolate(Set<Square> tileA, Set<Square> tileB, Map<Square, Set<Sense>> environment) {
		
		Set<Square> locality = new HashSet<Square>();
		locality.addAll(tileA); locality.addAll(tileB);
		Map<Square, Set<Sense>> localEnv = SquareObserver.restrictEnvironment(environment, locality);
		Map<Square, Set<Sense>> sensed = SquareObserver.sensedByTile(localEnv, tileA);
		Iterator<Square> iter = sensed.keySet().iterator();
		Square a = iter.next();
		while ( sensed.get(a).size() == 0 ) a = iter.next();
		Sense sense = sensed.get(a).iterator().next();
		
		Map<Square, Square> newA = Tile.copy(tileA);
		Map<Square, Square> newB = Tile.copy(tileB);
		
		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		Tile.position(newA.get(a), 0, 0, coordinates, positions);	
		Tile.position(newB.get(sense.getSquare()), Compass.getOffsetX(sense.getDirection()), Compass.getOffsetY(sense.getDirection()), coordinates, positions);
	}

}
