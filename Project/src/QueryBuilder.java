import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author Jaden
 *
 */
public class QueryBuilder {

	/**
	 * Index
	 */
	private final InvertedIndex index; 

	/**
	 * qSet
	 */
	private TreeMap<String, ArrayList<Result>> qSet; // TODO final, better name

	/**
	 * Snowball Stemmer
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Constructor
	 * 
	 * @param index InvertedIndex that the queries are being built on
	 * @throws IOException
	 */
	public QueryBuilder(InvertedIndex index) throws IOException { // TODO Remove throws IOException
		this.index = index;
		this.qSet = new TreeMap<>();
	}

	/**
	 * getter for qset
	 * 
	 * @return an unmodifiable map of this.qSet
	 */
	public Map<String, ArrayList<Result>> qSet() {
		// TODO Better name, also still breaks encapsulation because of the nested data (the list is still modifiable)
		return Collections.unmodifiableMap(this.qSet);

		/*
		 * TODO 
		 * Create getters that unnest the data
		 * public Set<String> getQueryLines()
		 * public List<Result> getQueryResults(String queryLine)
		 */
	}

	/**
	 * makes the Queries
	 * 
	 * @param qPath path of query
	 * 
	 * @throws IOException
	 */
	// TODO public void makeQuery(Path qPath, boolean exact) throws IOException {
	public void makeQuery(Path qPath) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(qPath, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> query = TextFileStemmer.uniqueStems(line);
				String joined = String.join(" ", query);
				if (query.size() != 0 && !qSet.containsKey(joined)) {
					// TODO search here, avoid the loop later with the exactSesrch partialSerach methods...
					this.qSet.put(joined, null);
				}

			}
		}
	}

	/**
	 * This function will do an exact search on the queries.
	 */
	public void exactSearch() { // TODO Remove
		for (String query : this.qSet.keySet()) {
			this.qSet.put(query, this.index.getResults(query));
		}

	}

	/**
	 * @return an unmodifiableMap of ArrayList
	 */
	public Map<String, ArrayList<Result>> getQuerySet() { // TODO Remove
		return Collections.unmodifiableMap(qSet);
	}

	/**
	 * This function does Partial search.
	 */ 
	public void partialSearch() {  // TODO Remove
		for (String query : this.qSet.keySet()) {
			ArrayList<Result> results = new ArrayList<>();
			for (String queryWord : query.split(" ")) {
				for (String indexWord : index.getWords()) {
					// TODO Can just do startsWith...
					if (indexWord.startsWith(queryWord) || indexWord.equals(queryWord)) {
						results.addAll(this.index.makeResult(indexWord));
					}
				}
			}

			results = InvertedIndex.mergeDuplicates(results);
			Collections.sort(results);
			this.qSet.put(query, results);
		}
		
		/*
		 * TODO If the search results break without a query builder class, then that
		 * action needs to be in the inverted index.
		 */

	}
}