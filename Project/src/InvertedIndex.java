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
	private boolean contains(String word) {
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
		return contains(word) ? index.get(word).containsKey(path) : false;
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
		return contains(word, path) ? index.get(word).get(path).contains(postion) : false;
	}

	/**
	 * checks if word has given position
	 * 
	 * @param location we looking for
	 * @param word     to look in
	 * @return if word exist
	 */
	public boolean hasLocation(String word, String location) {
		if (contains(word)) {
			return index.get(word).containsKey(location);
		}
		return false;
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
		return Collections.unmodifiableSet(index.get(word).keySet());
	}

	/**
	 * getter for set of postions
	 * 
	 * @param word
	 * @param location
	 * @return an unmodifiable set of Positions
	 */
	public Set<Integer> getPositions(String word, String location) {
		return Collections.unmodifiableSet(index.get(word).get(location));

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
