import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into sets of
 * stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 *
 * @see TextParser
 */
public class TextFileStemmer {

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		// THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
		return uniqueStems(line, new SnowballStemmer(DEFAULT));
	}
	

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		TreeSet<String> stemmed = new TreeSet<String>(); 
		String[] words = TextParser.parse(line);
		for(String word: words) {
			String data = (String) stemmer.stem(word).toString();
			stemmed.add(data);
		}
		return stemmed;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		TreeSet<String> stemmedWords = new TreeSet<>();
		try (
				BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				
		) {
			String line = reader.readLine();

			// only 1 line needs to be "in memory" at a time
			// (realistically, an entire buffer of text is in memory at a time)
			while (line!= null) {
				String[] parsed = TextParser.parse(line); 
				for(String words: parsed) {
					String data = stemmer.stem(words).toString();
					stemmedWords.add(data);
				}
				line  = reader.readLine();
			}
			
			
		}
		return stemmedWords;
	}
}
