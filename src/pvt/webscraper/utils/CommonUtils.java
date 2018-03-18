package pvt.webscraper.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.exceptions.AppException;

public final class CommonUtils {

	private static final Logger LOGGER = LogManager.getLogger(CommonUtils.class);

	private CommonUtils() {
		// Default No-Arg Constructor
	}

	public static String getDecodedUrl(final String inUrl) throws AppException {
		if (StringUtils.isBlank(inUrl)) {
			throw new AppException("Invalid URL :: '" + inUrl + "'");
		}
		String decodedUrl = null;
		try {
			decodedUrl = URLDecoder.decode(inUrl, Charsets.UTF_8.toString());
		} catch (final UnsupportedEncodingException ex) {
			LOGGER.error("Invalid Encoding for URL :: " + inUrl, ex);
		}
		// decodedUrl = handleIncompatibleChars(decodedUrl);
		LOGGER.debug("Decoded URL ::" + decodedUrl);
		return decodedUrl;
	}

	public static String handleIncompatibleChars(final String inDecodedUrl) {
		String output = new String(inDecodedUrl);
		if (SystemUtils.IS_OS_WINDOWS) {
			String[] invalidList = PropertyReader.getValueAsArray(AppConstants.PROP_INVALID_CHARS_WIN);
			String[] replaceList = PropertyReader.getValueAsArray(AppConstants.PROP_REPLACE_CHARS_WIN);
			output = StringUtils.replaceEachRepeatedly(output, invalidList, replaceList);
		}
		return output;
	}

	public static void populateValuesToMap(final String inUrl, final String inRegex, Map<String, String> inUrlGrpVals)
			throws AppException {
		String url = getDecodedUrl(inUrl);
		if (StringUtils.isBlank(inRegex)) {
			throw new AppException("Regular Expression Cannot be Null or Blank");
		}
		Matcher matcher = Pattern.compile(inRegex).matcher(url);
		if (matcher.matches() && (inUrlGrpVals != null) && !inUrlGrpVals.isEmpty()) {
			for (Map.Entry<String, String> entry : inUrlGrpVals.entrySet()) {
				String key = entry.getKey();
				try {
					inUrlGrpVals.put(key, matcher.group(key));
				} catch (final IllegalStateException | IllegalArgumentException ex) {
					LOGGER.error(ex.getMessage(), ex);
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
			LOGGER.debug(inUrlGrpVals);
		}
	}
}
