import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * A threadSafe version of QueryBuilder
 * 
 * @author Jaden
 *
 */
public class ThreadSafeQueryBuilder extends QueryBuilder {

	/**
	 * workQueue
	 */
	WorkQueue workQueue;

	/**
	 * @param index
	 */
	public ThreadSafeQueryBuilder(ThreadSafeInvertedIndex index) {
		super(index);
		new TreeMap<>();
	}

	/**
	 * A getter for query lines
	 * 
	 * @return a set of query lines
	 */
	@Override
	public Set<String> getQueryLines() {
		return super.getQueryLines();

	}

	/**
	 * A getter for resutls of query lines
	 * 
	 * @param queryLine the query you want results for
	 * @return a list of results
	 */
	@Override
	public List<InvertedIndex.Result> getQueryResults(String queryLine) {
		return super.getQueryResults(queryLine);

	}

	/**
	 * will write query from path
	 * 
	 * @param fileName path of output file
	 * @throws IOException
	 */
	@Override
	public void write(Path fileName) throws IOException {
		super.write(fileName);
	}

	/**
	 * Function that checks if the map is empty.
	 *
	 * @return True if empty.
	 */
	@Override
	public boolean isEmpty() {
		return super.isEmpty();

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
		this.workQueue = new WorkQueue(numThreads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				workQueue.execute(new Task(query, exactSearch));
			}
		}try {
			workQueue.finish();
		} catch (Exception e) {

		}
		workQueue.shutdown();

	}

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line        The line we are parsing.
	 * @param exactSearch Wether we are doing exact search or not.
	 */
	@Override
	public void makeQueryLine(String line, boolean exactSearch) {
		super.makeQueryLine(line, exactSearch);
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
		 * @param line
		 * @param exact
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			synchronized (workQueue) {
				makeQueryLine(line, exact);
			}
		}
	}
}
