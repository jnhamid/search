import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.List;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author Jaden This is a Builder class for our InvertedIndex class.
 */
public class InvertedIndexBuilder {

	/**
	 * Snowball Stemmer
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Will build index by traversing files
	 * 
	 * @param index The InvertedIndex that is getting built
	 * @param path  The Path that is getting checked
	 * @throws IOException
	 */
	public static void build(InvertedIndex index, Path path) throws IOException {
		if (Files.isRegularFile(path)) {
			addPath(index, path);
		} else {
			for (Path newPath : getTextFiles(path)) {
				if (isTextFile(newPath)) { // TODO Remove this check, getTextFiles should only return text files!
					addPath(index, newPath);
				}
			}
		}
	}

	/**
	 * Checks to see if path is a text file and not a directory or some trash file
	 * 
	 * @param path file that you are checking
	 * @return if its a text file
	 */
	public static boolean isTextFile(Path path) {
		String lowerCase = path.toString().toLowerCase();
		return ((lowerCase.endsWith(".txt") || lowerCase.endsWith(".text")) && Files.isRegularFile(path));
	}

	/**
	 * Gets a list of files by walking the directory or file
	 * 
	 * @param path path or directory that needs to be traversed
	 * @return a list of text files
	 * @throws IOException
	 */
	public static List<Path> getTextFiles(Path path) throws IOException {
		// TODO Filter this by text files before returning the list.
		return Files.walk(path, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList());
	}

	/**
	 * Adds Path to index
	 * 
	 * @param index the InvertedIndex that the file is getting added to
	 * @param file  the Path that is getting added to index.
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
