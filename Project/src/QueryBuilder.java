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
	private TreeMap<String, ArrayList<Result>> qSet;


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
	public Map<String, ArrayList<Result>> qSet() {
		return Collections.unmodifiableMap(this.qSet);

	}

	/**
	 * makes the Queries
	 * @param qPath 
	 * 
	 * @throws IOException
	 */
	public void makeQuery(Path qPath) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(qPath, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> query = TextFileStemmer.uniqueStems(line);
				String joined = String.join(" ", query);
				//System.out.println(joined);
				if(query.size() != 0 && !qSet.containsKey(joined)){
					this.qSet.put(joined, null);
				}

			}
		}
	}

	/**
	 * This function will trigger an exact search on the queries.
	 * @return an arraylist of SearchResults
	 */
	public ArrayList<Result> exactSearch() {
		ArrayList<Result> results = new ArrayList<>();
		for(String query: this.qSet.keySet()) {
			if(this.index.contains(query)) {
				for(String fileName : this.index.getLocations(query)){
					System.out.println(fileName);
					results.addAll(this.index.getResults(fileName));
					System.out.println(results.toString());
	
				}
			}
			Collections.sort(results);
			//System.out.println("query: " + query + " result: " + results.toString());
			
			this.qSet.put(query, results);
		}
		results = InvertedIndex.mergeDuplicates(results);
		Collections.sort(results);
		return results;
	}
	
	/**
	 * @return map
	 */
	public Map<String, ArrayList<Result>> getQuerySet(){
		return Collections.unmodifiableMap(qSet);
	}

	/**
	 * This function does Partial search.
	 */
	public void partialSearch() {
		for (String query : this.qSet.keySet()) {
			ArrayList<Result> results = new ArrayList<>();
			for (String queryWord : query.split(" ")) {
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