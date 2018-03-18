package pvt.webscraper.process.executor.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import pvt.webscraper.beans.CloudConfigInfo;
import pvt.webscraper.beans.ResourceInfo;
import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.exceptions.AppException;
import pvt.webscraper.process.WebPageParser;
import pvt.webscraper.utils.AppUtils;
import pvt.webscraper.utils.CommonUtils;
import pvt.webscraper.utils.PropertyReader;
import pvt.webscraper.utils.QueueFileUtils;

public class ResourceDownloadTask extends BaseTask implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(ResourceDownloadTask.class);
	private static final long serialVersionUID = 6219637152279191231L;

	public ResourceDownloadTask(final CloudConfigInfo inCloudConfig, final String inDwnldUrl) {
		super(inCloudConfig, inDwnldUrl);
	}

	public ResourceDownloadTask(final ResourceInfo inResource) {
		super(inResource);
	}

	@Override
	public void run() {
		Thread.currentThread().setName(getTaskName());
		LOGGER.debug(getTaskName() + " INITIALIZED");
		boolean failed = false;
		try {
			byte[] imageAsBytes = downloadResource();
			computeResourceName();
			saveInFilesStore(imageAsBytes);

		} catch (final FileNotFoundException ex) {
			failed = true;
			LOGGER.error("Probable Error with Filename {" + resource.getFileName()
					+ "} While Saving File. Find more details below", ex);
		} catch (final IOException ex) {
			failed = true;
			LOGGER.error("Probable Error with Filepath {" + resource.getFileStorePath()
					+ "} While Saving File. Find more details below", ex);
		} catch (final AppException ex) {
			failed = true;
			LOGGER.error(ex.getMessage(), ex);
		} catch (final Exception ex) {
			failed = true;
			LOGGER.error(ex.getMessage(), ex);
		}
		if (failed) {
			// Submit Failed Tasks for Retrial
			try {
				QueueFileUtils.addFailedTask(new ResourceDownloadTask(resource));
			} catch (final AppException ex) {
				LOGGER.error("A Failed Download could not be added to Failed Task Queue. Writing Resource to "
						+ SystemUtils.JAVA_IO_TMPDIR);
				AppUtils.writeResourceToFile(resource.getDwnldUrl());
			}
			LOGGER.info("Submitted Failed Task :: " + getTaskName() + " for Retrial");
		}
		LOGGER.debug(getTaskName() + " COMPLETED");
	}

	private String computeFilename(final Map<String, String> inRegexGroups) {
		String fileName = StringUtils.EMPTY;
		if ((inRegexGroups != null) && !inRegexGroups.isEmpty()) {
			fileName = inRegexGroups.get(AppConstants.CONST_URL_FILE_NAME) + AppConstants.STR_DOT
					+ inRegexGroups.get(AppConstants.CONST_URL_FILE_EXTN);
		}
		return CommonUtils.handleIncompatibleChars(fileName);
	}

	private void computeResourceName() throws AppException {
		// Compute FileName
		LOGGER.debug("Computing filename from Resource URL :: " + resource.getDwnldUrl());
		resource.setFileName(computeFilename(getRegexGroupValues()));
		if (StringUtils.isBlank(resource.getFileName()) || AppConstants.STR_DOT.equals(resource.getFileName())
				|| resource.getFileName().startsWith(AppConstants.STR_DOT)
				|| resource.getFileName().endsWith(AppConstants.STR_DOT)) {
			throw new AppException("Invalid Filename :: " + resource.getFileName());
		}
		LOGGER.debug("Computing Filename SUCCESS :: " + resource.getFileName());
	}

	private byte[] downloadResource() throws AppException {
		// Download Resource
		LOGGER.debug("Resource Download Begin   :: " + resource.getDwnldUrl());
		byte[] imageAsBytes = WebPageParser.downloadAsBytes(url.toExternalForm());
		LOGGER.info("Resource Download SUCCESS :: " + resource.getDwnldUrl());
		return imageAsBytes;
	}

	private Map<String, String> getRegexGroupValues() {
		String regexGrpNmKey = MessageFormat.format(AppConstants.TMPLT_PROP_DWNLD_URL_REGEX_GRP, resource.getCloudId());
		Map<String, String> regexGroups = PropertyReader.getValueAsMap(regexGrpNmKey);
		try {
			CommonUtils.populateValuesToMap(url.toExternalForm(), resource.getDwnldUrlRegex(), regexGroups);
		} catch (final AppException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return regexGroups;
	}

	private void saveInFilesStore(byte[] imageAsBytes) throws IOException {
		String destPath = resource.getFileStorePath() + File.separator + resource.getFileName();
		File file = new File(destPath);
		FileUtils.writeByteArrayToFile(file, imageAsBytes);
		LOGGER.info("File Save to Path SUCCESS :: " + destPath);
	}
}
