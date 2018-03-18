package pvt.webscraper.process.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.process.executor.tasks.BaseTask;
import pvt.webscraper.process.executor.tasks.factory.TaskFactory;
import pvt.webscraper.utils.PropertyReader;

public class TaskExecutor {

	private static volatile TaskExecutor instance;
	private static Logger LOGGER = LogManager.getLogger(TaskExecutor.class);

	private final int poolSize;
	private int submittedTaskCount;
	private ThreadPoolExecutor taskProcessor;

	private TaskExecutor() {
		// Default No-Arg Constructor
		poolSize = PropertyReader.getValueAsInt(AppConstants.PROP_THREAD_POOL_SIZE);
	}

	public static void init() {

	}

	public static boolean isProcessingComplete() {
		return getInstance().getCompletedTaskCount() == getInstance().getSubmittedTaskCount();
	}

	public static void submitFailedTask(Runnable inTask) {
		getInstance().submit(inTask);
	}

	public static void submitTask(final BaseTask inTask) {
		getInstance().submit(inTask);
		String count = StringUtils.leftPad(String.valueOf(++getInstance().submittedTaskCount), 4, "0");
		LOGGER.info("Submitted Task For Execution [" + count + "] :: " + inTask.getTaskName());
	}

	static synchronized TaskExecutor getInstance() {
		if (instance == null) {
			instance = new TaskExecutor();
		}
		if ((instance.taskProcessor == null) || instance.taskProcessor.isShutdown()
				|| instance.taskProcessor.isTerminated()) {
			instance.taskProcessor = ((ThreadPoolExecutor) Executors.newFixedThreadPool(instance.poolSize,
					TaskFactory.getFactory()));
			LOGGER.info(instance.getClass().getSimpleName() + " Initialized");
		}
		return instance;
	}

	public int getActiveTaskCount() {
		return taskProcessor.getActiveCount();
	}

	public int getCapacity() {
		return taskProcessor.getMaximumPoolSize();
	}

	public long getCompletedTaskCount() {
		return taskProcessor.getCompletedTaskCount();
	}

	public int getPendingTaskCount() {
		return taskProcessor.getQueue().toArray().length;
	}

	public int getSubmittedTaskCount() {
		return submittedTaskCount;
	}

	public void shutdown() {
		if ((!taskProcessor.isTerminated() || !taskProcessor.isShutdown()) && (taskProcessor.getActiveCount() == 0)) {
			LOGGER.info("Initiating TaskProcessor Pool Shutdown Sequence");
			taskProcessor.shutdown();
			LOGGER.info("TaskProcessor Pool has been Shutdown Successfully");
		}
	}

	public void submit(final Runnable inTask) {
		taskProcessor.submit(inTask);
	}

}
