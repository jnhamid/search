import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jaden
 *
 */
public class Query implements Comparable<Query> {

	/**
	 * TreeSet of Strings that hold all -query flags
	 */
	private final TreeSet<String> words;

	/**
	 *  consturtor
	 */
	public Query() {
		this.words = new TreeSet<>();
	}

	/**
	 * return a unmodifiable set of the words 
	 * @return set of words
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.words);
	}

	/**
	 * simple add
	 * @param word
	 */
	public void add(String word) {
		this.words.add(word);
	}

	/**
	 * simple size function
	 * @return number of words
	 */
	public int size() {
		return this.words.size();
	}
	
	/**
	 * toString override
	 * @return String Query joined by spaces
	 */
	@Override
	public String toString() {
		return String.join(" ", this.words);

	}

	/**
	 * compareTo override
	 * @param o the Query getting compared
	 * @return int Same as any compareTo
	 */
	@Override
	public int compareTo(Query o) {
		return this.toString().compareTo(o.toString());

	}

}