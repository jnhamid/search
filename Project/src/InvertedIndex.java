import java.util.*;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Jnhamid This is the class for our Custom Data Structure
 */
public class InvertedIndex {

	/**
	 * Private Identifier for our Custom data structure
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	/**
	 * Private Identifier for count data structure
	 */
	private final TreeMap<String, Integer> count;

	/**
	 * Initialize maps
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
		count = new TreeMap<>();
	}

	/**
	 * Will add element to our Inverted Index.
	 * 
	 * @param word to be added
	 * @param file to be added
	 * @param pos  to be added
	 * @throws IOException
	 * 
	 */
	public void addElement(String word, String file, Integer pos) throws IOException {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(file, new TreeSet<Integer>());
		index.get(word).get(file).add(pos);
		count.putIfAbsent(file, pos);
		if (pos > count.get(file)) {
			count.put(file, pos);
		}

	}

	/**
	 * This function will make the result entry 
	 * @param word word being searched in index
	 * @return an arraylist of results
	 */
	public ArrayList<Result> makeResult(String word) {
		ArrayList<Result> results = new ArrayList<>();

		if (this.contains(word)) {
			var files = this.index.get(word).keySet();
			for (String file : files) {
				Result result = new Result();
				result.setFileName(file);
				result.setCount(this.index.get(word).get(file).size());
				result.setScore((double) result.getCount() / count.get(file));

				results.add(result);
			}
		}
		return results;
	}

	/**
	 * Given a TreeSet of results will merge duplicates by file.
	 *
	 * @param results arraylist that has duplicates
	 * @return a merged TreeSet of Results.
	 */
	public static ArrayList<Result> mergeDuplicates(ArrayList<Result> results) {
		ArrayList<Result> merged = new ArrayList<>();

		for (Result result : results) {
			boolean mergeHappened = false;
			for (Result mergedResult : merged) {
				if (mergedResult.sameFileName(result)) {
					mergedResult.setScore(mergedResult.getScore() + result.getScore());
					mergedResult.setCount(mergedResult.getCount() + result.getCount());
					mergeHappened = true;
				}
			}
			if (!mergeHappened) {
				merged.add(result);
			}
		}
		return merged;
	}

	/**
	 * Returns TreeSet of Results given a query.
	 *
	 * @param query Current query.
	 * @return A set of Results associated to a query.
	 */
	public ArrayList<Result> getResults(String query) {
		ArrayList<Result> results = new ArrayList<>();

		for (String word : query.split(" ")) {

			ArrayList<Result> r = makeResult(word);

			for (Result q : r) {
				results.add(q);
			}

		}

		results = mergeDuplicates(results);
		Collections.sort(results);
		return results;
	}

	/**
	 * will output to file using method in SimpleJsonWriter
	 * 
	 * @param outFile file name of file that is getting written
	 * @throws IOException
	 */
	public void printIndex(String outFile) throws IOException {
		SimpleJsonWriter.asDoubleNested(index, Path.of(outFile));
	}

	/**
	 * getter for count
	 * 
	 * @return count as a unmodifiableMap
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(count);
	}

	/**
	 * checks to see if word is in index
	 * 
	 * @param word word that is getting checked
	 * @return if index has word
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}

	/**
	 * checks if the map has the specific word and if word contain path.
	 * 
	 * @param word word that is getting checked
	 * @param path path that is getting checked
	 * @return if has word and path
	 */
	public boolean contains(String word, String path) {
		return contains(word) && index.get(word).containsKey(path);
	}

	/**
	 * checks if the map contains the specific word, path and index.
	 * 
	 * @param word    word that is getting checked
	 * @param path    path that is getting checked
	 * @param postion postion that is getting checked
	 * @return if has word and path at postion
	 */
	public boolean contains(String word, String path, int postion) {
		return contains(word, path) && index.get(word).get(path).contains(postion);
	}

	/**
	 * getter for set of words
	 * 
	 * @return an unmodifiable set of words
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());

	}

	/**
	 * getter for set of locations
	 * 
	 * @param word key value for locations
	 * @return an unmodifiable set of Locations
	 */
	public Set<String> getLocations(String word) {
		if (contains(word)) {
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		return Collections.emptySet();

	}

	/**
	 * getter for set of postions
	 * 
	 * @param word
	 * @param location
	 * @return an unmodifiable set of Positions
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (contains(word, location)) {
			return Collections.unmodifiableSet(index.get(word).get(location));
		}
		return Collections.emptySet();

	}

	/**
	 * returns how many words is in inverted index.
	 * 
	 * @return size of inverted index.
	 */
	public int size() {
		return index.size();
	}

	/**
	 * returns how many paths are found in word.
	 * 
	 * @param word to check
	 * @return amount of paths, return 0 if not found
	 */
	public int size(String word) {
		return contains(word) ? index.get(word).size() : 0;
	}

}
