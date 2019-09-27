import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;

/*
 * TODO Clean up any unnecessary System.out.println(...)
 */

// TODO Delete old TODO comments

/*
 * TODO Fix the Javadoc warnings...
Javadoc: The method asNestedObject(TreeMap<String,ArrayList<Integer>>, Writer, int) in the type SimpleJsonWriter is not applicable for the arguments (Map, Writer, int)	SimpleJsonWriter.java	/Project/src	line 266	Java Problem
Javadoc: The method asNestedObject(TreeMap<String,ArrayList<Integer>>, Writer, int) in the type SimpleJsonWriter is not applicable for the arguments (Map, Writer, int)	SimpleJsonWriter.java	/Project/src	line 315	Java Problem
 */

/*
 * TODO Fix the formatting (and use of blanks lines) and remove commented out code.
 */

/*
 * TODO Exception handling
 * Handle exceptions where you interact with the user. In this project, that means
 * throw all your exceptions to Driver.main, but Driver.main must catch them.
 * 
 * All output should be user-friendly and informative.
 * 
 * 1) Stack traces are not user-friendly.
 * 2) Messages like "Error happened." not informative.
 */

/*
 * TODO Class design... Driver is the only programmer-specific class that does not
 * get shared with other developers. So any useful code should in another class.
 * (Like... traversing the directory.)
 * 
 * InvertedIndexBuilder class that has all of the directory traversing and file parsing
 * code related to building an index.
 */

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 *push
 */
public class Driver {
	//Test commit

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
		if(parse.hasFlag("-path") && parse.getPath("-path") != null) {
			
//			index.addPath(parse.getPath("-path"));
			Path path = parse.getPath("-path");
			
			try(Stream<Path> subPaths=Files.walk(path, FileVisitOption.FOLLOW_LINKS)){
				var iterator = subPaths.iterator();
				while(iterator.hasNext()) {
					var nextPath = iterator.next();
					if(nextPath.toString().toLowerCase().endsWith(".txt") || nextPath.toString().toLowerCase().endsWith(".text")) {
						index.addPath(nextPath);
					}
					
						
					
					
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		if(parse.hasFlag("-index") && parse.getString("-index") != null) {
			index.printIndex(parse.getString("-index"));
			
		}
		if(parse.hasFlag("-index")) {
			SimpleJsonWriter.asDoubleNested((index.getIndex()), Path.of("index.json"));
		}
		if(parse.hasFlag("-counts")) {
			SimpleJsonWriter.asObject(index.getCount(), Path.of("actual/counts.json")); // TODO Must be a bug if you need actual/counts.json
		}
		
		/* TODO Try this for exception handling...
		if (parse.hasFlag("-counts")) {
			Path path = parse.getPath("-counts", Path.of("counts.json"));
			
			try {
				SimpleJsonWriter.asObject(index.getCount(), path);
			}
			catch (IOException e) {
				System.out.println("Unable to write the word counts to path: " + path);
			}
		}
		*/
		
		
		
//		if(parse.hasFlag("-counts") && parse.getString("-counts") != null) {
//			SimpleJsonWriter.asObject(parse.getString("counts"));
//			
//		}
//		
		System.out.println(Arrays.toString(args));

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
