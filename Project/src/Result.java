
/**
 * @author Jaden
 *
 */
public class Result implements Comparable<Result> {

	/**
	 * name of file
	 */
	private String fileName;

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
	 */
	public Result() {
		this.score = 0;
		this.count = 0;
		this.fileName = "";

	}

	/**
	 * Better Constructor
	 * 
	 * @param fileName
	 * @param count
	 * @param score
	 */
	public Result(String fileName, int count, double score) {
		this.score = score;
		this.count = count;
		this.fileName = fileName;
	}

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
	 * @param other
	 * @return if
	 */
	public boolean sameFileName(Result other) {
		return this.fileName.compareTo(other.fileName) == 0;
	}

	/**
	 * @return score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return String for writing
	 */
	public String getFileNameString() {
		return ("\"where\": " + "\"" + this.fileName + "\",");
	}

	/**
	 * @return String for writing
	 */
	public String getCountString() {
		return ("\"count\": " + this.count + ",");
	}

	/**
	 * @return String for writing
	 */
	public String getScoreString() {
		return ("\"score\": " +  String.format("%.8f", this.score));
	}

	public String toString() {
		return "[" + getFileName() + ", " + getCountString() + ", " + getScoreString() + "]\n";

	}
}
