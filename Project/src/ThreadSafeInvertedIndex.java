import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A threadSafe version of InvertedIndex
 *
 * @author Jaden
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	/** The lock used to protect concurrent access to the underlying set. */
	private final SimpleReadWriteLock lock;

	/**
	 * Constructor
	 *
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new SimpleReadWriteLock();

	}

	/**
	 * Will add element to our Inverted Index.
	 *
	 * @param word to be added
	 * @param file to be added
	 * @param pos  to be added
	 * @throws IOException
	 *
	 */
	@Override
	public void addElement(String word, String file, Integer pos) throws IOException {
		lock.writeLock().lock();
		try {
			super.addElement(word, file, pos);
		} finally {
			lock.writeLock().unlock();
		}

	}

	/**
	 * Does exactSearch of a Collection of quieries
	 *
	 * @param queries the queries being searched
	 * @return an arraylist of results
	 */
	@Override
	public ArrayList<Result> exactSearch(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Does partialSearch of a Collection of quieries
	 *
	 * @param queries the queries being searched
	 * @return an arraylist of results
	 */
	@Override
	public ArrayList<Result> partialSearch(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * will output to file using method in SimpleJsonWriter
	 *
	 * @param outFile file name of file that is getting written
	 * @throws IOException
	 */
	@Override
	public void printIndex(Path outFile) throws IOException {
		lock.readLock().lock();
		try {
			super.printIndex(outFile);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * getter for count
	 *
	 * @return count as a unmodifiableMap
	 */
	@Override
	public Map<String, Integer> getCount() {
		lock.readLock().lock();
		try {
			return super.getCount();
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * checks to see if word is in index
	 *
	 * @param word word that is getting checked
	 * @return if index has word
	 */
	@Override
	public boolean contains(String word) {
		lock.readLock().lock();
		try {
			return super.contains(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * checks if the map has the specific word and if word contain path.
	 *
	 * @param word word that is getting checked
	 * @param path path that is getting checked
	 * @return if has word and path
	 */
	@Override
	public boolean contains(String word, String path) {
		lock.readLock().lock();
		try {
			return super.contains(word, path);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * checks if the map contains the specific word, path and index.
	 *
	 * @param word    word that is getting checked
	 * @param path    path that is getting checked
	 * @param postion postion that is getting checked
	 * @return if has word and path at postion
	 */
	@Override
	public boolean contains(String word, String path, int postion) {
		lock.readLock().lock();
		try {
			return super.contains(word, path, postion);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * getter for set of words
	 *
	 * @return an unmodifiable set of words
	 */
	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}

	}

	// TODO Missing some methods at the end (size and the other get methods)

}
