package tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import act.Act;
import act.Action;
import act.TileAction;
import cakes.category.Maps;
import cells.Sense;
import cells.Square;
import observe.SquareObserver;
import orient.Chooser;
import orient.Partitioner;
import worlds.Board;

public class Pentomino {
	
	private static final int MAX_ITERATIONS = 5000;

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
				Set<Square> tile = Partitioner.getTileContaining(next);
				TileAction.dissolveTile(tile);
			}
			else {
				
				// a square from one of the smallest tiles defects (to its smallest neighbour)
				Integer p = chooser.randomSmallestPartion(sizeMap, sizes);
				TileAction.anySquareDefects(partitionMap.get(p), environment, chooser);
			}

			moves++;
		}
		
		
		return moves;
	}
	
	
	public static int formPentominoes2(Board board, Chooser chooser, Map<Square, Set<Sense>> environment) {
		
		int moves = 0;
		
		for ( int i = 0; i < MAX_ITERATIONS; i++ ) {
			
			// partition the board into tiles and get the tile sizes
			Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
			Map<Integer, Set<Integer>> sizeMap = Partitioner.collectBySize(partitionMap);

			// finished if we only have pentominoes
			if ( Pentomino.haveOnlyPentominoes(partitionMap) )  break;
			
			List<Integer> sizes = new ArrayList<Integer>();
			sizes.addAll(sizeMap.keySet());

			Integer p = chooser.randomSmallestPartion(sizeMap, sizes);
			TileAction.anySquareDefects(partitionMap.get(p), environment, chooser);

			moves++;
		}
		
		
		return moves;
	}

	
	public static Map<Integer, Set<Set<Square>>> getDetachableFragments(Map<Square, Set<Sense>> contacts) {
		
		// Get tile fragments that be detached from a pentomino, leaving the remainder of the tile connected.
		// Fragments are filtered so that only those containing a square in 'contacts' are returned.
		
		Map<Integer, Set<Set<Square>>> tileFragments = new HashMap<>();

		// partition the tiles that are in contact		
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(contacts.keySet());		

		for ( Integer partNo: partitionMap.keySet() ) {
			
			Set<Set<Square>> fragments = new HashSet<>();
			
			for ( Square square: partitionMap.get(partNo) ) {
				
				if ( Tile.isDetachable(square) ) {
					
					// the single square is a detacable fragment in its own right
					Set<Square> fragment = new HashSet<>();
					fragment.add(square);
					
					// ... but may also be part of a larger fragment
					for ( Square neighbour: square.getNeighbours() ) {
						
						if ( neighbour.neighbourCount() <= 2 ) {
							
							fragment.add(neighbour);
						}
						else if ( neighbour.neighbourCount() == 3 )  {
							
							// check for P tile - can only disconnect the square with 3 neighbours if it is in a fragment
							// with the square that has 1 neighbour
							Set<Square> nextNeighbours = neighbour.getNeighbours();
							nextNeighbours.remove(square);
							
							boolean wanted = true;
							
							for ( Square sq: nextNeighbours )  if ( sq.neighbourCount() != 2 )  wanted = false;						
							if ( wanted )  fragment.add(neighbour);
						}
					}
					
					// ignore fragments that don't have a square in the set of contacts
					Set<Square> pointsOfContact = new HashSet<>();
					pointsOfContact.addAll(fragment);
					pointsOfContact.retainAll(contacts.keySet());
					
					// store the fragment. Don't need fragments of size 3 because we'll get the corresponding size 2 fragment anyway
					if ( !pointsOfContact.isEmpty() && fragment.size() < 3 ) {
						
						fragments.add(fragment);
					}
					
					if ( fragment.size() > 1 && contacts.keySet().contains(square) ) {
						
						// Add a set containing just the detachable tile 
						Set<Square> singleton = new HashSet<>();
						singleton.add(square);
						fragments.add(singleton);						
					}
				}
				
				tileFragments.put(partNo, fragments);
			}
		}
		
		return tileFragments;
	}

	public static boolean exchangeSquares(Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment, int a, int b, Chooser chooser) {
		
		HashSet<Integer> tiles = new HashSet<Integer>(); tiles.add(a); tiles.add(b);
		Map<Square, Set<Sense>> contacts = SquareObserver.sense(partitionMap, environment, tiles);
		List<Set<Set<Square>>> possibleMoves = getPossibleSwaps(contacts);
		
		if ( possibleMoves.size() > 0 ) {
			
			Set<Set<Square>> option = chooser.randomFromList(possibleMoves);

			List<Action> moves = getSwapActions(option, contacts);
			Action.makeMoves(moves);
			return true;
		}
		else return false;
	}


	public static List<Set<Set<Square>>> getPossibleSwaps(Map<Square, Set<Sense>> contacts) {
		
		// find fragments in a pair of tiles that can be swapped
		
		List<Set<Set<Square>>> options = new ArrayList<>();
	
		// get the detachable subsets (fragments) of each of the tiles
		// key is tile number, value is set of detachable fragments (each of which is  a set of squares)
		
		Map<Integer, Set<Set<Square>>> tileFragments = getDetachableFragments(contacts);
	
		if ( !tileFragments.isEmpty() ) {
					
			Integer tile1 = 1; Integer tile2 = 2; // assuming there are 2 tiles
						
			for ( Set<Square> fragment1: tileFragments.get(tile1) ) {
				
				for ( Set<Square> fragment2: tileFragments.get(tile2) ) {
					
					if ( fragment1.size() == fragment2.size() ) {
											
						// get points-of-contact in each of the tile fragments				
						Set<Square> poc1 = new HashSet<Square>(); poc1.addAll(fragment1); poc1.retainAll(contacts.keySet());
						Set<Square> poc2 = new HashSet<Square>(); poc2.addAll(fragment2); poc2.retainAll(contacts.keySet());
	
						//get the squares seen by the contact squares in each fragment ...
						Set<Square> sensed1 = new HashSet<>();
						Set<Square> sensed2 = new HashSet<>();				
						for ( Square s: poc1 )  sensed1.addAll(getSensedSquares(contacts.get(s)));
						for ( Square s: poc2 )  sensed2.addAll(getSensedSquares(contacts.get(s)));
	
						// ... these will the squares on the other tile that this fragment might attach to
						// remove any squares that are part of the other fragment (don't want to connect fragment to fragment)
						sensed1.removeAll(fragment2);
						sensed2.removeAll(fragment1);
	
						if ( !sensed1.isEmpty() && !sensed2.isEmpty() ) {
							
							// we have two fragments of the same size, that can detach from their tile, and that can 
							// attach to the other tile, without interfering with each other.
							Set<Set<Square>> option = new HashSet<>();
							option.add(fragment1); option.add(fragment2);
							options.add(option);
						}
					}
				}			
			}
		}
			
		return options;		
	}

	
	public static List<Action> getSwapActions(Set<Set<Square>> option, Map<Square, Set<Sense>> contacts) {
		
		List<Action> actions = new ArrayList<>();
		
		Iterator<Set<Square>> iter = option.iterator();		
		Set<Square> fragment1 = iter.next();
		Set<Square> fragment2 = iter.next();
		actions.addAll(Tile.detachTileActions(fragment1));
		actions.addAll(Tile.detachTileActions(fragment2));

		for ( Square x: fragment1 ) {
			
			Set<Sense> touches = contacts.get(x);
			
			if ( touches != null ) {
				
				for ( Sense touch: touches ) {
					
					if ( !fragment2.contains(touch.getSquare()) ) {
						
						Action action = new Action();
						action.setAct(Act.ATTACH); action.setActor(x); action.setSense(touch);
						actions.add(action);
					}
				}
			}
		}

		for ( Square x: fragment2 ) {
			
			Set<Sense> touches = contacts.get(x);
			
			if ( touches != null ) {
				
				for ( Sense touch: touches ) {
					
					if ( !fragment1.contains(touch.getSquare()) ) {
						
						Action action = new Action();
						action.setAct(Act.ATTACH); action.setActor(x); action.setSense(touch);
						actions.add(action);
					}
				}
			}
		}
		
		return actions;

	}
	
	
	public static Set<Square> getSensedSquares(Set<Sense> sensed) {
		
		Set<Square> squares = new HashSet<Square>();
		for ( Sense sense: sensed )  squares.add(sense.getSquare());	
		return squares;
	}

	
	public static void chaseTheAce(Square ace, Set<Square> tileFrom, Map<Square, Set<Sense>> environment, Chooser chooser) {
		
		boolean finished = false; int steps = 0;
		
		Set<Square> currentTile = tileFrom;
		
		while ( !finished && ace != null && steps < 200 ) {

			// find squares (in neighbouring tiles) that the displaced square might connect to
			Map<Square, Sense> possibleConnections = selectCandidateTargetSquares(ace, environment, currentTile);
			// get the tiles that these belong to
			Map<Integer, Set<Square>> possibleTiles = Partitioner.partition(possibleConnections.keySet());
			
			// work out where to connect the displaced square
			Map<Integer, Set<Integer>> sizes = Partitioner.collectPartitionSizes(possibleTiles);
			System.out.println("step: " + steps + " - " + sizes);
			
			if ( sizes.get(4) != null ) { 
				
				// connect to a tile of size 4 if there is one
				Integer tileOfSize4 = chooser.randomFromSet(sizes.get(4));
				System.out.println("444444444444444444444444444444444444444 " );
				Set<Square> candidates = possibleConnections.keySet();
				candidates.retainAll(possibleTiles.get(tileOfSize4));
				System.out.println(candidates);
				Square target = chooser.randomFromSet(candidates);
				
				List<Action> actions = new ArrayList<Action>();
				Action action = new Action(ace, Act.ATTACH, possibleConnections.get(target));
				actions.add(action);
				Action.makeMoves(actions);

				finished = true;
			}
			else {
				
				// make a random choice for where the displaced square will go
				Square choice = chooser.randomFromSet(possibleConnections.keySet());	
				
				// displace a square from the target tile that isn't the one where the displaced square will attach
				Set<Square> except = new HashSet<Square>(); except.add(choice);
				currentTile = Partitioner.getTileContaining(choice);
				System.out.println("aa " + currentTile);
				Square next = TileAction.displaceSquare(currentTile, environment, chooser, except);
				
				if ( next != null ) {
					
					List<Action> actions = new ArrayList<Action>();
					Action action = new Action(ace, Act.ATTACH, possibleConnections.get(choice));
					actions.add(action);
					Action.makeMoves(actions);
				}
				
				ace = next; steps++;
				//finished = true;
			}
		}
	}
	
	public static Map<Square, Sense> selectCandidateTargetSquares(Square source, Map<Square, Set<Sense>> environment, Set<Square> except) {
		
		// candidates are squares neighbouring 'source' that aren't in 'except'
		List<Square> cadidateTargets = new ArrayList<>();
		Map<Square, Sense> neighbours = mapSquareToSense(environment.get(source));
		cadidateTargets.addAll(neighbours.keySet());
		cadidateTargets.removeAll(except);
		for ( Square exception: except )  neighbours.remove(exception);
		return neighbours;
	}
	
	public static Map<Integer, Set<Square>> selectCandidateTargetTiles(Square square, Map<Square, Set<Sense>> environment) {
		
		Set<Sense> visible = environment.get(square);
		Map<Square, Sense> map = mapSquareToSense(visible);
		
		Map<Integer, Set<Square>> localTiles = Partitioner.partition(map.keySet());
		Map<Integer, Integer> sizes = Partitioner.getPartitionSizes(localTiles);
		System.out.println(Pentomino.getPentominoes(localTiles));
		System.out.println("xxxx " + sizes);
		
		return localTiles;		
	}
	
	
	public static Map<Square, Sense> mapSquareToSense(Set<Sense> observations) {
		
		Map<Square, Sense> squareToSense = new HashMap<>();
		for ( Sense sense: observations )  squareToSense.put(sense.getSquare(), sense);
		return squareToSense;
	}

	
	public static Square displace(Square ace, Map<Square, Set<Sense>> environment, Chooser chooser) {
		
		Set<Square> activeTile = new HashSet<>(); activeTile.add(ace);
		Square detached = null;
		
		// key = squares in neighbouring tiles, values = squares in this tile that can see them
		Map<Square, Set<Square>> squaresThatCanSee = new HashMap<>();
				
		// find all the squares that can be "seen" from any tile in the active tile
		Set<Square> visible = new HashSet<Square>();
		
		for ( Square square: activeTile ) {
			
			Set<Sense> seen = environment.get(square);
			for ( Sense sense: seen ) {
				
				visible.add(sense.getSquare());
				Maps.addMapValue(squaresThatCanSee, sense.getSquare(), square);
			}
		}
		
		// construct the local neighbourhood, excluding the active tile
		Set<Square> neighbourhood = new HashSet<Square>();
		//neighbourhood.addAll(activeTile);
		neighbourhood.addAll(visible);
		
		// partition it
		Map<Integer, Set<Square>> nextMap = Partitioner.partition(neighbourhood);
		Map<Integer, Set<Integer>> sizeMap = Partitioner.collectBySize(nextMap);
	
		if ( sizeMap.get(4) == null ) {
			
			detached = TileAction.displaceSquare(activeTile, environment, chooser, new HashSet<Square>());
		}
		else {
			
			visible.retainAll(nextMap.get(chooser.randomFromSet(sizeMap.get(4))));
			Square target = chooser.randomFromSet(visible);
			
			for ( Sense sense: environment.get(ace) ) {
				
				if ( sense.getSquare() == target ) {
					
					List<Action> actions = new ArrayList<>();
					Action action = new Action();
					action.setAct(Act.ATTACH); action.setActor(ace); action.setSense(sense);
					actions.add(action);
					
					Action.makeMoves(actions);
				}
			}
		}
		
		return detached;
	}

}
