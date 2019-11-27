import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 *
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		int numThreads = 1;
		InvertedIndex index;
		// store initial start time
		Instant start = Instant.now();
		InvertedIndexBuilder indexBuilder;
		ArgumentParser parse = new ArgumentParser(args);
		QueryBuilderInterface qBuilder;
		WorkQueue queue = null;

		/*
		 * This if builds the InvertedIndex if has the flag "-path"
		 */
		if (parse.hasFlag("-threads")) {
			try {
				numThreads = Integer.parseInt(parse.getString("-threads"));
				if (numThreads == 0) {
					numThreads = 5;
				}
			} catch (Exception e) {
				numThreads = 5;
			}
			queue = new WorkQueue(numThreads);
//			index = new ThreadSafeInvertedIndex();
//			indexBuilder = new ThreadSafeInvertedIndexBuilder((ThreadSafeInvertedIndex) index, queue);
//			qBuilder = new ThreadSafeQueryBuilder((ThreadSafeInvertedIndex) index, queue);

			ThreadSafeInvertedIndex threadSafe = new ThreadSafeInvertedIndex();
			index = threadSafe;
			indexBuilder = new ThreadSafeInvertedIndexBuilder(threadSafe, queue);
			qBuilder = new ThreadSafeQueryBuilder(threadSafe, queue);

		} else {
			index = new InvertedIndex();
			indexBuilder = new InvertedIndexBuilder(index);
			qBuilder = new QueryBuilder(index);

		}

		if (parse.hasFlag("-path") && parse.getPath("-path") != null) {
			Path path = parse.getPath("-path");
			try {
				if (Files.exists(path)) {
					indexBuilder.build(path);
				}
			} catch (IOException e) {
				System.out.println("File is a directory");
			}
		}
		/*
		 * This if writes the output file to the path with flag "-index"
		 */
		if (parse.hasFlag("-index")) {
			Path path = parse.getPath("-index", Path.of("index.json"));
			try {
				index.printIndex(path);
			} catch (IOException e) {
				System.out.println("Unable to write the index to path: " + path);
			}
		}
		/*
		 * This if writes the output file to the path with flag "-counts"
		 */
		if (parse.hasFlag("-counts")) {
			Path path = parse.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(index.getCount(), path);
			} catch (IOException e) {
				System.out.println("Unable to write the word counts to path: " + path);
			}
		}
		/*
		 * This if writes the output file to the path with flag "-query"
		 */
		if (parse.hasFlag("-query") && parse.getPath("-query") != null) {
			Path path = parse.getPath("-query");
			try {

				qBuilder.makeQueryFile(path, parse.hasFlag("-exact"));
			} catch (IOException e) {
				System.out.println("Unable to read the query file" + path.toString());

			} catch (Exception s) {
				System.out.println("Unable to do something to the query file" + path.toString());
				s.printStackTrace();
			}
		}
		/*
		 * This if writes the output file to the path with flag "-results"
		 */
		if (parse.hasFlag("-results")) {
			Path path = parse.getPath("-results", Path.of("results.json"));
			try {
				qBuilder.write(path);
			} catch (IOException e) {
				System.out.println("Cannot write results from path: " + path.toString());
			}
		}

		/*
		 * TODO if (queue != null) { shutdown }
		 */

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
