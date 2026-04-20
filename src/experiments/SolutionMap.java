package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import observe.SquareObserver;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import tiles.Tile;
import worlds.Board;
import worlds.BoardManager;
import worlds.Compass;
import worlds.Plane;
import xslt.Pipeline;

public class SolutionMap {

	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, TransformerException {

		Board board = BoardManager.loadFromXml(new FileInputStream("/D:/GitHub/automata/experiments/solution34.xml"));
		System.out.println(BoardManager.identifySolution(board.getGrid()));
		
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		System.out.println(pentominoes);

		Map<Integer, Set<Integer>> graph = Partitioner.getPartitionGraph(partitionMap, environment);
		System.out.println(graph);
		
		System.out.println("---------");
		System.out.println(filterGraph(graph, partitionMap,environment));
		
		swap(partitionMap, environment, 4, 11);
	}
	
	
	public static int numberofTransforms(Integer a, Integer b, Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment) {
		
		HashSet<Integer> tiles = new HashSet<Integer>();
		tiles.add(a); tiles.add(b);
		
		Map<Square, Set<Sense>> contacts = SquareObserver.sense(partitionMap, environment, tiles);
		List<Set<Set<Square>>> possibleMoves = Pentomino.getPossibleSwaps(contacts);
		return possibleMoves.size();
	}
	
	
	public static Map<Integer, Set<Integer>> filterGraph(Map<Integer, Set<Integer>> graph, Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment) {
		
		Map<Integer, Set<Integer>> filtered = new HashMap<>();
		
		for ( Integer from: graph.keySet() ) {
			
			Set<Integer> filteredLinks = new HashSet<>();
			Set<Integer> links = graph.get(from);
			
			for ( Integer to: links ) {
				
				int transforms = numberofTransforms(from, to, partitionMap, environment);
				//if ( from < to ) System.out.println(from + "," + to + ": " + transforms);
				
				if ( transforms > 0 ) {
					
					filteredLinks.add(to);
				}
			}
			
			filtered.put(from, filteredLinks);
		}
		
		return filtered; 
	}

	
	public static Map<Square, Square> copy(Set<Square> squares) {
		
		Map<Square, Square> copyMap = new HashMap<>();
		for ( Square square: squares )  copyMap.put(square, new Square());
		
		for ( Square original: copyMap.keySet() ) {
			
			Square copy = copyMap.get(original);
			copy.setLabel(original.getLabel());
			Map<Direction, Square> neighbourMap = original.getNeighbourMap();
			
			for ( Direction direction: neighbourMap.keySet() ) {
				
				copy.setNeighbour(direction, copyMap.get(neighbourMap.get(direction)));
			}
		}
		
		return copyMap;
	}

	
	public static void swap(Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment, int a, int b) throws ParserConfigurationException, SAXException, FileNotFoundException, TransformerException {
		
		HashSet<Integer> tiles = new HashSet<Integer>(); tiles.add(a); tiles.add(b);
		Map<Square, Set<Sense>> contacts = SquareObserver.sense(partitionMap, environment, tiles);
		List<Set<Set<Square>>> possibleMoves = Pentomino.getPossibleSwaps(contacts);

		Set<Square> tileA = partitionMap.get(a);
		Set<Square> tileB = partitionMap.get(b);

		Pipeline p = new Pipeline();

		p.setOutput(new FileOutputStream("/D:/GitHub/automata/experiments/out.xml"));
		ContentHandler serializer = p.getContentHandler();
		serializer.startDocument();
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
		
		for ( Set<Set<Square>> option: possibleMoves ) {
			
			coordinates = new HashMap<Square, Integer[]>();		
			positions = new HashMap<>();

			Map<Square, Square> map = new HashMap<>();
			Map<Square, Square> newA = copy(tileA);
			Map<Square, Square> newB = copy(tileB);
			map.putAll(newA); map.putAll(newB);

			Tile.position(map.get(squareA), 0, 0, coordinates, positions);	
			Tile.position(map.get(sense.getSquare()), Compass.getOffsetX(sense.getDirection()), Compass.getOffsetY(sense.getDirection()), coordinates, positions);

			List<Action> moves = Pentomino.getSwapActions(option, contacts);
			List<Action> copy = copyActions(moves, map); 
			
			Action.makeMoves(copy);
			
			Plane.serialize(coordinates, positions, serializer);			
		}
		
		serializer.endElement("", "equivalent", "equivalent");
		serializer.endDocument();

	}
	
	
	public static List<Action> copyActions(List<Action> actions, Map<Square, Square> map) {
		
		List<Action> copy = new ArrayList<>();
		
		for ( Action action: actions ) {
			
			Sense sense = action.getSense();
			Sense newSense = new Sense(sense.getDirection(), map.get(sense.getSquare()));
			Action newAction = new Action(map.get(action.getActor()), action.getAct(), newSense);
			copy.add(newAction);
		}
		
		return copy;
	}
	
	public static void isolate(Set<Square> tileA, Set<Square> tileB, Map<Square, Set<Sense>> environment) {
		
		Set<Square> locality = new HashSet<Square>();
		locality.addAll(tileA); locality.addAll(tileB);
		Map<Square, Set<Sense>> localEnv = SquareObserver.restrictEnvironment(environment, locality);
		Map<Square, Set<Sense>> sensed = SquareObserver.sensedByTile(localEnv, tileA);
		Iterator<Square> iter = sensed.keySet().iterator();
		Square a = iter.next();
		while ( sensed.get(a).size() == 0 ) a = iter.next();
		Sense sense = sensed.get(a).iterator().next();
		
		Map<Square, Square> newA = copy(tileA);
		Map<Square, Square> newB = copy(tileB);
		
		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();

		Tile.position(newA.get(a), 0, 0, coordinates, positions);	
		Tile.position(newB.get(sense.getSquare()), Compass.getOffsetX(sense.getDirection()), Compass.getOffsetY(sense.getDirection()), coordinates, positions);
	}

}
