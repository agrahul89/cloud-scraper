package pvt.webscraper.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Splitter;

import pvt.webscraper.constants.AppConstants;

public final class PropertyReader {

	private static final Configuration CONFIG_PROPS = ConfigLoader.getConfigProps();

	private PropertyReader() {
		//
	}

	public static final String[] getValueAsArray(final String inKey) {
		String[] output = {};
		List<String> values = getValueAsList(inKey);
		if ((null != values) && !values.isEmpty()) {
			output = values.toArray(new String[values.size()]);
		}
		return output;
	}

	public static final boolean getValueAsBool(final String inKey) {
		return CONFIG_PROPS.getBoolean(inKey);
	}

	public static final char getValueAsChar(final String inKey) {
		String value = CONFIG_PROPS.getString(inKey);
		char literal = '\0';
		if (StringUtils.isNotBlank(value)) {
			literal = value.charAt(0);
		}
		return literal;
	}

	public static final int getValueAsInt(final String inKey) {
		return CONFIG_PROPS.getInt(inKey);
	}

	public static final List<String> getValueAsList(final String inKey) {
		List<Object> objects = CONFIG_PROPS.getList(inKey);
		List<String> values = new ArrayList<String>();
		if ((null != objects) && !objects.isEmpty()) {
			for (Object obj : objects) {
				values.add(String.valueOf(obj));
			}
		}
		return values;
	}

	public static final Map<String, String> getValueAsMap(final String inKey) {
		String data = getValueAsText(inKey);
		Map<String, String> output = new HashMap<String, String>(
				Splitter.on(AppConstants.STR_SEMICOLON).withKeyValueSeparator(AppConstants.STR_EQUALS).split(data));
		return output;
	}

	public static final String getValueAsText(final String inKey) {
		return CONFIG_PROPS.getString(inKey, StringUtils.EMPTY);
	}
}
