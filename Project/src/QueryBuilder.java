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
	 * makes the Queries
	 * 
	 * @param qPath path of query
	 * @param exact whether or not a
	 * 
	 * @throws IOException
	 */
	public void makeQuery(Path qPath, Boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(qPath, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> query = TextFileStemmer.uniqueStems(line);
				String joined = String.join(" ", query);
				if (query.size() != 0 && !querySet.containsKey(joined)) {
					this.querySet.put(joined, index.search(query, exact));
				}

			}
		}
	}
}