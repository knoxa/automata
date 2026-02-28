package orient;

import java.util.Map;
import java.util.Set;

import cells.Sense;
import cells.Square;

public class State {

	Map<Integer, Set<Square>> partitionMap = null;
	Map<Square, Set<Sense>> environment = null;
	Map<Integer, Set<Integer>> partitionGraph = null;	
	Map<Integer, Integer> partitionSizes = null;
	
	public Map<Integer, Set<Square>> getPartitionMap() {
		return partitionMap;
	}
	public void setPartitionMap(Map<Integer, Set<Square>> partitionMap) {
		this.partitionMap = partitionMap;
	}
	public Map<Square, Set<Sense>> getEnvironment() {
		return environment;
	}
	public void setEnvironment(Map<Square, Set<Sense>> environment) {
		this.environment = environment;
	}
	public Map<Integer, Set<Integer>> getPartitionGraph() {
		return partitionGraph;
	}
	public void setPartitionGraph(Map<Integer, Set<Integer>> partitionGraph) {
		this.partitionGraph = partitionGraph;
	}
	public Map<Integer, Integer> getPartitionSizes() {
		return partitionSizes;
	}
	public void setPartitionSizes(Map<Integer, Integer> partitionSizes) {
		this.partitionSizes = partitionSizes;
	}
	
	
}
