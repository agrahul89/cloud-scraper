package pvt.webscraper.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import pvt.webscraper.beans.CloudConfigInfo;
import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.exceptions.AppException;
import pvt.webscraper.process.executor.WorkLoadController;
import pvt.webscraper.process.executor.tasks.ResourceDownloadTask;
import pvt.webscraper.utils.AppUtils;
import pvt.webscraper.utils.PropertyReader;
import pvt.webscraper.utils.QueueFileUtils;

public enum Process {

	INSTANCE;
	private static final Logger LOGGER = LogManager.getLogger(Process.class);

	public void beginProcess() throws AppException {

		List<CloudConfigInfo> cloudConfigInfos = getCloudConfigInfo();

		for (CloudConfigInfo cloudConfigInfo : cloudConfigInfos) {
			String pageNum = StringUtils.EMPTY;
			try {
				pageNum = determinePageNum(cloudConfigInfo);
			} catch (final IllegalArgumentException ex) {
				LOGGER.error(ex);
			}
			if (NumberUtils.toInt(pageNum, -1) > 0) {
				LOGGER.info("Initializing Download processing for Page :: " + pageNum);
				// Set identified Page Number in the Configuration Bean
				cloudConfigInfo.setPageNum(pageNum);
				// Get All links for Current Page Number
				List<String> links = getAllLinks(cloudConfigInfo.getCloudUrl(), cloudConfigInfo.getFilterCriteria());
				// Get Index for matching Resource
				int index = getRsrcIdxIfFound(cloudConfigInfo, links);
				// Begin Downloading of the Resource
				while ((index >= 0) && (NumberUtils.toInt(cloudConfigInfo.getPageNum(), -1) > 0)) {
					QueueFileUtils.addNewTask(new ResourceDownloadTask(cloudConfigInfo, links.get(index--)));
					// Change Current Page when All Entries are Exhausted
					if (index < 0) {
						index = moveToNextPage(cloudConfigInfo, links, index);
					}
				}
				WorkLoadController.getInstance().initializeProcessing();
				LOGGER.info("Processing Completed Successfully");
			} else {
				LOGGER.info("NO DATA FOUND. Processing for [" + cloudConfigInfo.getCloudId() + "] has been Terminated");
			}
		}
	}

	public String determinePageNum(final CloudConfigInfo inCloudConfigInfo) throws AppException {

		LOGGER.info("Searching Contents for URL :: " + inCloudConfigInfo.getCloudUrl());

		// Get All links for Current Page
		List<String> links = getAllLinks(inCloudConfigInfo.getCloudUrl(), inCloudConfigInfo.getFilterCriteria());

		// Determine if current Page Number Contains the Latest Local Resource
		int index = getRsrcIdxIfFound(inCloudConfigInfo, links);
		String pageNum = StringUtils.EMPTY;
		if (index >= 0) {

			// Latest Local Resource Found. Set Current Page as Start
			pageNum = inCloudConfigInfo.getPageNum();
			LOGGER.info("Resource Found. Entry Position :: " + index);

		} else {
			LOGGER.info("Resource could not be found. RETRY");

			try {
				String resourceId = AppUtils.findResourceId(inCloudConfigInfo, links);
				if (NumberUtils.toInt(resourceId) > NumberUtils.toInt(inCloudConfigInfo.getLatestFileId())) {

					// Check if Latest Local Resource has been run past
					inCloudConfigInfo.setPageNum(String.valueOf(NumberUtils.toInt(inCloudConfigInfo.getPageNum()) + 5));
					pageNum = determinePageNum(inCloudConfigInfo);

				} else if (NumberUtils.toInt(inCloudConfigInfo.getPageNum(), -1) > 0) {

					// Search for resource in previous page
					inCloudConfigInfo.setPageNum(String.valueOf(NumberUtils.toInt(inCloudConfigInfo.getPageNum()) - 1));
					pageNum = determinePageNum(inCloudConfigInfo);

				} else {
					throw new IllegalArgumentException(
							"Invalid Page Number[" + inCloudConfigInfo.getPageNum() + "]. Terminate Processing");
				}

			} catch (final NumberFormatException ex) {
				LOGGER.error("Invalid Resource ID was found against " + inCloudConfigInfo.getLatestFileId() + "\n", ex);
			}
		}
		return pageNum;
	}

	public List<String> getAllLinks(final String inCloudUrl, final String inFilterCriteria) throws AppException {
		Elements elements = WebPageParser.getFilteredElems(inCloudUrl, inFilterCriteria);
		return WebPageParser.getAnchorLinks(elements);
	}

	public List<CloudConfigInfo> getCloudConfigInfo() throws AppException {
		List<String> cloudIds = PropertyReader.getValueAsList(AppConstants.PROP_CLOUD_ID);
		List<CloudConfigInfo> cloudConfigInfos = new ArrayList<>(cloudIds.size());
		for (String cloudId : cloudIds) {
			cloudConfigInfos.add(CloudConfigInfo.build(cloudId));
		}
		return cloudConfigInfos;
	}

	private int getRsrcIdxIfFound(final CloudConfigInfo inInfoBean, final List<String> inLinks) {
		int index = -1;
		for (String link : inLinks) {
			try {
				String resourceId = AppUtils.findResourceId(inInfoBean, link);
				if (NumberUtils.toInt(resourceId) == NumberUtils.toInt(inInfoBean.getLatestFileId())) {
					index = inLinks.indexOf(link);
					break;
				}
			} catch (final NumberFormatException ex) {
				LOGGER.error("Invalid Resource ID was found against " + inInfoBean.getLatestFileId() + "\n", ex);
			}
		}
		return index;
	}

	private int moveToNextPage(CloudConfigInfo cloudConfigInfo, List<String> inLinks, int index) throws AppException {

		// SET New Page & URL
		cloudConfigInfo.setPageNum(String.valueOf(NumberUtils.toInt(cloudConfigInfo.getPageNum()) - 1));

		// GET All Links from New Page
		inLinks.clear();
		inLinks.addAll(getAllLinks(cloudConfigInfo.getCloudUrl(), cloudConfigInfo.getFilterCriteria()));

		// RESET index position to End of New Page
		index = inLinks.size() - 1;

		if (NumberUtils.toInt(cloudConfigInfo.getPageNum(), 0) > 0) {
			LOGGER.info("Initializing Download processing for Page :: " + cloudConfigInfo.getPageNum());
		}

		return index;
	}
}
