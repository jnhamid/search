import java.util.*;
import java.io.IOException;
import java.nio.file.Path;

/*
 * TODO Formatting. Javadoc. Needs more methods to make more reusable. Is it possible
 * to access safely all of the data stored by this class?
 */


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
