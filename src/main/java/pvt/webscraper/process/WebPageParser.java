package pvt.webscraper.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.exceptions.AppException;
import pvt.webscraper.utils.PropertyReader;

public class WebPageParser {

	private static final Logger LOGGER = LogManager.getLogger(WebPageParser.class);

	public static byte[] downloadAsBytes(final String inUrl) throws AppException {
		byte[] content = new byte[0];
		try {
			content = getConnection(inUrl).ignoreContentType(true).execute().bodyAsBytes();
		} catch (final IOException ex) {
			throw new AppException("Resource Download FAILED :: " + inUrl, ex);
		}
		return content;
	}

	public static String getAnchorLink(Element inElement) {
		String link = inElement.attr("href");
		LOGGER.debug("Extracted Link :: " + link);
		// TODO Remove when no longer required
		if (!StringUtils.startsWith(link, "https:")) {
			link = "https:" + link;
		}
		return link;
	}

	public static List<String> getAnchorLinks(Elements inElements) {
		List<String> links = new ArrayList<>(inElements.size());
		for (Element element : inElements) {
			links.add(getAnchorLink(element));
		}
		return links;
	}

	public static Elements getFilteredElems(final String inUrl, final String inCssCritFilter) throws AppException {
		return getPage(inUrl).select(inCssCritFilter);
	}

	private static Connection getConnection(final String inUrl) {
		int maxContentSizeBytes = PropertyReader.getValueAsInt(AppConstants.PROP_CONTENT_SIZE_MBYTES) * 1024 * 1024;
		int timeoutMillis = PropertyReader.getValueAsInt(AppConstants.PROP_CONN_TIME_OUT_MINS) * 60 * 1000;
		LOGGER.debug("MAX Content Size(bytes) :: " + maxContentSizeBytes + ", Timeout(ms):: " + timeoutMillis);
		return Jsoup.connect(inUrl).timeout(timeoutMillis).maxBodySize(maxContentSizeBytes);
	}

	private static Document getPage(final String inUrl) throws AppException {
		try {
			return getConnection(inUrl).get();
		} catch (final IOException ex) {
			throw new AppException("Failed to Connect to URL :: " + inUrl, ex);
		}
	}
}
