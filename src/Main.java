
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.exceptions.AppException;
import pvt.webscraper.process.Process;
import pvt.webscraper.process.executor.WorkLoadController;
import pvt.webscraper.utils.ConfigLoader;

public class Main {

	private static int IS_SUCCESS = AppConstants.EXIT_CODE_SUCCESS;
	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	public static void main(String[] args) throws IOException {

		String initIdentifier = StringUtils.EMPTY;
		try {
			initLogger();
			LOGGER.info("########## Initialized Processing [" + new Date() + "] ##########");
			initIdentifier = Long.toString(System.nanoTime(), 36);
			LOGGER.info("Initialization ID :: " + initIdentifier);
			initPropConfig();
		} catch (final AppException ex) {
			ex.printStackTrace();
		}

		try {
			Process.INSTANCE.beginProcess();
		} catch (final AppException ex) {
			LOGGER.error(ex.getMessage(), ex);
			IS_SUCCESS = AppConstants.EXIT_CODE_FAILURE;
		} finally {
			// Clean all used up resources
			WorkLoadController.getInstance().initializeCleanup();
			LOGGER.info("########## Processing[" + initIdentifier + "] Complete ##########");
			System.exit(IS_SUCCESS);
		}
	}

	private static void initLogger() throws AppException {

		String logConfigXmlKey = "JSOUP_LOG_CONFIG_PATH";
		String logConfigXmlPath = System.getProperty(logConfigXmlKey);

		if (StringUtils.isBlank(logConfigXmlPath)) {
			logConfigXmlPath = System.getenv(logConfigXmlKey);
		}

		if (StringUtils.isBlank(logConfigXmlPath)) {
			String errMessage = "Logger Configuration not Found. "
					+ "Please provide a Logger Configuration File Location with JVM or SYSTEM/ENV Key :: "
					+ logConfigXmlKey;
			throw new AppException(errMessage);
		}

		LOGGER.info("Log Configuration File Path :: " + logConfigXmlPath);
		if ("xml".equalsIgnoreCase(FilenameUtils.getExtension(logConfigXmlPath))) {
			DOMConfigurator.configure(logConfigXmlPath);
		} else if ("properties".equalsIgnoreCase(FilenameUtils.getExtension(logConfigXmlPath))
				|| "props".equalsIgnoreCase(FilenameUtils.getExtension(logConfigXmlPath))) {
			PropertyConfigurator.configure(logConfigXmlPath);
		}
	}

	private static void initPropConfig() throws AppException {

		String propFilePathKey = "JSOUP_PROP_PATH";
		String propFilePath = System.getProperty(propFilePathKey);

		if (StringUtils.isBlank(propFilePath)) {
			propFilePath = System.getenv(propFilePathKey);
		}

		if (StringUtils.isBlank(propFilePath)) {
			String errMessage = "Property Configuration not Found. "
					+ "Please Configure a Property File Location with JVM or SYSTEM/ENV Key :: " + propFilePathKey;
			throw new AppException(errMessage);
		}

		LOGGER.info("Property File Path :: " + propFilePath);
		ConfigLoader.loadConfiguration(propFilePath);
	}
}
