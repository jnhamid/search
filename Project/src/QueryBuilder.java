import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
	 * Set of Queries mapped to results
	 */
	private TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;

	/**
	 * Snowball Stemmer
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Constructor
	 * 
	 * @param index InvertedIndex that the queries are being built on
	 */
	public QueryBuilder(InvertedIndex index) {
		this.index = index;
		this.querySet = new TreeMap<>();
	}

	/**
	 * A getter for query lines
	 * 
	 * @return a set of query lines
	 */
	public Set<String> getQueryLines() {
		return Collections.unmodifiableSet(this.querySet.keySet());
	}

	/**
	 * A getter for resutls of query lines
	 * 
	 * @param queryLine the query you want results for
	 * @return a list of results
	 */
	public List<InvertedIndex.Result> getQueryResults(String queryLine) {
		return Collections.unmodifiableList(this.querySet.get(queryLine));

	}

	/**
	 * will write query from path
	 * 
	 * @param fileName path of output file
	 * @throws IOException
	 */
	public void write(Path fileName) throws IOException {
		SimpleJsonWriter.asQuery(this.querySet, fileName);
	}

	/**
	 * Gets queries from the input path and performs the searches.
	 *
	 * @param path The path to the Query file.
	 * @param exactSearch True if we are doing exact search.
	 * @param numThreads number of threads for mulyithreading
	 * @throws IOException Could happen.
	 */
	public void makeQueryFile(Path path, boolean exactSearch, int numThreads) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				makeQueryLine(query, exactSearch);
			}
		}
	}

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line The line we are parsing.
	 * @param exactSearch Wether we are doing exact search or not.
	 */
	public void makeQueryLine(String line, boolean exactSearch) {
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);
		String joined = String.join(" ", queries);
		if (queries.size() != 0 && !querySet.containsKey(joined)) {
			this.querySet.put(joined, index.search(queries, exactSearch));
		}
	}
	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	public boolean isEmpty() {
		return this.querySet.keySet().size() == 0;
	}

}