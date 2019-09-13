import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where
 * newlines are used to separate elements and nested elements are indented.
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
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		// TODO MODIFY AND FILL IN AS NECESSARY TO PASS TESTS
		// TODO USE ITERATION NOT STRING REPLACEMENT
		Iterator<Integer> eterator= elements.iterator();
		writer.write("[");
		if(eterator.hasNext()) {
			writer.write("\n");
			indent(eterator.next().toString(),writer,level+1);
		}
		while(eterator.hasNext()) {
			
			writer.write(",\n");
			indent(eterator.next().toString(),writer,level+1);
		}
		writer.write("\n");
		indent("]",writer,level);
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		// TODO MODIFY AND FILL IN AS NECESSARY TO PASS TESTS
		// TODO USE ITERATION NOT STRING REPLACEMENT
		Iterator<String> eterator=elements.keySet().iterator();
		
		System.out.println(elements.toString());
		writer.write("{");
		if(eterator.hasNext()) {
			String line = eterator.next();
			
			writer.write("\n");
			quote(line.toString(), writer, level+1);
			writer.write(":");
			writer.write(" " + elements.get(line));
			
		}
		while(eterator.hasNext()) {
			String line = eterator.next();
			writer.write(",\n");
			quote(line.toString() , writer, level+1);
			writer.write(":");
			writer.write(" " +elements.get(line));
			
		}
		writer.write("\n");
		indent("}",writer,level);
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
		}
		catch (IOException e) {
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
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level) throws IOException {
		// TODO MODIFY AND FILL IN AS NECESSARY TO PASS TESTS
		// TODO USE ITERATION NOT STRING REPLACEMENT
		Iterator<String> eterator=elements.keySet().iterator();
		
//		System.out.println(line);
//		System.out.println(elements.toString());
		writer.write("{");
		if(eterator.hasNext()) {
			String line = eterator.next();
			
			writer.write("\n");
			quote(line.toString(), writer, level+1);
			writer.write(": [\n");
			Collection<Integer> nestedList =elements.get(line);
			
			Iterator<Integer> Niterator = nestedList.iterator();
			if(Niterator.hasNext()) {
				Integer num = Niterator.next();
				indent(num.toString(), writer, level+2);
				writer.write("\n\t]");
				
			}
			
			
			while(Niterator.hasNext()) {
				Integer num = Niterator.next();
				indent(num.toString(), writer, level+2);
				
				writer.write("\t]");
				
				
			}
			
			//writer.write(" " + elements.get(line));//list iterator 
			
		}
		while(eterator.hasNext()) {
			String line = eterator.next();
			writer.write(",\n");
			quote(line.toString() , writer, level+1);
			writer.write(": [");
			
			Collection<Integer> nestedList = elements.get(line);
			
			Iterator<Integer> Niterator = nestedList.iterator();
			
			if(Niterator.hasNext()) {
				Integer num = Niterator.next();
				writer.write("\n");
				indent(num.toString(), writer, level+2);
				
				
			}
			
			while(Niterator.hasNext()) {
				Integer num = Niterator.next();
				writer.write(",\n");
				indent(num.toString(), writer, level+2);
				
			
				
			}
			
			
			writer.write("\n\t]");
			
		}
		writer.write("\n");
		indent("}",writer,level);
		

		/*
		 * The generic notation:
		 *
		 *    Map<String, ? extends Collection<Integer>> elements
		 *
		 * May be confusing. You can mentally replace it with:
		 *
		 *    HashMap<String, HashSet<Integer>> elements
		 */
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 * 
	 */
	private static void asDoubleNested(TreeMap<String, TreeMap<String, ArrayList<Integer>>> elements, Writer writer, Integer level) throws IOException {
		var iterator = elements.keySet().iterator();
		writer.write("{");
		if(iterator.hasNext()) {
			String key = iterator.next();
			writer.write("\n");
			writer.write(key +":");
			asNestedObject(elements.get(key), writer, level);
		}
		while(iterator.hasNext()) {
			String key = iterator.next();
			writer.write(",\n");
			writer.write(key +":");
			asNestedObject(elements.get(key), writer, level);
		}
	}
	/**
	 * @param elements
	 * @param path
	 * @throws IOException
	 */
	public static void asDoubleNested(TreeMap<String, TreeMap<String, ArrayList<Integer>>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNested(elements, writer, 0);
		}
	}
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Path path) throws IOException {
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
		}
		catch (IOException e) {
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

	/**
	 * A simple main method that demonstrates this class.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		// MODIFY AS NECESSARY TO DEBUG YOUR CODE

		//TreeSet<Integer> elements = new TreeSet<>();
		Map<String, Integer> test = new HashMap<>();
		Map<String, HashSet<Integer>> nestedTest = new HashMap<>();
		HashSet<Integer> one = new HashSet<>();
		HashSet<Integer> two= new HashSet<>();
		HashSet<Integer> three= new HashSet<>();
		
		TreeMap<String, TreeSet<Integer>> elements = new TreeMap<>();
		elements.put("a", new TreeSet<>());
		elements.put("b", new TreeSet<>());
		elements.put("c", new TreeSet<>());

		elements.get("a").add(1);
		elements.get("b").add(2);
		elements.get("b").add(3);
		elements.get("b").add(4);
		
		one.add(1);
		two.add(1);
		two.add(2);
		three.add(1);
		three.add(2);
		three.add(3);
		
		
		test.put("one", 1);
		test.put("two", 2);
		test.put("three",3);
		nestedTest.put("one", one);
		nestedTest.put("two", two);
		nestedTest.put("three", three);
		
		System.out.println(asNestedObject(elements));
		
		System.out.println(nestedTest);
		
		System.out.println(asNestedObject(nestedTest));
		
		
		System.out.println(test.toString());
		
//		System.out.println(asObject(test));
//		System.out.println("Empty:");
//		System.out.println(asArray(elements));
//
//		elements.add(65);
//		System.out.println("\nSingle:");
//		System.out.println(asArray(elements));
//
//		elements.add(66);
//		elements.add(67);
//		System.out.println("\nSimple:");
//		System.out.println(asArray(elements));
	}
}
