import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author jnhamid
 *
 */
public class WebCrawler {

	/**
	 * Default stemmer.
	 */
	private static SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * The Inverted Index to populate
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * The workQueue that will be used for multithreading.
	 */
	private WorkQueue queue;

	/**
	 * The number of maximum follows.
	 */
	private int limit;

	/**
	 * The set of links.
	 */
	private Set<URL> links;

	/**
	 * Constructor for the crawler class
	 *
	 * @param index the index to use
	 * @param queue
	 * @param limit maximum depth
	 */
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue queue, int limit) {
		this.index = index;
		this.queue = queue;
		this.limit = limit;
		this.links = new HashSet<URL>();
	}

	/**
	 * Stems the content of the cleaned html and adds it to the index.
	 *
	 * @param cleaned  the cleaned HTML
	 * @param fileName the location string
	 * @param index    the index to add to
	 * @throws IOException could happen
	 */
	public static void addStemmed(String cleaned, String fileName, InvertedIndex index) throws IOException {
		int position = 0;
		try (BufferedReader reader = new BufferedReader(new StringReader(cleaned));) {
			String line = null;
			SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					position++;
					index.addElement(stemmer.stem(word).toString(), fileName, position);
				}
			}
		}
	}

	/**
	 * This traverses the URLs and does all the hard work
	 *
	 * @param seed the seed url
	 * @throws IOException could happen
	 */
	public void traverse(URL seed) throws IOException {
		links.add(seed);
		queue.execute(new Task(seed));
		try {
			queue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
	}

	/**
	 * A task class for multithreadig.
	 *
	 * @author jnhamid
	 *
	 */
	private class Task implements Runnable {

		/**
		 * Given URL.
		 */
		private final URL url;

		/**
		 * Constructor for the Task.
		 *
		 * @param url the url to work on.
		 */
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			HtmlCleaner htmlCleaner = new HtmlCleaner(this.url, HtmlFetcher.fetch(url, 3));
			try {
				if (HtmlFetcher.fetch(url, 3) == null) {
					return;
				}

				InvertedIndex local = new InvertedIndex();
				addStemmed(htmlCleaner.getHtml(), url.toString(), local);
				index.addAll(local);

				synchronized (links) {
					for (URL url : htmlCleaner.getUrls()) {
						if (links.size() < limit && links.add(url)) {
							queue.execute(new Task(url));
						} else if (links.size() == limit) {
							break;
						}
					}
				}

			} catch (Exception e) {
				System.out.println("Something went wrong while adding the cleaned HTML to the index.");
			}
		}
	}

}
