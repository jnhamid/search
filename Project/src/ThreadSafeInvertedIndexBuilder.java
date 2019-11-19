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

	private final WorkQueue queue;

	/**
	 * Constructor
	 *
	 * @param index the thread safe index
	 * @param queue the word queue
	 */
	public ThreadSafeInvertedIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		super(index);
		this.index = index;
		this.queue = queue;
	}

	/**
	 * Will build index by traversing files
	 *
	 * @param path The Path that is getting checked
	 * @throws IOException
	 */
//	public void build(Path path, int numThreads) throws IOException {
//		WorkQueue queue = new WorkQueue(numThreads);
//		for (Path currentPath : getTextFiles(path)) {
//			if (isTextFile(currentPath)) {
//				queue.execute(new Task(currentPath, this.index));
//			}
//		}
//		try {
//			queue.finish();
//		} catch (Exception e) {
//			System.out.println("The work queue encountered an error.");
//		}
//		queue.shutdown();
//	}

	@Override
	public void build(Path path) throws IOException {
		super.build(path);
		try {
			queue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
	}

	@Override
	public void addPath(Path path) throws IOException {
		queue.execute(new Task(path));
	}

	/**
	 * The Task class for multithreading
	 *
	 * @author Jaden
	 *
	 */
	private class Task implements Runnable {
		/** The prime number to add or list. */
		private final Path path;

		/**
		 * @param path the path to be added
		 */
		public Task(Path path) {
			this.path = path;
		}

		@Override
		public void run() {
			try {

				InvertedIndex local = new InvertedIndex();
				addPath(local, path);
				index.addAll(local); // TODO make addAll

			} catch (IOException e) {
				System.out.println("There is an error adding a path: " + path);
			}
		}
	}

}
