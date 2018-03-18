package pvt.webscraper.utils;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import pvt.webscraper.exceptions.AppException;

public final class ConfigLoader {

	private static Configuration configuration = null;
	private static final Logger LOGGER = LogManager.getLogger(ConfigLoader.class);

	private ConfigLoader() {
		//
	}

	public static Configuration getConfigProps() {
		return configuration;
	}

	public static void loadConfiguration(String inConfigPropPath) throws AppException {
		LOGGER.info("Initializing Configuration");
		String configPropPath = new String(inConfigPropPath);
		// Load Default Configuration if External Configuration Unavailable
		if (StringUtils.isBlank(configPropPath)) {
			throw new AppException("Configuration File is Unavailable");
		}
		LOGGER.info("Loading Configuration File Contents to Utility");
		try {
			setupConfiguration(configPropPath);
		} catch (final ConfigurationException ex) {
			LOGGER.error("Configuration Details Loading Failed");
			throw new AppException(ex);
		}
		LOGGER.info("Configuration Details Loaded Successfully");
	}

	private static void setupConfiguration(String configPropPath) throws ConfigurationException {
		File configPropFile = FileUtils.getFile(configPropPath);
		FileChangedReloadingStrategy reloadStrategy = new FileChangedReloadingStrategy();
		reloadStrategy.setRefreshDelay(3000L);
		PropertiesConfiguration configuration = new PropertiesConfiguration(configPropFile);
		configuration.setAutoSave(true);
		configuration.setReloadingStrategy(reloadStrategy);
		ConfigLoader.configuration = configuration;
	}
}
