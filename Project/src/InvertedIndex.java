import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Jnhamid This is the class for our Custom Data Structure
 */
public class InvertedIndex {

	/**
	 * Private Identifier for our Custom data structure
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	/**
	 * Private Identifier for count data structure
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * Initialize maps
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
		counts = new TreeMap<>();
	}

	/**
	 * An innerclass of results for the queries and words in index
	 *
	 * @author Jaden
	 *
	 */
	public class Result implements Comparable<Result> {

		/**
		 * name of file
		 */
		private final String fileName;

		/**
		 * count
		 */
		private int count;

		/**
		 * score
		 */
		private double score;

		/**
		 * constructor
		 *
		 * @param fileName The location of Result
		 */
		public Result(String fileName) {
			this.score = 0;
			this.count = 0;
			this.fileName = fileName;

		}

		/*
		 * Overridden compareTo
		 *
		 * @param o other result that is comparing
		 */
		@Override
		public int compareTo(Result o) {
			double scoreDifference = this.score - o.score;
			int countDifference = this.count - o.count;
			if (scoreDifference != 0) {
				return scoreDifference > 0 ? -1 : 1;
			} else if (countDifference != 0) {
				return countDifference > 0 ? -1 : 1;

			} else {
				return (this.fileName.toLowerCase().compareTo(o.fileName.toLowerCase()));
			}

		}

		/**
		 * a helper method for creating results
		 *
		 * @param other the other result to check if two files have the same name
		 * @return true if file name is the same
		 */
		public boolean sameFileName(Result other) {
			return this.fileName.compareTo(other.fileName) == 0;
		}

		/**
		 * getting for score
		 *
		 * @return score
		 */
		public double getScore() {
			return score;
		}

		/**
		 * getter for count
		 *
		 * @return count
		 */
		public int getCount() {
			return this.count;
		}

		/**
		 * setter for count
		 *
		 * @param word the word to be updated
		 */
		private void update(String word) {
			this.count += index.get(word).get(fileName).size();
			this.score = (double) this.count / counts.get(this.fileName);
		}

		/**
		 * getter for fileName
		 *
		 * @return fileName
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * pretty string for fileName
		 *
		 * @return String for writing
		 */
		public String getFileNameString() {
			return ("\"where\": " + "\"" + this.fileName + "\",");
		}

		/**
		 * pretty string for count
		 *
		 * @return String for writing
		 */
		public String getCountString() {
			return ("\"count\": " + this.count + ",");
		}

		/**
		 * pretty string for score
		 *
		 * @return String for writing
		 */
		public String getScoreString() {
			return ("\"score\": " + String.format("%.8f", this.score));
		}

		/*
		 * overridden toString
		 */
		@Override
		public String toString() {
			return "[" + getFileName() + ", " + getCountString() + ", " + getScoreString() + "]\n";
		}
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
	public void addElement(String word, String file, Integer pos) throws IOException {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(file, new TreeSet<Integer>());
		index.get(word).get(file).add(pos);
		counts.putIfAbsent(file, pos);
		if (pos > counts.get(file)) {
			counts.put(file, pos);
		}

	}

	/**
	 * will add all elements for other into our index
	 *
	 * @param other the InvertedIndex to be added
	 */
	public void addAll(InvertedIndex other) {
		for (String word : other.index.keySet()) {
			if (this.index.containsKey(word) == false) {
				this.index.put(word, other.index.get(word));
			} else {
				for (String path : other.index.get(word).keySet()) {
					if (!this.index.get(word).containsKey(path)) {
						this.index.get(word).put(path, other.index.get(word).get(path));
					} else {
						this.index.get(word).get(path).addAll(other.index.get(word).get(path));
					}
				}
			}
		}

		for (String word : other.counts.keySet()) {
			if (this.counts.containsKey(word) == false) {
				this.counts.put(word, other.counts.get(word));
			} else {
				this.counts.put(word, Math.max(this.counts.get(word), other.counts.get(word)));
			}
		}

	}

	/**
	 * A method that calls exactSearch or paritalSearch
	 *
	 * @param queries the queries being searched
	 * @param exact   whether exactsearch or not
	 * @return an arraylist of search results
	 */
	public ArrayList<Result> search(Collection<String> queries, boolean exact) {
		return exact ? exactSearch(queries) : partialSearch(queries);
	}

	/**
	 * Does exactSearch of a Collection of quieries
	 *
	 * @param queries the queries being searched
	 * @return an arraylist of results
	 */
	public ArrayList<Result> exactSearch(Collection<String> queries) {
		ArrayList<Result> results = new ArrayList<>();
		HashMap<String, Result> lookup = new HashMap<>();

		for (String query : queries) {
			if (index.containsKey(query)) {
				searchHelper(results, query, lookup);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Does partialSearch of a Collection of quieries
	 *
	 * @param queries the queries being searched
	 * @return an arraylist of results
	 */
	public ArrayList<Result> partialSearch(Collection<String> queries) {
		ArrayList<Result> results = new ArrayList<>();
		HashMap<String, Result> lookup = new HashMap<>();

		for (String query : queries) {
			for (String word : this.index.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					searchHelper(results, word, lookup);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;

	}

	/**
	 * A helper for search
	 *
	 * @param results the Arraylist of results
	 * @param word    the word being searched
	 * @param lookup  lookup checker for duplicates
	 */
	private void searchHelper(ArrayList<Result> results, String word, Map<String, Result> lookup) {
		for (String fileName : this.index.get(word).keySet()) {
			if (!lookup.containsKey(fileName)) {
				Result result = new Result(fileName);
				lookup.put(fileName, result);
				results.add(result);
			}

			lookup.get(fileName).update(word);

		}

	}

	/**
	 * will output to file using method in SimpleJsonWriter
	 *
	 * @param outFile file name of file that is getting written
	 * @throws IOException
	 */
	public void printIndex(Path outFile) throws IOException {
		SimpleJsonWriter.asDoubleNested(index, outFile);
	}

	/**
	 * getter for count
	 *
	 * @return count as a unmodifiableMap
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * checks to see if word is in index
	 *
	 * @param word word that is getting checked
	 * @return if index has word
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}

	/**
	 * checks if the map has the specific word and if word contain path.
	 *
	 * @param word word that is getting checked
	 * @param path path that is getting checked
	 * @return if has word and path
	 */
	public boolean contains(String word, String path) {
		return contains(word) && index.get(word).containsKey(path);
	}

	/**
	 * checks if the map contains the specific word, path and index.
	 *
	 * @param word    word that is getting checked
	 * @param path    path that is getting checked
	 * @param postion postion that is getting checked
	 * @return if has word and path at postion
	 */
	public boolean contains(String word, String path, int postion) {
		return contains(word, path) && index.get(word).get(path).contains(postion);
	}

	/**
	 * getter for set of words
	 *
	 * @return an unmodifiable set of words
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());

	}

	/**
	 * getter for set of locations
	 *
	 * @param word key value for locations
	 * @return an unmodifiable set of Locations
	 */
	public Set<String> getLocations(String word) {
		if (contains(word)) {
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		return Collections.emptySet();

	}

	/**
	 * getter for set of postions
	 *
	 * @param word
	 * @param location
	 * @return an unmodifiable set of Positions
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (contains(word, location)) {
			return Collections.unmodifiableSet(index.get(word).get(location));
		}
		return Collections.emptySet();

	}

	/**
	 * returns how many words is in inverted index.
	 *
	 * @return size of inverted index.
	 */
	public int size() {
		return index.size();
	}

	/**
	 * returns how many paths are found in word.
	 *
	 * @param word to check
	 * @return amount of paths, return 0 if not found
	 */
	public int size(String word) {
		return contains(word) ? index.get(word).size() : 0;
	}

}
