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
 *
 * @author Jaden
 *
 */
public class ThreadSafeQueryBuilder implements QueryBuilderInterface {

	/**
	 * workQueue
	 */
	private final WorkQueue workQueue;

	/**
	 * the index
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * The set of queries
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> querySet;

	/**
	 * TODO 
	 * @param index     index the query is being built from
	 * @param workQueue the work queue
	 */
	public ThreadSafeQueryBuilder(ThreadSafeInvertedIndex index, WorkQueue workQueue) {
		this.index = index;
		this.querySet = new TreeMap<>();
		this.workQueue = workQueue;
	}

	{ // TODO Clean

	}

	/**
	 * A getter for query lines
	 *
	 * @return a set of query lines
	 */
	@Override
	public Set<String> getQueryLines() {
		synchronized (querySet) {
			return Collections.unmodifiableSet(querySet.keySet());
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
			// TODO Check for null like you did before
			return Collections.unmodifiableList(querySet.get(queryLine));
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
			return this.querySet.size() == 0;
		}

	}

	/**
	 * Gets queries from the input path and performs the searches.
	 *
	 * @param path        The path to the Query file.
	 * @param exactSearch True if we are doing exact search.
	 * @throws IOException Could happen.
	 */
	@Override
	public void makeQueryFile(Path path, boolean exactSearch, int numThreads) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				workQueue.execute(new Task(query, exactSearch));
			}
		}
		try {
			workQueue.finish();
		} catch (Exception e) {

		}
		// workQueue.shutdown();

		/* TODO
		QueryBuilderInterface.super.makeQueryFile(path, exactSearch);
		workQueue.finish();
		*/
	}

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line        The line we are parsing.
	 * @param exactSearch Wether we are doing exact search or not.
	 */
	@Override
	public void makeQueryLine(String line, boolean exactSearch) {
		// TODO Should just create a task and add to the work queue
		// TODO Move this code into run()
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);

		if (queries.size() == 0) {
			return;
		}

		String joined = String.join(" ", queries);
		synchronized (querySet) {
			if (querySet.containsKey(joined)) {
				return;
			}
		}

		ArrayList<InvertedIndex.Result> local = index.search(queries, exactSearch);
		synchronized (querySet) {
			this.querySet.put(joined, local);
		}

	}

	/**
	 * task class
	 *
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

		/**
		 * Construtor
		 *
		 * @param line  the line to be made
		 * @param exact whether or not a exact search or nor
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			makeQueryLine(line, exact);

		}
	}
}
