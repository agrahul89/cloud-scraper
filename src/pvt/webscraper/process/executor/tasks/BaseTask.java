package pvt.webscraper.process.executor.tasks;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.Validate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import pvt.webscraper.beans.CloudConfigInfo;
import pvt.webscraper.beans.ResourceInfo;
import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.utils.AppUtils;

public abstract class BaseTask implements Runnable, Serializable {

	private static final Logger LOGGER = LogManager.getLogger(BaseTask.class);
	private static final long serialVersionUID = -4138154626916607927L;

	private final String taskName;
	protected final ResourceInfo resource;
	protected final URL url;

	public BaseTask(final CloudConfigInfo inCloudConfig, final String inDwnldUrl) {
		this(AppUtils.copyBean(inCloudConfig, ResourceInfo.class, inDwnldUrl));
	}

	public BaseTask(final ResourceInfo inResource) {
		Validate.notNull(inResource, "Resource Information Cannot be NULL");
		resource = inResource;
		try {
			url = new URL(resource.getDwnldUrl());
		} catch (final MalformedURLException ex) {
			String errorMessage = "Invalid URL :: " + resource.getDwnldUrl();
			LOGGER.error(errorMessage);
			// TODO Send URL to Invalid URL List
			throw new IllegalArgumentException(errorMessage);
		}
		taskName = resource.getCloudId() + AppConstants.STR_UNDER_SCORE + resource.getRsrcId();
	}

	public String getTaskName() {
		return taskName;
	}

	public String getUrlPath() {
		return url.toExternalForm();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName()).append('[');
		builder.append("taskName :: ").append(taskName);
		builder.append(", dwnldUrl :: ").append(resource.getDwnldUrl());
		builder.append(", rsrcId :: ").append(resource.getRsrcId());
		return builder.append(']').toString();
	}

}
