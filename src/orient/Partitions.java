package orient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import orient.State;
import cakes.category.Maps;
import cells.Direction;
import cells.Sense;
import cells.Square;

public class Partitions {

	public static Map<Integer, Set<Square>> partition(Set<Square> squares) {
		
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
	
	
	public static void extendPartition(Square square, Set<Square> partition) {
		
		if ( square.neighbourCount() == 0 ) {
			//System.out.println("NO NEIGHBOURS");
			return;
		}
		
		//System.out.println(square);
		Set<Square> neighbours = square.getNeighbours();
		neighbours.removeAll(partition);
		//System.out.println(neighbours);
		//System.out.println(partition);
		
		for ( Square s: neighbours ) {
			
			partition.add(s);
			extendPartition(s, partition);
		}	
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
					
					Square thisSquare = neighbour.getNeighbour(square.getReturnDirection(direction));
					
					if ( thisSquare == null || thisSquare != square ) {
						
						System.err.println("DIRECTIONS DON'T MATCH: " + square);
						System.err.println(square + "-- " + direction + " --> " + neighbour);
						System.err.println(neighbour + "-- " + square.getReturnDirection(direction) + " --> " + thisSquare);
						System.exit(0);
					}
				}
			}
		}
	}
	
	public static void partitionGraph(State state) {
		
		Map<Integer, Set<Square>> partitionMap = state.partitionMap;
		Map<Square, Set<Sense>>   environment  = state.environment;
		
		Map<Integer, Set<Integer>> graph = new HashMap<Integer, Set<Integer>>();		
		Map<Integer, Integer> sizes = new HashMap<Integer, Integer>();		
		Map<Square, Set<Integer>> reverse = cakes.category.Maps.invertMap(partitionMap);
		
		for ( Integer partNo: partitionMap.keySet() ) {
			
			sizes.put(partNo, partitionMap.get(partNo).size());
		}
		
		for ( Square square: reverse.keySet() ) {
					
			int partNo = reverse.get(square).iterator().next();		
			Set<Sense> sensed = environment.get(square);
			
			for ( Sense sense: sensed ) {
				
				int neighbourPart = reverse.get(sense.getSquare()).iterator().next();
				if ( partNo != neighbourPart )  Maps.addMapValue(graph, partNo, neighbourPart);
			}
		}
		
		state.partitionGraph = graph;
		state.partitionSizes = sizes;
	}
	
}
