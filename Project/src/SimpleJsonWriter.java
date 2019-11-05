import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SimpleJsonWriter {
	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			String line = iterator.next();
			asObjectHelper(elements, line, writer, level);
		}

		while (iterator.hasNext()) {
			String line = iterator.next();
			writer.write(",");
			asObjectHelper(elements, line, writer, level);
		}

		writer.write("\n");
		indent("}", writer, level);
	}

	/**
	 * Helper method for asObject();
	 * 
	 * @param elements the elements to write
	 * @param line     the line from element that is getting written
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObjectHelper(Map<String, Integer> elements, String line, Writer writer, int level)
			throws IOException {
		writer.write("\n");
		quote(line.toString(), writer, level + 1);
		writer.write(":");
		writer.write(" " + elements.get(line));
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{");
		if (iterator.hasNext()) {
			String line = iterator.next();
			asNestedHelper(elements, line, writer, level);
			writer.write("\n\t]");
		}

		while (iterator.hasNext()) {
			String line = iterator.next();
			writer.write(",");
			asNestedHelper(elements, line, writer, level);
			writer.write("\n\t]");
		}

		writer.write("\n");
		indent("}", writer, level);
	}

	/**
	 * Helper for asNestedObject();
	 * 
	 * @param elements the element to write
	 * @param line     the line of element to write
	 * @param writer   the writer to use
	 * @param level    the inital indent level
	 * @throws IOException
	 */
	public static void asNestedHelper(Map<String, ? extends Collection<Integer>> elements, String line, Writer writer,
			int level) throws IOException {
		writer.write("\n");
		quote(line.toString(), writer, level + 1);
		writer.write(": [\n");
		Collection<Integer> nestedList = elements.get(line);
		Iterator<Integer> iterator = nestedList.iterator();

		if (iterator.hasNext()) {
			String postion = iterator.next().toString();
			indent(postion, writer, level + 2);
		}

		while (iterator.hasNext()) {
			String postion = iterator.next().toString();
			writer.write(",\n");
			indent(postion, writer, level + 2);
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param index  the elements to write
	 * @param writer the writer to use
	 * @param level  the inital indent level
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 * 
	 */
	private static void asDoubleNested(Map<String, TreeMap<String, TreeSet<Integer>>> index, Writer writer,
			Integer level) throws IOException {
		var iterator = index.keySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			String key = iterator.next();
			writer.write("\n\t");
			writer.write('"' + key + '"' + ": ");
			asNestedObject(index.get(key), writer, level + 1);
		}

		while (iterator.hasNext()) {
			String key = iterator.next();
			writer.write(",\n\t");
			writer.write('"' + key + '"' + ": ");
			asNestedObject(index.get(key), writer, level + 1);
		}

		writer.write("\n");
		indent("}", writer, level - 1);
	}

	/**
	 * Writer for Query Object
	 * 
	 * @param querySet Map of Query and results
	 * @param path     the Path to be written to
	 * @param writer   the writer
	 * @param level    the intial indent level
	 * @throws IOException
	 */
	public static void asQuery(Map<String, ArrayList<InvertedIndex.Result>> querySet, Path path, Writer writer,
			int level) throws IOException {
		writer.write("{\n");
		var iterator = querySet.keySet().iterator();

		if (iterator.hasNext()) {
			String query = iterator.next();
			indent(writer, level + 1);
			writer.write("\"" + query.toString() + "\": [");
			indent(writer, level + 1);
			asQueryHelper(querySet, query, path, writer, level + 1);
			writer.write("\n\t]");

		}

		while (iterator.hasNext()) {
			String query = iterator.next();
			writer.write(",\n");
			indent(writer, level + 1);
			writer.write("\"" + query.toString() + "\": [");
			indent(writer, level + 1);
			asQueryHelper(querySet, query, path, writer, level + 1);
			writer.write("\n\t]");
		}
		writer.write("\n}");

	}

	/**
	 * a helper method for asQuery
	 * 
	 * @param querySet  Map of Query and results
	 * @param nextQuery the next query entry
	 * @param path      the Path to be written to
	 * @param writer    the writer
	 * @param level     the intial indent level
	 * @throws IOException
	 */
	public static void asQueryHelper(Map<String, ArrayList<InvertedIndex.Result>> querySet, String nextQuery, Path path,
			Writer writer, int level) throws IOException {
		var innerIterator = querySet.get(nextQuery).iterator();

		if (innerIterator.hasNext()) {
			writer.write("\n");
			indent(writer, level + 1);
			writer.write("\t{\n");
			indent(writer, level + 3);
			var nexto = innerIterator.next();
			writer.write(nexto.getFileNameString() + "\n");
			indent(writer, level + 3);
			writer.write(nexto.getCountString() + "\n");
			indent(writer, level + 3);
			writer.write(nexto.getScoreString() + "\n");
			indent(writer, level + 2);
			writer.write("}");
			indent(writer, level - 1);
		}
		while (innerIterator.hasNext()) {
			writer.write(",\n");
			indent(writer, level + 2);
			writer.write("{\n");
			indent(writer, level + 3);
			var nexto = innerIterator.next();
			writer.write(nexto.getFileNameString() + "\n");
			indent(writer, level + 3);
			writer.write(nexto.getCountString() + "\n");
			indent(writer, level + 3);
			writer.write(nexto.getScoreString() + "\n");
			indent(writer, level + 2);
			writer.write("}");
			indent(writer, level - 1);
		}
	}

	/**
	 * Overloads asQuery
	 *
	 * @param map  map of query
	 * @param path path being written to
	 * @throws IOException
	 */
	public static void asQuery(Map<String, ArrayList<InvertedIndex.Result>> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asQuery(map, path, writer, 0);
		}
	}

	/**
	 * Calls the overloaded asDoubleNested() just taking in the element and the path
	 * 
	 * @param elements the Map that is being written to
	 * @param path     the path that is getting written
	 * @throws IOException
	 */
	public static void asDoubleNested(Map<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNested(elements, writer, 0);
		}
	}

	/**
	 * Calls the overloaded asNestedObject() just taking the element and the path
	 * 
	 * @param elements the Map that is being written to
	 * @param path     the path that is getting written
	 * @throws IOException
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static String asNestedObject(Map<String, ? extends Collection<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		quote(element, writer);
	}
}
