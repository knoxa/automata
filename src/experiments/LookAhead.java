package experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import worlds.Compass;

public class LookAhead {
	
	public static Set<PentominoMove> lookAt(Board board) {
		
		Set<PentominoMove> solution = null;
				
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<Square, Set<Sense>> environment = BoardObserver.lookAbout(board);
		
		Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
		Map<Integer, Set<Integer>> graph = Partitioner.getPartitionGraph(partitionMap, environment);
		Map<Integer, Set<Integer>> potentialSwaps = filterGraph(graph, partitionMap,environment);
		
		List<Set<Integer>> pairs = getPairs(potentialSwaps);

		///
		
		Map<Set<Integer>, List<PentominoType>> inputMap  = new HashMap<>();
		Map<Set<Integer>, List<PentominoMove>> outputMap = new HashMap<>();

		for ( Integer from: potentialSwaps.keySet() ) {
			
			for ( Integer to: potentialSwaps.get(from) ) {
				
				// edges in the graph are bidirectional - only need each pair of tiles once
				
				if ( from < to )  {
					
					List<PentominoMove> options = swap(partitionMap, environment, from, to);
					Set<Integer> pair = new HashSet<>(); pair.add(from); pair.add(to);
					List<PentominoType> inputTypes = new ArrayList<>();
					inputTypes.add(Pentomino.identifyPentomino(partitionMap.get(from))); inputTypes.add(Pentomino.identifyPentomino(partitionMap.get(to)));
					inputMap.put(pair, inputTypes);
					outputMap.put(pair, options);
				}
			}
		}
		
		///
		
		Map<Set<Integer>, Integer> nodeMap = new HashMap<>();
		Map<Integer, Set<Integer>> pairMap = new HashMap<>();
		Set<Set<Integer>> extensions = argument(pairs, nodeMap, pairMap);
		
		///
		
		// Analyse the extensions
		
		for ( Set<Integer> extension: extensions ) {
			
			// get the set of pentomino types for the tiles that would be changed by this extension
			Set<PentominoType> input = new HashSet<>();		
			for ( Integer x: extension )  input.addAll(inputMap.get(pairMap.get(x)));

			// get the possible alternative exchanges for this extension
			Set<Integer> temp = new HashSet<>(); temp.addAll(extension);
			Set<Set<PentominoMove>> alts = alternatives(temp, outputMap, pairMap);
			
			// examine each alternative ...
			
			for ( Set<PentominoMove> alternative: alts ) {
				
				// get the set of pentomino types on the input 'board'
				Set<PentominoType> result = new HashSet<>();
				result.addAll(pentominoes.keySet());
				
				// remove all the types this alternative would change
				result.removeAll(input);
				
				// add the pentomino types this alternative would produce
				Set<PentominoType> outputTypes = new HashSet<PentominoType>();
				for ( PentominoMove alt: alternative )  outputTypes.addAll(alt.getResult());
				result.addAll(outputTypes);

				if ( result.size() == 12 ) {

					System.out.println("can get a solution from here: " + input + " -->  " + outputTypes);
					solution = alternative;
				}
			}
		}		

		return solution;
	}

	
	public static Map<Integer, Set<Integer>> filterGraph(Map<Integer, Set<Integer>> graph, Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment) {
		
		// take a graph of adjacent pentominoes and filter out links between pentominoes that can't exchange squares
		
		Map<Integer, Set<Integer>> filtered = new HashMap<>();
		
		for ( Integer from: graph.keySet() ) {
			
			Set<Integer> filteredLinks = new HashSet<>();
			Set<Integer> linksTo = graph.get(from);
			
			for ( Integer to: linksTo ) {
				
				int transforms = LookAhead.numberOfTransforms(from, to, partitionMap, environment);

				if ( transforms > 0 ) {
					
					// the pair can exchange squares, keep this link
					filteredLinks.add(to);
				}
			}
			
			// replace the input links with the selected links 
			filtered.put(from, filteredLinks);
		}
		
		return filtered; 
	}


	public static int numberOfTransforms(Integer a, Integer b, Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment) {
		
		// get the number of possible exchanges between a pair of pentominoes
		HashSet<Integer> tiles = new HashSet<Integer>();
		tiles.add(a); tiles.add(b);
		
		Map<Square, Set<Sense>> contacts = SquareObserver.sense(partitionMap, environment, tiles);
		List<Set<Set<Square>>> possibleMoves = Pentomino.getPossibleSwaps(contacts);
		return possibleMoves.size();
	}

	
	private static List<Set<Integer>> getPairs(Map<Integer, Set<Integer>> potentialSwaps) {
		
		// get potential tile exchanges as a list of pairs of tile numbers
		
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

		return pairs;
	}

	
	private static Set<Set<Integer>> argument(List<Set<Integer>> pairs, Map<Set<Integer>, Integer> nodeMap, Map<Integer, Set<Integer>> pairMap) {
		
		// construct a Dung framework		
		// Each argument is a pair of pentominoes. It is attacked by any argument that includes either of the same pair.
		Map<Set<Integer>, Set<Set<Integer>>> argument = new HashMap<>();
		
		for ( Set<Integer> pair: pairs ) {
			
			Set<Set<Integer>> attacks = new HashSet<>();
			
			for ( Set<Integer> swap: pairs ) {
				
				// comparing the tile number in pair A and B: make a set that is the intersection of the two ...
				Set<Integer> temp = new HashSet<>();
				temp.addAll(swap); temp.removeAll(pair);
				
				// if the intersection is of size 2, then A and B are the same tile - no contradiction
				// if the intersection is of size 0, then no shared tile and no contradiction
				// if the intersection is of size 1, then A and B share a tile - contradcition
				if ( temp.size() == 1 ) attacks.add(swap);			
			}
			
			argument.put(pair, attacks);
		}
	
		// make a FastGraph of the Dung argument framework
		
		List<NodeStructure> nodes = new ArrayList<NodeStructure>();
		List<EdgeStructure> edges = new ArrayList<EdgeStructure>();
		
		int nodeNum = 0;
		
		for ( Set<Integer> pair: argument.keySet() ) {
			
			String label = pair.toString();
			// nodes in the Dung framework graph are numbered.
			// We want to build maps between pentomino pairs and node numbers, and vice versa 
			nodeMap.put(pair, nodeNum); pairMap.put(nodeNum, pair);
			nodes.add(new NodeStructure(nodeNum++, label, 0, (byte) 0, (byte) 0));
		}

		for ( Set<Integer> pair: argument.keySet() ) {
			
			for ( Set<Integer> notAcceptable: argument.get(pair) ) {
				
				int from = nodeMap.get(pair); int to = nodeMap.get(notAcceptable);
				edges.add(new EdgeStructure(0, "", 0, (byte) 0, (byte) 0, from, to));
			}
		}

		FastGraph framework = FastGraph.structureFactory("PENTOMINO", (byte) 0, nodes, edges, true);

		// Run the argument - the set of all possible self consistent tile exchanges,
		// i.e. that don't conflict because any given tile appears at most once in any pair in the extension,
		// is the same as the set of admissible extensions of the Dung framework.
		
		Label[] labels = new Label[framework.getNumberOfNodes()];
		Arrays.fill(labels, Label.BLANK);
		Set<Set<Integer>> extensions = new HashSet<Set<Integer>>();
		Backtrack.admissible(framework, labels, extensions);
		
		return extensions;
	}
	
	
	public static List<PentominoMove> swap(Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment, int a, int b) {
		
		// Make a list of possible exchanges between the given pair of tiles		
		List<PentominoMove> possibileSwaps = new ArrayList<PentominoMove>();
		
		HashSet<Integer> tiles = new HashSet<Integer>(); tiles.add(a); tiles.add(b);
		Map<Square, Set<Sense>> contacts = SquareObserver.sense(partitionMap, environment, tiles);
		List<Set<Set<Square>>> possibleMoves = Pentomino.getPossibleSwaps(contacts);

		Set<Square> tileA = partitionMap.get(a);
		Set<Square> tileB = partitionMap.get(b);

		// the pentomino types before squares are exchanged
		List<PentominoType> inputTypes = new ArrayList<>();
		inputTypes.add(Pentomino.identifyPentomino(tileA));
		inputTypes.add(Pentomino.identifyPentomino(tileB));

		// get a square in tile 'A' that is in contact with a square in tile 'B'
		Set<Square> temp = new HashSet<Square>();
		temp.addAll(tileA);
		temp.retainAll(contacts.keySet());
		Square squareA = temp.iterator().next();
		Sense sense = contacts.get(squareA).iterator().next();		

		// Use the square in tile 'A' to plot tile A, then use the square it connects to in tile 'B' to plot tile B.
		// This is the input arrangement of tiles.
		Map<Square, Integer[]> coordinates = new HashMap<Square, Integer[]>();		
		Map<Integer, Map<Integer, Set<Square>>> positions = new HashMap<>();
		Tile.position(squareA, 0, 0, coordinates, positions);
		Tile.position(sense.getSquare(), Compass.getOffsetX(sense.getDirection()), Compass.getOffsetY(sense.getDirection()), coordinates, positions);
		
		// loop over each possible exchange between the input tiles.

		for ( Set<Set<Square>> option: possibleMoves ) {
			
			// make a copy of the input tile arrangement
			
			coordinates = new HashMap<Square, Integer[]>();		
			positions = new HashMap<>();

			Map<Square, Square> map = new HashMap<>();
			Map<Square, Square> newA = Tile.copy(tileA);
			Map<Square, Square> newB = Tile.copy(tileB);
			map.putAll(newA); map.putAll(newB);

			Tile.position(map.get(squareA), 0, 0, coordinates, positions);	
			Tile.position(map.get(sense.getSquare()), Compass.getOffsetX(sense.getDirection()), Compass.getOffsetY(sense.getDirection()), coordinates, positions);

			// make the square exchanges in the copy of the intput arrangement
			List<Action> moves = Pentomino.getSwapActions(option, contacts);
			List<Action> copy = copyActions(moves, map); 			
			Action.makeMoves(copy);
			
			// get the output tile types for this exchange
			Map<Integer, Set<Square>> newPartitions = Partitioner.partition(coordinates.keySet());
			List<PentominoType> outputTypes = new ArrayList<>();
			
			for ( Integer tile: newPartitions.keySet() ) {
				
				outputTypes.add(Pentomino.identifyPentomino(newPartitions.get(tile)));
			}
			
			// make a PentominoMove to capture the list of actions (as would be applied to the input tiles) and the output types that would result.
			PentominoMove opt = new PentominoMove();
			opt.setActions(moves);
			opt.setResult(outputTypes);
			possibileSwaps.add(opt);
		}

		return possibileSwaps;
	}


	public static List<Action> copyActions(List<Action> actions, Map<Square, Square> map) {
		
		// The 'actions' list is a set of actions on the squares that are the keys of 'map'.
		// Make a corresponding list of actions on the values of 'map'.
		
		List<Action> copy = new ArrayList<>();
		
		for ( Action action: actions ) {
			
			Sense sense = action.getSense();
			Sense newSense = new Sense(sense.getDirection(), map.get(sense.getSquare()));
			Action newAction = new Action(map.get(action.getActor()), action.getAct(), newSense);
			copy.add(newAction);
		}
		
		return copy;
	}
	
	
	public static Set<Set<PentominoMove>> alternatives(Set<Integer> extension, Map<Set<Integer>, List<PentominoMove>> outputMap, Map<Integer, Set<Integer>> pairMap) {
		
		// An extension is a set of Integers that maps to a set of tile pairs via 'pairMap'
		// A tile pair maps to a list of possible tile exchanges via 'outputMap'
		// We want to produce a set of PentominoMove objects for each combination of possible moves across all tile pairs
		
		Set<Set<PentominoMove>> results = new HashSet<>();
		
		if ( extension.isEmpty() ) return results;
		
		// for the next extension in the input ...
		Integer x = extension.iterator().next();
		// get the alternative moves for that extension
		List<PentominoMove> outputs = outputMap.get(pairMap.get(x));
		
		// remove the current extension from the list and make a recursive call
		extension.remove(x);		
		Set<Set<PentominoMove>> alts = alternatives(extension, outputMap, pairMap);
		
		// at the bottom of the recursion, 'alts' contains all the alternatives for the remainder of the extensions.
		// add in the alternatives for the current extension
		
		if ( alts.isEmpty() ) {
			
			// no extensions have been processed so far - the current one must be the last in the 'extension' set ...
			// Make a new set of alternatives for each possible exchange for this extension
			
			for ( PentominoMove ti: outputs ) {
				
				Set<PentominoMove> output = new HashSet<>();
				output.add(ti);
				results.add(output);
			}
		}
		else {
			
			// We already have alternatives returned for the remainder of the 'extension' set ...
			// Extend the list by creating a new alternative that adds each of the possible exchanges for this extension
			// to each of the alternatives.
			
			for ( PentominoMove ti: outputs ) {
				
				for ( Set<PentominoMove> tj : alts ) {
					
					Set<PentominoMove> merge = new HashSet<>();
					merge.add(ti); merge.addAll(tj);
					results.add(merge);
				}
			}
		}
		
		return results;
	}

}
