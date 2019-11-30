import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans simple, validating HTML 4/5 into plain text.
 */
public class HtmlCleaner {

	/**
	 * The passed html.
	 */
	private String html;

	/**
	 * List of URLs.
	 */
	private final List<URL> urlList;

	/**
	 * Gets the html
	 *
	 * @return the html as a string
	 */
	public String getHtml() {
		return this.html;
	}

	/**
	 * Gets the URLs
	 *
	 * @return an unmodifiable list of URLs.
	 */
	public List<URL> getUrls() {
		return Collections.unmodifiableList(this.urlList);
	}

	/**
	 * Constructor for the cleaner.
	 *
	 * @param base base URL
	 * @param html given HTML
	 */
	public HtmlCleaner(URL base, String html) {
		if (html != null) {
			html = stripComments(html);

			html = stripElement(html, "head");
			html = stripElement(html, "style");
			html = stripElement(html, "script");
			html = stripElement(html, "noscript");
			html = stripElement(html, "svg");
			this.urlList = Collections.unmodifiableList(listLinks(base, html));
			html = stripTags(html);
			html = stripEntities(html);
			this.html = html;
		} else {
			this.urlList = Collections.emptyList();
		}
	}

	/**
	 * Lists all the links
	 *
	 * @param base the base
	 * @param html the html
	 * @return a list of the links
	 */
	public static ArrayList<URL> listLinks(URL base, String html) {
		String regex = "(?msi)<a\\s+?(?:[^>]*?\\s+)?href\\s*?=\\s*?([\\\\\"])(.*?)\\1";
		ArrayList<URL> matches = new ArrayList<URL>();

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);

		int index = 0;

		while (index < html.length() && matcher.find(index)) {

			try {
				matches.add(clean(new URL(base, html.substring(matcher.start(2), matcher.end(2)))));
			} catch (MalformedURLException e) {
				System.out.println("The URL is malformed :/");
			}

			if (matcher.start() == matcher.end()) {
				index = matcher.end() + 1;
			} else {
				index = matcher.end();
			}

		}
		return matches;
	}

	/**
	 * Cleans from an url
	 *
	 * @param url the url to clean
	 * @return a cleaned url
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Removes all HTML tags and certain block elements from the provided text. The
	 * block elements removed include: head, style, script, noscript, and svg.
	 *
	 * @param html the HTML to strip tags and elements from
	 * @return text clean of any HTML tags and certain block elements
	 */
	public static String stripHtml(String html) {
		html = stripBlockElements(html);
		html = stripTags(html);
		html = stripEntities(html);
		return html;
	}

	/**
	 * Removes comments and certain block elements from the provided html. The block
	 * elements removed include: head, style, script, noscript, and svg.
	 *
	 * @param html the HTML to strip comments and block elements from
	 * @return text clean of any comments and certain HTML block elements
	 */
	public static String stripBlockElements(String html) {
		html = stripComments(html);
		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");
		html = stripElement(html, "noscript");
		html = stripElement(html, "svg");
		return html;
	}

	// THE FOLLOWING REPLACE WITH THE EMPTY STRING

	/**
	 * Replaces all HTML entities with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html text including HTML entities to remove
	 * @return text without any HTML entities
	 */
	public static String stripTags(String html) {
		return html.replaceAll("(?is)(<{1}.*?>{1})", "");
	}

	/**
	 * @param html
	 * @return a String
	 */
	public static String stripEntities(String html) {
		return html.replaceAll("&{1}[^\\s]*?;{1}", "");
	}

	// THE FOLLOWING REPLACE WITH A SINGLE SPACE

	/**
	 * Replaces all HTML comments with a single space. For example, "A<!-- B -->C"
	 * will become "A C".
	 *
	 * @param html text including HTML comments to remove
	 * @return text without any HTML comments
	 */
	public static String stripComments(String html) {
		return html.replaceAll("(?mis)<!--.*?-->", " ");
	}

	/**
	 * Replaces everything between the element tags and the element tags themselves
	 * with a single space. For example, consider the html code: *
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed, and
	 * replaced with a single space.
	 *
	 * @param html text including HTML elements to remove
	 * @param name name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 */
	public static String stripElement(String html, String name) {
		return html.replaceAll("(?is)<" + name + "\\b.*?>.*?<\\/" + name + ".*?>", " ");
	}
}