package orient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class Chooser {

	private Random random;

	public Chooser() {

		random = new Random();
	}

	public Chooser(long seed) {

		random = new Random(seed);
	}

	public <T> T randomFromSet(Set<T> candidates) {
		
		List<T> list = new ArrayList<>();
		list.addAll(candidates);
		return randomFromList(list);
	}

	public <T> T randomFromList(List<T> candidates) {
		
		T selected = (candidates.size() > 1) ? candidates.get(random.nextInt(candidates.size())) : candidates.get(0);
		return selected;
	}

	
	public Integer randomSmallestPartion(Map<Integer, Set<Integer>> sizeMap, List<Integer> sizes) {
		
		// select a partition number random from a smallest partition
		
		Collections.sort(sizes);		
		int smallestSize = sizes.get(0);
		
		// make a list of the smallest partitions
		List<Integer> smallest = new ArrayList<Integer>();
		smallest.addAll(sizeMap.get(smallestSize));

		// select randomly from this list
		return smallest.get(random.nextInt(smallest.size()));
	}

	
	public <T> T randomFromSmallestPartion(Map<Integer, Set<T>> partitionMap, Map<Integer, Set<Integer>> sizeMap, List<Integer> sizes) {
		
		// select a candidate at random from a smallest partition

		// get a smallest partition
		Integer p = randomSmallestPartion(sizeMap, sizes);		
		Set<T> partition = partitionMap.get(p);

		// select randomly from this set
		return randomFromSet(partition);
	}

	
	public <T> T randomFromLargestPartion(Map<Integer, Set<T>> partitionMap, Map<Integer, Set<Integer>> sizeMap, List<Integer> sizes) {
		
		// select a candidate at random from a largest partition
		
		Collections.sort(sizes);		
		int largestSize = sizes.get(sizes.size()-1);
		
		// make a list of the smallest partitions
		List<Integer> largest = new ArrayList<Integer>();
		largest.addAll(sizeMap.get(largestSize));
		
		// choose one and get the associated set
		Integer p = largest.get(random.nextInt(largest.size()));		
		Set<T> partition = partitionMap.get(p);

		// select randomly from this set
		return randomFromSet(partition);
	}
	
	
	public Random getRandom() {
		
		return random;
	}

}
