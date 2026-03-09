package orient;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import orient.State;
import worlds.Compass;
import cakes.category.Maps;
import cells.Direction;
import cells.Sense;
import cells.Square;

public class Partitioner {

	public static Map<Integer, Set<Square>> partition(Collection<Square> squares) {
		
		Set<Square> temp = new HashSet<Square>();
		temp.addAll(squares);
		Map<Integer, Set<Square>> partitionMap = new HashMap<Integer, Set<Square>>();
		int partitionNum = 0;
			
		while ( temp.size() > 0 ) {
			
			Set<Square> partition = new HashSet<Square>();			
			Square nextSquare = temp.iterator().next();
			partition.add(nextSquare);
			
			extendPartition(nextSquare, partition);
			temp.removeAll(partition);			
		
			checkPartition(partition);
			partitionMap.put(++partitionNum, partition);			
		}
		
		return partitionMap;
	}
	
	
	private static void extendPartition(Square square, Set<Square> partition) {
		
		if ( square.neighbourCount() == 0 )  return;
		
		Set<Square> neighbours = square.getNeighbours();
		neighbours.removeAll(partition);
		
		for ( Square s: neighbours ) {
			
			partition.add(s);
			extendPartition(s, partition);
		}	
	}


	public static <T> Map<Integer, Set<Integer>> collectBySize(Map<Integer, Set<T>> partitionMap) {
		
		Map<Integer, Set<Integer>> sizeMap = new HashMap<>();
		
		for ( Integer partNo: partitionMap.keySet() ) {
			
			Maps.addMapValue(sizeMap, partitionMap.get(partNo).size(), partNo);
		}
		
		return sizeMap;
	}
	

	public static void checkPartition(Set<Square> partition) {
		
		for ( Square square: partition ) {
			
			Set<Square> neighbours = square.getNeighbours();
			
			if ( !partition.containsAll(neighbours) ) {
				
				System.err.println("PARTITION NOT CLOSED");				
			}
			
			for ( Direction direction: new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST } ) {
				
				Square neighbour = square.getNeighbour(direction);
				
				if ( neighbour != null ) {
					
					Square thisSquare = neighbour.getNeighbour(Compass.getReturnDirection(direction));
					
					if ( thisSquare == null || thisSquare != square ) {
						
						System.err.println("DIRECTIONS DON'T MATCH: " + square);
						System.err.println(square + "-- " + direction + " --> " + neighbour);
						System.err.println(neighbour + "-- " + Compass.getReturnDirection(direction) + " --> " + thisSquare);
						System.exit(0);
					}
				}
			}
		}
	}
	
	public static void partitionGraph(State state) {
		
		Map<Integer, Integer> sizes = new HashMap<Integer, Integer>();		

		for ( Integer partNo: state.partitionMap.keySet() ) {
			
			sizes.put(partNo, state.partitionMap.get(partNo).size());
		}
		state.partitionGraph = getPartitionGraph(state.getPartitionMap(), state.getEnvironment());
		state.partitionSizes = sizes;
	}
	
	
	public static Map<Integer, Integer> getPartitionSizes(Map<Integer, Set<Square>> partitionMap) {
		
		Map<Integer, Integer> sizes = new HashMap<Integer, Integer>();		
		for ( Integer partNo: partitionMap.keySet() )  sizes.put(partNo, partitionMap.get(partNo).size());	
		return sizes;
	}
	
	
	public static Map<Integer, Set<Integer>> getPartitionGraph(Map<Integer, Set<Square>> partitionMap, Map<Square, Set<Sense>> environment) {
		
		Map<Integer, Set<Integer>> graph = new HashMap<Integer, Set<Integer>>();		
		Map<Square, Set<Integer>> reverse = cakes.category.Maps.invertMap(partitionMap);

		for ( Square square: reverse.keySet() ) {
					
			int partNo = reverse.get(square).iterator().next();		
			Set<Sense> sensed = environment.get(square);
			
			for ( Sense sense: sensed ) {
				
				//System.out.println(sense);
				//System.out.println(sense.getSquare());
				//System.out.println(reverse);
				
				Set<Integer> values = reverse.get(sense.getSquare());
				
				if ( values != null ) {
					
					int neighbourPart = values.iterator().next();
					if ( partNo != neighbourPart )  Maps.addMapValue(graph, partNo, neighbourPart);
				}
			}
		}
		
		return graph;
	}
	
}
