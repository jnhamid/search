import java.io.IOException;
import java.nio.file.Path;

/**
 * ThreadSafe version of InvertedIndexBuilder
 *
 * @author Jaden
 *
 */
public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {
	/**
	 * The {@link ThreadSafeInvertedIndex} being built
	 */
	private final ThreadSafeInvertedIndex index;

	// TODO private final WorkQueue queue
	
	/**
	 * TODO Javadoc
	 * @param index
	 */
	public ThreadSafeInvertedIndexBuilder(ThreadSafeInvertedIndex index) { // TODO Pass in a work queue as a parameter
		super(index);
		this.index = index;
	}

	/**
	 * Will build index by traversing files
	 *
	 * @param path The Path that is getting checked
	 * @throws IOException
	 */
	@Override
	public void build(Path path, int numThreads) throws IOException {
		WorkQueue queue = new WorkQueue(numThreads);
		for (Path currentPath : getTextFiles(path)) {
			if (isTextFile(currentPath)) {
				queue.execute(new Task(currentPath, this.index));
			}
		}
		try {
			queue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
		queue.shutdown();
	}

	/* TODO Try this
	@Override
	public void build(Path path, int numThreads) throws IOException {
		super.build(path);
		try {
			queue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
	}	
	
	@Override
	public void addPath(Path path) throws IOException {
		queue.execute(new Task(currentPath));
	}
	*/

	/**
	 * The Task class for multithreading
	 *
	 * @author Jaden
	 *
	 */
	private static class Task implements Runnable { // TODO private class (non-static)
		/** The prime number to add or list. */
		private final Path path;

		/**
		 * The Thread Safe Inverted index
		 */
		private final ThreadSafeInvertedIndex index; // TODO Remove

		/**
		 * @param path  the path to be added
		 * @param index the index being added to
		 */
		public Task(Path path, ThreadSafeInvertedIndex index) {
			this.path = path;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				addPath(this.index, path);
				
				/* TODO
				InvertedIndex local = new InvertedIndex();
				addPath(local, path);
				index.addAll(local);
				*/
				
			} catch (IOException e) {
				System.out.println("There is an error adding a path: " + path);
			}
		}
	}

}
