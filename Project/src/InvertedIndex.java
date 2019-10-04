import java.util.*;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * @author Jnhamid
 *push1
 */
public class InvertedIndex {
	
	/**
	 * 	Snowball Stemmer 
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH; // TODO Move to the builder class
	/**
	 * Private Identifier for our Custom data structure
	 */
	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> index; // TODO Switch ArrayList<Integer> to TreeSet<Integer>, make this final
	
	/**
	 * Map for count
	 */
	public TreeMap<String, Integer> count; // TODO private final
	
	/**
	 * Initial map
	 */
	public InvertedIndex(){
		index = new TreeMap<>();
		count = new TreeMap<>();
	}
	
	/**
	 * Will add element to our Inverted Index.
	 * @param word 
	 * @param file 
	 * @param pos 
	 * 
	 */
	public void addElement(String word, String file, Integer pos) {
		index.putIfAbsent(word, new TreeMap<String, ArrayList<Integer>>());
		index.get(word).putIfAbsent(file, new ArrayList<Integer>());
		index.get(word).get(file).add(pos);
		
		/*
		 * TODO This assumes there are no duplicate add calls
		 * 
		 * Use the position as a proxy for word count
		 * Always use the max position of a location for its word count
		 */
		count.putIfAbsent(file, 0);
		count.put(file, count.get(file) +1);
		
	}
	/**
	 * will output to file, using a modified function from hw 
	 * @param outFile
	 * @throws IOException 
	 */
	public void printIndex(String outFile) throws IOException {
		SimpleJsonWriter.asDoubleNested(index, Path.of(outFile));
	}
	/**
	 * Adds Path 
	 * @param file
	 * @throws IOException 
	 */
	public void addPath(Path file) throws IOException { // TODO Move to the builder
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (
				BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
				
		) {
			String line = reader.readLine();
			// only 1 line needs to be "in memory" at a time
			// (realistically, an entire buffer of text is in memory at a time)
			int i =0;
			while (line!= null) {
				String[] PLine = TextParser.parse(line); 
				for(String words: PLine) {	
					String data = (String) stemmer.stem(words.toString());
					addElement(data,file.toString(),++i);
				}
				line  = reader.readLine();
			}
		}
		catch (Exception e) { // TODO Remove the catch block
			System.out.println("Couldn't Read File: " + file);
		}
	}
	
	/*
	 * TODO Both of these below are breaking encapsulation
	 */
	
	/**
	 * @return index
	 */
	public TreeMap<String,TreeMap<String, ArrayList<Integer>>> getIndex(){ // TODO Remove
		return index;
	}
	/**
	 * @return count TreeMap
	 */
	public TreeMap<String, Integer> getCount(){
		return count; // TODO Wrap in an unmodifiable map
	}
	
	/*
	 * TODO Consdier adding more functionality (has and get methods similar to WordIndex)
	 */
}

