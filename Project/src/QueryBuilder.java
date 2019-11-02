import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import opennlp.tools.stemmer.Stemmer;
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
	private TreeMap<Query, ArrayList<Result>> qSet;


	/**
	 * Snowball Stemmer
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * @param index
	 * @throws IOException
	 */
	public QueryBuilder(InvertedIndex index) throws IOException {
		this.index = index;
		this.qSet = new TreeMap<>();
	}

	/**
	 * @return this.qSet
	 */
	public Map<Query, ArrayList<Result>> qSet() {
		return Collections.unmodifiableMap(this.qSet);

	}

	/**
	 * makes the Queries
	 * @param qPath 
	 * 
	 * @throws IOException
	 */
	public void makeQuery(Path qPath) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(qPath, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				Query query = new Query();
				String[] parsedLine = TextParser.parse(line);
				for (String words : parsedLine) {
					String stemmed = stemmer.stem(words).toString();
					query.add(stemmed);

				}
				if (query.size() != 0) {
					this.qSet.put(query, null);
				}

			}
		}
	}

	/**
	 * This function will trigger an exact search on the queries.
	 */
	public void exactSearch() {
		for (Query query : this.qSet.keySet()) {
			this.qSet.put(query, this.index.getResults(query));
		}
	}
	
	/**
	 * @return map
	 */
	public Map<Query, ArrayList<Result>> getQuerySet(){
		return Collections.unmodifiableMap(qSet);
	}

	/**
	 * This function does Partial search.
	 */
	public void partialSearch() {
		for (Query query : this.qSet.keySet()) {
			ArrayList<Result> results = new ArrayList<>();
			for (String queryWord : query.getWords()) {
				for (String indexWord : index.getWords()) {
					if (indexWord.startsWith(queryWord) || indexWord.equals(queryWord)) {
						results.addAll(this.index.makeResult(indexWord));
					}
				}
			}

			results = InvertedIndex.mergeDuplicates(results);
			Collections.sort(results);
			this.qSet.put(query, results);
		}

	}
}