import java.util.*;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * @author Jnhamid
 *
 */
public class InvertedIndex {
	/**
	 * Private Identifier for our Custom data structure
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> index;
	
	/**
	 * Initial map
	 */
	public InvertedIndex(){
		index = new TreeMap<>();
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
				 }
			 }
			 else{
			 ArrayList<Integer> postions = new ArrayList<>();
			 postions.add(pos);
			 index.get(word).put(file, postions);
			 }
			 
		}
		else {
			TreeMap<String, ArrayList<Integer>> nestedIndex = new TreeMap<>();
			ArrayList<Integer> postions = new ArrayList<>();
			postions.add(pos);
			nestedIndex.put(file, postions);
			index.put(word, nestedIndex);
		}
	}
	
	/**
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
	}
	
		
	}

