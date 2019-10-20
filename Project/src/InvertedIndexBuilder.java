import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


// TODO I'm going to stop marking everywhere you left off a description for your Javadoc. Go through your code and fix it everywhere!

/**
 * @author Jaden
 * TODO DESCRIPTION
 */
public class InvertedIndexBuilder {

	/**
	 * Snowball Stemmer
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Will build index by traversing files
	 * 
	 * @param index TODO DESCRIPTION
	 * @param path TODO DESCRIPTION
	 * @throws IOException
	 */
	public static void build(InvertedIndex index, Path path) throws IOException {
		if (Files.isRegularFile(path)) {
			addPath(index, path);
		} else {
			// TODO Might as well make a getTextFiles(Path) method that returns this list... more functionality!
			for (Path newPath : Files.walk(path, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList())) {
				if (isTextFile(newPath)) {
					addPath(index, newPath);
				}
			}
		}
	}

	/**
	 *  TODO DESCRIPTION
	 * @param path  TODO DESCRIPTION
	 * @return if its a text file
	 */
	public static boolean isTextFile(Path path) {
		String lowerCase = path.toString().toLowerCase();
		return ((lowerCase.endsWith(".txt") || lowerCase.endsWith(".text")) && Files.isRegularFile(path));
	}

	/**
	 * Adds Path
	 * 
	 * @param index TODO DESCRIPTION
	 * @param file TODO DESCRIPTION
	 * @throws IOException
	 */
	public static void addPath(InvertedIndex index, Path file) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			String line = reader.readLine();
			int i = 0;
			String fileName = file.toString();
			while (line != null) {
				String[] parsed = TextParser.parse(line);
				for (String word : parsed) {
					String data = stemmer.stem(word).toString();
					index.addElement(data, fileName, ++i);
				}
				line = reader.readLine();
			}
		}
	}

}
