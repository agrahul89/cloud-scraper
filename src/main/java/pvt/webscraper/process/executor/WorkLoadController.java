package pvt.webscraper.process.executor;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.exceptions.AppException;
import pvt.webscraper.utils.PropertyReader;
import pvt.webscraper.utils.QueueFileUtils;

public class WorkLoadController {

	private static WorkLoadController instance;
	private static final Logger LOGGER = LogManager.getLogger(WorkLoadController.class);
	private TaskExecutor executor;

	private final int sleepDurationMillis = PropertyReader.getValueAsInt(AppConstants.PROP_QUEUE_READ_PAUSE_TM_MILLIS);

	private WorkLoadController() {
		// Default No-Arg Constructor
	}

	public static WorkLoadController getInstance() {
		if (instance == null) {
			instance = new WorkLoadController();
		}
		return instance;
	}

	public void initializeCleanup() {
		LOGGER.info("Initialize Cleanup");
		try {
			QueueFileUtils.initializeCleanup();
		} catch (final IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		if (executor != null) {
			executor.shutdown();
		}
		LOGGER.info("Cleanup Completed");
	}

	public void initializeProcessing() {
		LOGGER.debug(this.getClass().getSimpleName() + " Initialized");
		executor = TaskExecutor.getInstance();
		processSubmittedTasks();
		// Retry Tasks that failed Execution
		processFailedTasks();
		LOGGER.debug(this.getClass().getSimpleName() + " Terminated Successfully");
	}

	private void printExecStatus() {
		LOGGER.info("Submitted Task Count :: " + executor.getSubmittedTaskCount());
		LOGGER.info("Completed Task Count :: " + executor.getCompletedTaskCount());
		LOGGER.info("  Pending Task Count :: " + executor.getPendingTaskCount());
		LOGGER.info("   Active Task Count :: " + executor.getActiveTaskCount());
		LOGGER.info("   Failed Task Count :: " + QueueFileUtils.getFailedTaskQueueSize());
	}

	private void processFailedTasks() {
		LOGGER.info("Initialize Failed Task Queue Processing");
		int retryCount = 0;
		// Keep retrying failed item download till failure queue gets empty
		while (!QueueFileUtils.isFailedTaskQueueEmpty() || !TaskExecutor.isProcessingComplete()) {
			LOGGER.info("Executing Failed Tasks. Count :: " + QueueFileUtils.getFailedTaskQueueSize() + ", RETRY :: "
					+ retryCount++);
			for (int queueSize = 0; queueSize < Math.min(executor.getCapacity(),
					QueueFileUtils.getFailedTaskQueueSize()); queueSize++) {
				try {
					TaskExecutor.submitTask(QueueFileUtils.fetchFailedTask());
				} catch (final AppException ex) {
					LOGGER.error("Failure Detected while Pulling Task from Failed Task Queue. Queue Size :: "
							+ QueueFileUtils.getTaskQueueSize(), ex);
				}
			}
			printExecStatus(); // Print Queue Status till all Tasks Complete
			try {
				Thread.sleep(sleepDurationMillis);
			} catch (final InterruptedException ex) {
				LOGGER.warn(this.getClass().getSimpleName() + " was interrupted from sleep", ex);
			}
		}
		LOGGER.info("Failed Task Queue Processing Completed");
	}

	private void processSubmittedTasks() {
		LOGGER.info("Initialize Task Queue Processing");
		while (!QueueFileUtils.isTaskQueueEmpty() || !TaskExecutor.isProcessingComplete()) {
			for (int poolCap = 1; poolCap <= Math.min(executor.getCapacity(),
					QueueFileUtils.getTaskQueueSize()); poolCap++) {
				try {
					TaskExecutor.submitTask(QueueFileUtils.fetchTask());
				} catch (final AppException ex) {
					LOGGER.error("Failure Detected while Pulling Task from Queue. Queue Size :: "
							+ QueueFileUtils.getTaskQueueSize(), ex);
				}
			}
			printExecStatus(); // Print Queue Status till all Tasks Complete
			try {
				Thread.sleep(sleepDurationMillis);
			} catch (final InterruptedException ex) {
				LOGGER.warn(this.getClass().getSimpleName() + " was interrupted from sleep", ex);
			}
		}
		LOGGER.info("Task Queue Processing Completed");
	}
}
