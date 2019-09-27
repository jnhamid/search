import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;

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
			SimpleJsonWriter.asObject(index.getCount(), Path.of("actual/counts.json"));
		}
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
