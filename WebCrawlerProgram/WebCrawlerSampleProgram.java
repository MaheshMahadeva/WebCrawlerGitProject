import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author Mahesh Mahadeva
 * 
 * This Class is for Crawling web links and resource from http://wiprodigital.com/
 */
public class WebCrawlerSampleProgram {
	static Map<String, Set<String>> mainDomainCrawlingResult;;
	/**
	 * main method which runs on execution
	 * @param String[] args
	 *  In This Example we don't pass any argument from command prompt
	 *  
	 */
	public static void main(String[] args) {
		System.out.println("Job Started");
		System.out.println("Please wait!! It Takes few minutes");
		String startURL = "http://wiprodigital.com/";
		mainDomainCrawlingResult = getLinksAndResourceDetails(startURL);
		System.out.println("Got Details Of Home Page");
		List<Map<String, Set<String>>> sameDomainLinksCrawlingResultList = new ArrayList<Map<String, Set<String>>>();
		System.out.println("Crawling Each links with in Domain");
		for (String str : mainDomainCrawlingResult.get(LINKS_WITH_DOMAIN)) {
			//Crawling Links which are with in domain
			if (!str.endsWith("xmlrpc.php"))//the links ends with "xmlrpc.php" return error hence ignored 
				sameDomainLinksCrawlingResultList.add(getLinksAndResourceDetails(str));
		}
		System.out.println("Almost End.. About to print");
		//Updating main results Map
		for (Map<String, Set<String>> tempMap : sameDomainLinksCrawlingResultList) {
			mainDomainCrawlingResult.get(LINKS_WITH_DOMAIN).addAll(tempMap.get(LINKS_WITH_DOMAIN));
			mainDomainCrawlingResult.get(EXTERNAL_LINKS).addAll(tempMap.get(EXTERNAL_LINKS));
			mainDomainCrawlingResult.get(SCRIPT_URL).addAll(tempMap.get(SCRIPT_URL));
			mainDomainCrawlingResult.get(IMAGE_LINKS).addAll(tempMap.get(IMAGE_LINKS));
		}
		//printing details into file for easy reference
		File file = new File("CrawlResults.txt");
		PrintWriter writer = null;
			try {
				file.createNewFile();
				 writer = new PrintWriter(new FileWriter(file),true);
				for (Entry<String, Set<String>> en : mainDomainCrawlingResult.entrySet()) {
					writer.println("====="+en.getKey()+"=====");
					System.out.println("======" + en.getKey() + "=======");
					for (String str : en.getValue()) {
						System.out.println(str);
						writer.println(str);
					}
					writer.println();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				writer.close();
			}
		System.out.println("Job Completed Please check CrawlResults.txt file");
	}
	/**
	 * This Method is used to crawl given url and returns links and resource detials
	 * @param String href
	 *  It accepts URL as href
	 *  @return Map<String, Set<String>>
	 *  It will have all the links and Resources details
	 *  
	 */
	public static Map<String, Set<String>> getLinksAndResourceDetails(String href) {
		System.out.println("Fetching Details of " + href);
		Map<String, Set<String>> structuredMap = new HashMap<String, Set<String>>();
		structuredMap.put(LINKS_WITH_DOMAIN, new HashSet<String>());
		structuredMap.put(EXTERNAL_LINKS, new HashSet<String>());
		structuredMap.put(SCRIPT_URL, new HashSet<String>());
		structuredMap.put(IMAGE_LINKS, new HashSet<String>());
		URL url = null;
		Scanner scanner = null;
		try {
			url = new URL(href);
			URLConnection site = url.openConnection();
			InputStream is = null;
			if (site != null && !site.getURL().toString().endsWith(".php"))
				is = site.getInputStream();
			if (is != null) {
				scanner = new Scanner(new BufferedInputStream(is), "UTF-8");
				scanner.useLocale(Locale.US);
				String result = scanner.useDelimiter(Pattern.compile("\\A")).next();
				String regexp = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
				Pattern pattern = Pattern.compile(regexp);
				Matcher matcher = pattern.matcher(result);
				while (matcher.find()) {
					String w = matcher.group();
					if (w.contains("//wiprodigital.com") && !w.contains(".jpg") && !w.contains(".png")
							&& !w.endsWith(".js")) {
						structuredMap.get(LINKS_WITH_DOMAIN).add(w);
					} else if (w.contains(".jpg") || w.contains(".png") && !w.endsWith(".js")) {
						structuredMap.get(IMAGE_LINKS).add(w);
					} else if (w.endsWith(".js")) {
						structuredMap.get(SCRIPT_URL).add(w);
					} else {
						structuredMap.get(EXTERNAL_LINKS).add(w);
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}
		return structuredMap;
	}

	private final static String LINKS_WITH_DOMAIN = "Links With Same Domain";
	private final static String EXTERNAL_LINKS = "External Links";
	private final static String SCRIPT_URL = "Script URL";
	private final static String IMAGE_LINKS = "Image URL";
}
