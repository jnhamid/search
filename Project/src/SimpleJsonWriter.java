import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
		Iterator<String> eterator = elements.keySet().iterator();
		writer.write("{");

		if (eterator.hasNext()) {
			String line = eterator.next();

			writer.write("\n");
			quote(line.toString(), writer, level + 1);
			writer.write(":");
			writer.write(" " + elements.get(line));
		}

		while (eterator.hasNext()) {
			String line = eterator.next();
			writer.write(",\n");
			quote(line.toString(), writer, level + 1);
			writer.write(":");
			writer.write(" " + elements.get(line));

		}

		writer.write("\n");
		indent("}", writer, level);
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

	// TODO Fix this up and call your other methods

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
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {
		Iterator<String> eterator = elements.keySet().iterator();
		writer.write("{");
		if (eterator.hasNext()) {
			String line = eterator.next();
			writer.write("\n");
			quote(line.toString(), writer, level + 1);
			writer.write(": [\n");
			Collection<Integer> nestedList = elements.get(line);
			Iterator<Integer> Niterator = nestedList.iterator();
			if (Niterator.hasNext()) {
				Integer num = Niterator.next();
				indent(num.toString(), writer, level + 2);
			}
			while (Niterator.hasNext()) {
				Integer num = Niterator.next();
				writer.write(",\n");
				indent(num.toString(), writer, level + 2);
			}
			writer.write("\n\t]");
		}
		while (eterator.hasNext()) {
			String line = eterator.next();
			writer.write(",\n");
			quote(line.toString(), writer, level + 1);
			writer.write(": [");
			Collection<Integer> nestedList = elements.get(line);
			Iterator<Integer> Niterator = nestedList.iterator();
			if (Niterator.hasNext()) {
				Integer num = Niterator.next();
				writer.write("\n");
				indent(num.toString(), writer, level + 2);
			}
			while (Niterator.hasNext()) {
				Integer num = Niterator.next();
				writer.write(",\n");
				indent(num.toString(), writer, level + 2);
			}
			writer.write("\n\t]");
		}
		writer.write("\n");
		indent("}", writer, level);

		/*
		 * The generic notation:
		 *
		 * Map<String, ? extends Collection<Integer>> elements
		 *
		 * May be confusing. You can mentally replace it with:
		 *
		 * HashMap<String, HashSet<Integer>> elements
		 */
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param index the elements to write
	 * @param writer
	 * @param level
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 * 
	 */
	private static void asDoubleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Writer writer,
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
	 * @param index
	 * @param path
	 * @throws IOException
	 */
	public static void asDoubleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNested(index, writer, 0);
		}
	}

	/**
	 * @param elements
	 * @param path
	 * @throws IOException
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
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
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
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
