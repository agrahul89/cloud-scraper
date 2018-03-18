package pvt.webscraper.utils;

import java.net.URI;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import pvt.webscraper.beans.CloudConfigInfo;
import pvt.webscraper.beans.ResourceInfo;
import pvt.webscraper.constants.AppConstants;

public final class AppUtils {

	private static final Logger LOGGER = LogManager.getLogger(AppUtils.class);

	private AppUtils() {
		// Default No-Arg Constructor
	}

	public static ResourceInfo copyBean(final CloudConfigInfo inCloudConfig, final Class<ResourceInfo> inResource,
			final String inRsrcUrl) {
		ResourceInfo resource = null;
		LOGGER.debug(inCloudConfig + ", resourceUrl :: " + inRsrcUrl);
		if ((null != inCloudConfig) && (null != inResource)) {
			try {
				resource = inResource.newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				LOGGER.error(ex.getMessage(), ex.getCause());
			}
			if (resource != null) {
				resource.setCloudId(inCloudConfig.getCloudId());
				resource.setDwnldUrl(inRsrcUrl);
				resource.setDwnldUrlRegex(inCloudConfig.getDwnldUrlRegex());
				resource.setFileStoreLocation(inCloudConfig.getFileStoreLocation());
				resource.setRsrcId(findResourceId(inCloudConfig, inRsrcUrl));
			}
		}
		LOGGER.debug(resource);
		return resource;
	}

	public static String extractResourceId(final String resourceName, final String inPrefix) {
		return StringUtils.substringBetween(StringUtils.replace(resourceName, inPrefix, StringUtils.EMPTY),
				AppConstants.STR_SPACE);
	}

	public static String findResourceId(final CloudConfigInfo inInfoBean, final List<String> inLinks) {
		return findResourceId(inInfoBean, inLinks.get(inLinks.size() - 1));
	}

	public static String findResourceId(final CloudConfigInfo inInfoBean, final String inLink) {
		String linkPath = URI.create(inLink).getPath();
		String resourceName = StringUtils.split(linkPath, '/')[2];
		return extractResourceId(resourceName, inInfoBean.getFileNamePrefix());
	}

	public static void writeResourceToFile(String inResourceUrl) {
		// TODO Auto-generated method stub
	}
}
