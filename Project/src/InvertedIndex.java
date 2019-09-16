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
 *
 */
public class InvertedIndex {
	
	/**
	 * 	Snowball Stemmer 
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	/**
	 * Private Identifier for our Custom data structure
	 */
	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> index;
	
	/**
	 * Map for count
	 */
	public TreeMap<String, Integer> count;
	
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
		if(index.containsKey(word)) {
			 if(index.get(word).containsKey(file)) {
				 if(!index.get(word).get(file).contains(pos)) {
					 index.get(word).get(file).add(pos);
					 this.getCount().put(file, getCount().get(file) + 1);
				 }
			 }
			 else{
			 ArrayList<Integer> postions = new ArrayList<>();
			 postions.add(pos);
			 index.get(word).put(file, postions);
			 if (getCount().get(file) == null) {
					this.getCount().put(file, 1);
				} else {
					this.getCount().put(file, getCount().get(file) + 1);
				}
			 }
			 
		}
		else {
			TreeMap<String, ArrayList<Integer>> nestedIndex = new TreeMap<>();
			ArrayList<Integer> postions = new ArrayList<>();
			postions.add(pos);
			nestedIndex.put(file, postions);
			index.put(word, nestedIndex);
			if (getCount().get(file) == null) {
				this.getCount().put(file, 1);
			} else {
				this.getCount().put(file, getCount().get(file) + 1);
			}
		}
	}
	
	/**
	 * will output to file, using a modified function from hw 
	 * @param outFile
	 * @throws IOException 
	 */
	public void printIndex(String outFile) throws IOException {
		SimpleJsonWriter.asDoubleNested(index, Path.of(outFile));

	/**
	 * @return
	 */
	}
	/**
	 * Adds Path 
	 * @param file
	 * @throws IOException 
	 */
	public void addPath(Path file) throws IOException {
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
		catch (Exception e) {
			System.out.println("Couldn't Read File");
		}
	}
	/**
	 * @return index
	 */
	public TreeMap<String,TreeMap<String, ArrayList<Integer>>> getIndex(){
		return index;
	}
	/**
	 * @return count TreeMap
	 */
	public TreeMap<String, Integer> getCount(){
		return count;
	}
	
		
	}

