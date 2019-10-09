import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jaden
 *
 */
public class Query  {
	
	/**
	 * 
	 */
	private final TreeSet<String> words;
	
	/**
	 * 
	 */
	public Query() {
		this.words = new TreeSet<>(); 
	}
		
	/**
	 * @return set of words
	 */
	public Set<String> getWords(){
		return Collections.unmodifiableSet(this.words);
	}
	/**
	 * @param word
	 */
	public void add(String word) {
		 this.words.add(word);
	}
	/**
	 * @return number of words
	 */
	public int size() {
		return this.words.size();
	}
	public String toString() {
		return String.join(" ", this.words);
		
	}
	
	/**
	 * @param o
	 * @return int
	 */
	public int compareTo(Query o) {
		return this.toString().compareTo(o.toString());
		
	}

}
