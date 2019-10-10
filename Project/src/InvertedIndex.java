import java.util.*;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Jnhamid push1
 */
public class InvertedIndex {

	/**
	 * Private Identifier for our Custom data structure
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	/**
	 * Map for count
	 */
	private final TreeMap<String, Integer> count;

	/**
	 * Initial map
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
		count = new TreeMap<>();
	}

	/**
	 * Will add element to our Inverted Index.
	 * 
	 * @param word
	 * @param file
	 * @param pos
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
	 * @param word
	 * @return makes results
	 */
	public ArrayList<Result> makeResult(String word) {
		ArrayList<Result> results = new ArrayList<>();

		if (this.hasWord(word)) {
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
	 * @param results
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
	public ArrayList<Result> getResults(Query query) {
		ArrayList<Result> results = new ArrayList<>();

		for (String word : query.getWords()) {

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
	 * will output to file, using a modified function from hw
	 * 
	 * @param outFile
	 * @throws IOException
	 */
	public void printIndex(String outFile) throws IOException {
		SimpleJsonWriter.asDoubleNested(index, Path.of(outFile));
	}

	/**
	 * @return count TreeMap
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(count);
	}

	/**
	 * @return keyset of index
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.index.keySet());
	}

	/**
	 * @param word
	 * @param path
	 * @return if word has path
	 */
	public boolean hasWord(String word, String path) {
		return hasWord(word) ? index.get(word).containsKey(path) : false;

	}

	/**
	 * @param word
	 * @return if index has word
	 */
	private boolean hasWord(String word) {

		return index.containsKey(word);
	}

}
