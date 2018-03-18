package pvt.webscraper.beans;

import java.io.File;
import java.io.Serializable;

public class ResourceInfo implements Serializable {

	private static final long serialVersionUID = 1535295912932475064L;
	private String cloudId;
	private String dwnldUrl;
	private String dwnldUrlRegex;
	private String fileName;
	private File fileStoreLocation;
	private String rsrcId;

	public String getCloudId() {
		return cloudId;
	}

	public String getDwnldUrl() {
		return dwnldUrl;
	}

	public String getDwnldUrlRegex() {
		return dwnldUrlRegex;
	}

	public String getFileName() {
		return fileName;
	}

	public File getFileStoreLocation() {
		return fileStoreLocation;
	}

	public String getFileStorePath() {
		return fileStoreLocation.getAbsolutePath();
	}

	public String getRsrcId() {
		return rsrcId;
	}

	public void setCloudId(String inCloudId) {
		cloudId = inCloudId;
	}

	public void setDwnldUrl(String inDwnldUrl) {
		dwnldUrl = inDwnldUrl;
	}

	public void setDwnldUrlRegex(String inDwnldUrlRegex) {
		dwnldUrlRegex = inDwnldUrlRegex;
	}

	public void setFileName(String inFileName) {
		fileName = inFileName;
	}

	public void setFileStoreLocation(File inFileStoreLocation) {
		fileStoreLocation = inFileStoreLocation;
	}

	public void setFileStoreLocation(String inFileStoreLocation) {
		fileStoreLocation = new File(inFileStoreLocation);
	}

	public void setRsrcId(String inRsrcId) {
		rsrcId = inRsrcId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName()).append('[');
		builder.append("cloudId :: ").append(cloudId);
		builder.append(", dwnldUrl :: ").append(dwnldUrl);
		builder.append(", dwnldUrlRegex :: ").append(dwnldUrlRegex);
		builder.append(", rsrcId :: ").append(rsrcId);
		builder.append(", fileStoreLocation :: ").append(fileStoreLocation);
		builder.append(", fileName :: ").append(fileName);
		return builder.append(']').toString();
	}
}
