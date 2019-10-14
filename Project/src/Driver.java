import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/*
 * TODO There are 2 warnings. I will not pass code with warnings in it.
Javadoc: The method asNestedObject(TreeMap<String,TreeSet<Integer>>, Writer, int) in the type SimpleJsonWriter is not applicable for the arguments (Map, Writer, int)	SimpleJsonWriter.java	/Project/src	line 167	Java Problem
Javadoc: The method asNestedObject(TreeMap<String,TreeSet<Integer>>, Writer, int) in the type SimpleJsonWriter is not applicable for the arguments (Map, Writer, int)	SimpleJsonWriter.java	/Project/src	line 220	Java Problem
 */

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
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		InvertedIndex index = new InvertedIndex();
		// store initial start time
		Instant start = Instant.now();

		ArgumentParser parse = new ArgumentParser(args);
		try {
			if (parse.hasFlag("-path") && parse.getPath("-path") != null) {
				Path path = parse.getPath("-path");
				if (Files.exists(path)) {
					InvertedIndexBuilder.build(index, path);
				}
			}
		} catch (IOException e) {
			System.out.println("File is a directory");
		}

		if (parse.hasFlag("-index") && parse.getString("-index") != null) {
			// TODO Remove this if block---redundant with block below.
			try {
				index.printIndex(parse.getString("-index"));
			} catch (IOException e) {
				System.out.println("Unable write index.");
			}
		}
		if (parse.hasFlag("-index")) {
			Path path = parse.getPath("-index", Path.of("index.json"));
			try {
				index.printIndex(path.toString());
			} catch (IOException e) {
				System.out.println("Unable to write the index to path: " + path);
			}
		}
		if (parse.hasFlag("-counts")) {
			Path path = parse.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(index.getCount(), path);
			} catch (IOException e) {
				System.out.println("Unable to write the word counts to path: " + path);
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	/*
	 * TODO Fix formatting above. Decide on a consistent approach to using blank
	 * lines and be consistent as to whether you have a try/catch inside an if or
	 * the other way around.
	 */
	
	// TODO Your main() method states it throws an IOException, but it looks like that is no longer true. Update.
}
