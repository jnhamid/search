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

/**
 * A threadSafe version of QueryBuilder
 * @author Jaden
 *
 */
public class ThreadSafeQueryBuilder extends QueryBuilder {
	
	/**
	 * Index that the queries are being built for
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * The set that will hold cleaned up queries mapped to their results.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;
	
	/**
	 * @param index
	 */
	public ThreadSafeQueryBuilder(ThreadSafeInvertedIndex index) {
		super(index);
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
		synchronized (querySet) {
			return Collections.unmodifiableSet(this.querySet.keySet());
		}
		
	}

	/**
	 * A getter for resutls of query lines
	 * 
	 * @param queryLine the query you want results for
	 * @return a list of results
	 */
	@Override
	public List<InvertedIndex.Result> getQueryResults(String queryLine) {
		synchronized (querySet) {
			return Collections.unmodifiableList(this.querySet.get(queryLine));
		}
		
	}
	
	/**
	 * will write query from path
	 * 
	 * @param fileName path of output file
	 * @throws IOException
	 */
	@Override
	public void write(Path fileName) throws IOException {
		synchronized (querySet) {
			SimpleJsonWriter.asQuery(this.querySet, fileName);
		}	
	}
	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	@Override
	public boolean isEmpty() {
		synchronized (querySet) {
			return this.querySet.keySet().size() == 0;
		}
		
	}
	
	
	/**
	 * Gets queries from the input path and performs the searches.
	 *
	 * @param path The path to the Query file.
	 * @param exactSearch True if we are doing exact search.
	 * @throws IOException Could happen.
	 */
	@Override
	public void makeQueryFile(Path path, boolean exactSearch) throws IOException {
		WorkQueue queue = new WorkQueue(this.index.numThreads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				queue.execute(new Task(query, this.index, exactSearch));
			}
		}
		queue.shutdown();

	}

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line The line we are parsing.
	 * @param exactSearch Wether we are doing exact search or not.
	 */
	@Override
	public void makeQueryLine(String line, boolean exactSearch) {
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);
		String joined = String.join(" ", queries);
		if (queries.size() != 0 && !querySet.containsKey(joined)) {
			this.querySet.put(joined, index.search(queries, exactSearch));
		}
	}

	/**
	 * task class
	 * @author Jaden
	 *
	 */
	private class Task implements Runnable {
		/** The prime number to add or list. */
		private final String line;
		/**
		 * Whether or not a exactSearch
		 */
		private final boolean exact;

		private final ThreadSafeInvertedIndex index;

		/**
		 * Construtor
		 * @param line
		 * @param index
		 * @param exact
		 */
		public Task(String line, ThreadSafeInvertedIndex index, boolean exact) {
			this.line = line;
			this.index = index;
			this.exact = exact;
		}

		@Override
		public void run() {
			makeQueryLine(line, exact);
		}
	}
}
