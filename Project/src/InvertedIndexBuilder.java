import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author Jaden
 *
 */
public class InvertedIndexBuilder {
	/** 
	 * Will build index by traversing files
	 * @param index
	 * @param parse
	 */
	public static void build(InvertedIndex index, ArgumentParser parse) {
		if(parse.hasFlag("-path") && parse.getPath("-path") != null) {
			Path path = parse.getPath("-path");
			try(Stream<Path> subPaths=Files.walk(path, FileVisitOption.FOLLOW_LINKS)){
				var iterator = subPaths.iterator();
				while(iterator.hasNext()) {
					var nextPath = iterator.next();
					if(nextPath.toString().toLowerCase().endsWith(".txt") || nextPath.toString().toLowerCase().endsWith(".text")) {
						index.addPath(nextPath);
					}
				}
			} 
			catch (IOException e) {
				System.out.println("Unable to walk Directory");
			}
		}
		if(parse.hasFlag("-index") && parse.getString("-index") != null) {
			try {
				index.printIndex(parse.getString("-index"));
			} 
			catch (IOException e) {
				System.out.println("Unable write index.");
			}
		}
		if (parse.hasFlag("-index")) {
			Path path = parse.getPath("-index", Path.of("index.json"));
			try {
				SimpleJsonWriter.asDoubleNested(index.getIndex(), path);
			}
			catch (IOException e) {
				System.out.println("Unable to write the index to path: " + path);
			}
		}
		if (parse.hasFlag("-counts")) {
			Path path = parse.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(index.getCount(), path);
			}
			catch (IOException e) {
				System.out.println("Unable to write the word counts to path: " + path);
			}
		}
	}
	
}
