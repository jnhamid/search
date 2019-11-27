import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * This in a interface for QueryBuilder
 *
 * @author Jaden
 *
 */
public interface QueryBuilderInterface {
	/**
	 * A getter for query lines
	 *
	 * @return a set of Queries
	 */
	public Set<String> getQueryLines();

	/**
	 * a getter for Results
	 *
	 * @param queryLine the query
	 * @return a list of results based on queryline
	 */
	public List<InvertedIndex.Result> getQueryResults(String queryLine);

	/**
	 * Will write to from path filename
	 *
	 * @param fileName the path being written
	 * @throws IOException
	 */
	public void write(Path fileName) throws IOException;

	/**
	 * Gets queries from the input path and performs the searches.
	 *
	 * @param path        The path to the Query file.
	 * @param exactSearch True if we are doing exact search.
	 * @throws IOException
	 */
	public default void makeQueryFile(Path path, boolean exactSearch) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				makeQueryLine(query, exactSearch);
			}
		}
	}

	/**
	 * @return true is the set of queries is empty
	 */
	public boolean isEmpty();

	/**
	 * will make query line
	 *
	 * @param line        the line being made
	 * @param exactSearch whether or not exactSearch
	 */
	void makeQueryLine(String line, boolean exactSearch);

}
