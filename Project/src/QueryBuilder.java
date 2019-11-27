import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This class builds queries
 *
 * @author Jaden
 */
public class QueryBuilder implements QueryBuilderInterface {

	/**
	 * Index
	 */
	private final InvertedIndex index;

	/**
	 * Set of Queries mapped to results
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;

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
	@Override
	public Set<String> getQueryLines() {
		return Collections.unmodifiableSet(this.querySet.keySet());
	}

	/**
	 * A getter for resutls of query lines
	 *
	 * @param queryLine the query you want results for
	 * @return a list of results
	 */
	@Override
	public List<InvertedIndex.Result> getQueryResults(String queryLine) {
		ArrayList<InvertedIndex.Result> line = this.querySet.get(queryLine);
		if (line != null) {
			return Collections.unmodifiableList(line);
		}
		return Collections.emptyList();

	}

	/**
	 * will write query from path
	 *
	 * @param fileName path of output file
	 * @throws IOException
	 */
	@Override
	public void write(Path fileName) throws IOException {
		SimpleJsonWriter.asQuery(this.querySet, fileName);
	}

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line        The line we are parsing.
	 * @param exactSearch Wether we are doing exact search or not.
	 */
	@Override
	public void makeQueryLine(String line, boolean exactSearch) {
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);

		if (queries.size() == 0) {
			return;
		}

		String joined = String.join(" ", queries);

		if (querySet.containsKey(joined)) {
			return;
		}

		ArrayList<InvertedIndex.Result> local = index.search(queries, exactSearch);
		this.querySet.put(joined, local);
	}

	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	@Override
	public boolean isEmpty() {
		return this.querySet.keySet().size() == 0;
	}

}