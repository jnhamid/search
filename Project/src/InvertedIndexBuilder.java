import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author Jaden
 *
 */
public class InvertedIndexBuilder {
	
	/**
	 * 	Snowball Stemmer 
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH; 
	/** 
	 * Will build index by traversing files
	 * @param index
	 * @param path
	 * @throws IOException 
	 */
	public static void build(InvertedIndex index, Path path) throws IOException {
		if(Files.isRegularFile(path)){
			addPath(index, path);
		}
		else {
			
		
			try(Stream<Path> subPaths=Files.walk(path, FileVisitOption.FOLLOW_LINKS)){
				var iterator = subPaths.iterator();
				while(iterator.hasNext()) {
					var nextPath = iterator.next();
					if((nextPath.toString().toLowerCase().endsWith(".txt") || nextPath.toString().toLowerCase().endsWith(".text"))) {
						addPath(index, nextPath);
					}
				}
			} 
		}
	}
	/**
	 * Adds Path 
	 * @param index 
	 * @param file
	 * @throws IOException 
	 */
	public static void addPath(InvertedIndex index, Path file) throws IOException { 
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			String line = reader.readLine();
			// only 1 line needs to be "in memory" at a time
			// (realistically, an entire buffer of text is in memory at a time)
			int i =0;
			while (line!= null) {
				String[] PLine = TextParser.parse(line); 
				for(String words: PLine) {	
					String data = (String) stemmer.stem(words.toString());
					index.addElement(data,file.toString(),++i);
				}
				line  = reader.readLine();
			}
		}catch(IOException e) {//TODO remove catch
			System.out.println("Is a directory");
		}
	}
	
	
}
