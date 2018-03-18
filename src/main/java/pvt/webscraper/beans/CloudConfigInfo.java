package pvt.webscraper.beans;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.common.base.Splitter;

import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.exceptions.AppException;
import pvt.webscraper.utils.AppUtils;
import pvt.webscraper.utils.PropertyReader;

public class CloudConfigInfo implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(CloudConfigInfo.class);
	private static final long serialVersionUID = 3285433095083497885L;
	private int approxPageSize;
	private String cloudId;
	private String cloudUrl;
	private String cloudUrlTmplt;
	private String dwnldUrlRegex;
	private String fileNamePrefix;
	private File fileStoreLocation;
	private String filterCriteria;
	private String latestFileId;
	private String pageNum;

	private Map<String, String> params;

	public static final CloudConfigInfo build(final String inCloudId) throws AppException {
		String key = MessageFormat.format(AppConstants.TMPLT_PROP_FILE_INFO, inCloudId);
		String[] values = PropertyReader.getValueAsArray(key);
		CloudConfigInfo infoBean = new CloudConfigInfo();
		if (StringUtils.isBlank(values[0])) {
			throw new AppException("Invalid File Store Path :: '" + values[0] + "'");
		}
		infoBean.setCloudId(inCloudId);
		infoBean.setFileStoreLocation(values[0]);
		infoBean.setFileNamePrefix(values[1]);
		infoBean.setLatestFileId(infoBean.getMaxFileId());
		infoBean.setCloudUrlTmplt(values[2]);
		infoBean.params = new HashMap<>(Splitter.on(AppConstants.STR_SEMICOLON)
				.withKeyValueSeparator(AppConstants.STR_EQUALS).split(values[3]));
		infoBean.setPageNum(infoBean.params.get(AppConstants.STR_PARAMS_PAGENUM));
		infoBean.setFilterCriteria(values[4]);
		infoBean.setApproxPageSize(NumberUtils.toInt(values[5]));
		infoBean.setDwnldUrlRegex(values[6]);
		LOGGER.debug("Bean Creation Succesful :: " + infoBean);
		return infoBean;
	}

	public int getApproxPageSize() {
		return approxPageSize;
	}

	public String getCloudId() {
		return cloudId;
	}

	public String getCloudUrl() {
		return cloudUrl;
	}

	public String getDwnldUrlRegex() {
		return dwnldUrlRegex;
	}

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public File getFileStoreLocation() {
		return fileStoreLocation;
	}

	public String getFilterCriteria() {
		return filterCriteria;
	}

	public String getLatestFileId() {
		return latestFileId;
	}

	public String getPageNum() {
		return pageNum;
	}

	public void setApproxPageSize(int inApproxPageSize) {
		approxPageSize = inApproxPageSize;
	}

	public void setLatestFileId(String inLatestFileId) {
		latestFileId = inLatestFileId;
	}

	public void setPageNum(String inPageNum) {
		pageNum = inPageNum;
		params.put(AppConstants.STR_PARAMS_PAGENUM, pageNum);
		setCloudUrl(cloudUrlTmplt, params);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName()).append('[');
		builder.append("cloudUrl :: ").append(cloudUrl);
		builder.append(", pageNum :: ").append(pageNum);
		builder.append(", approxPageSize :: ").append(approxPageSize);
		builder.append(", fileStoreLocation :: ").append(fileStoreLocation);
		builder.append(", latestFileId :: ").append(latestFileId);
		return builder.append(']').toString();
	}

	private String getMaxFileId() throws AppException {
		List<File> allFiles = FileFilterUtils.filterList(
				FileFilterUtils.prefixFileFilter(fileNamePrefix, IOCase.SENSITIVE), getFileStoreLocation().listFiles());
		if (allFiles.size() <= 0) {
			throw new AppException("No Files available in " + getFileStoreLocation());
		}
		return AppUtils.extractResourceId(allFiles.get(allFiles.size() - 1).getName(), fileNamePrefix);
	}

	private void setCloudId(String inCloudId) {
		cloudId = inCloudId;
	}

	private void setCloudUrl(String inCloudUrlTmplt, Map<String, String> inParams) {
		StrSubstitutor substitutor = new StrSubstitutor(inParams);
		cloudUrl = substitutor.replace(inCloudUrlTmplt);
	}

	private void setCloudUrlTmplt(String inCloudUrlTmplt) {
		cloudUrlTmplt = inCloudUrlTmplt;
	}

	private void setDwnldUrlRegex(String inDwnldUrlRegex) {
		dwnldUrlRegex = inDwnldUrlRegex;
	}

	private void setFileNamePrefix(String inFileNamePrefix) {
		fileNamePrefix = inFileNamePrefix;
	}

	private void setFileStoreLocation(String inFileStoreLocation) {
		fileStoreLocation = new File(inFileStoreLocation);
	}

	private void setFilterCriteria(String inFilterCriteria) {
		filterCriteria = inFilterCriteria;
	}

}
