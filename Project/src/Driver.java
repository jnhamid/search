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
		InvertedIndex index = new InvertedIndex();
		// store initial start time
		Instant start = Instant.now();

		ArgumentParser parse = new ArgumentParser(args);
		InvertedIndexBuilder indexBuilder = new InvertedIndexBuilder(index);
		QueryBuilder qBuilder = new QueryBuilder(index);
		/*
		 * This if builds the InvertedIndex if has the flag "-path"
		 */
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
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
