import java.util.*;
import java.io.IOException;
import java.nio.file.Path;

// TODO These are still issues: https://github.com/usf-cs212-fall2019/project-jnhamid/blob/b2c5b0772ba3c61240dac5f62cdfd2deacc90acf/Project/src/InvertedIndex.java#L6-L7

/*
 * TODO Is it possible to safely get all of the elements from index? Right now, NONE of the elements
 * can be returned. You have the contains methods, but not the get methods.
 * 
 * public Set<String> getWords()
 * public Set<String> getLocations(String word)
 * public Set<Integer> getPositions(String word, String location)
 */

/**
 * @author Jnhamid
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
	 * @param outFile
	 * @throws IOException
	 */
	public void printIndex(String outFile) throws IOException {
		SimpleJsonWriter.asDoubleNested(index, Path.of(outFile));
	}

	/**
	 * @return count as a unmodifiableMap
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(count);
	}

	// TODO Remove this---duplicate of contains(String, String)
	/**
	 * Checks to see if word is in index at certain path
	 * 
	 * @param word
	 * @param path
	 * @return if word has path
	 */
	public boolean hasWord(String word, String path) {
		return hasWord(word) ? index.get(word).containsKey(path) : false;

	}

	/*
	 * TODO Rename this to "contains" to match your other methods.
	 */
	/**
	 * checks to see if word is in index
	 * 
	 * @param word
	 * @return if index has word
	 */
	private boolean hasWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * checks if the map has the specific word and if word contain path.
	 * 
	 * @param word
	 * @param path
	 * @return if has word and path
	 */
	public boolean contains(String word, String path) {
		return hasWord(word) ? index.get(word).containsKey(path) : false;
	}

	/**
	 * checks if the map contains the specific word, path and index.
	 * 
	 * @param word
	 * @param path
	 * @param postion
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
		if (hasWord(word)) {
			return index.get(word).containsKey(location);
		}
		return false;
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
		return hasWord(word) ? index.get(word).size() : 0;
	}

}
