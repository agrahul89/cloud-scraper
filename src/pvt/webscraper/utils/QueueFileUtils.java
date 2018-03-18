package pvt.webscraper.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.squareup.tape.QueueFile;

import pvt.webscraper.constants.AppConstants;
import pvt.webscraper.exceptions.AppException;
import pvt.webscraper.process.executor.tasks.BaseTask;

public final class QueueFileUtils {

	private static QueueFile failedTaskFile;
	private static String failedTaskQueueFilePath;
	private static final Logger LOGGER = LogManager.getLogger(QueueFileUtils.class);
	private static QueueFile queuedtaskFile;
	private static String taskQueueFilePath;

	static {
		try {
			failedTaskQueueFilePath = PropertyReader.getValueAsText(AppConstants.PROP_FAILED_TASK_PATH);
			failedTaskFile = new QueueFile(new File(failedTaskQueueFilePath));
			taskQueueFilePath = PropertyReader.getValueAsText(AppConstants.PROP_QUEUED_TASK_PATH);
			queuedtaskFile = new QueueFile(new File(taskQueueFilePath));
		} catch (final IOException ex) {
			LOGGER.error("Failed to create Queues in FileSystem Path");
		}
	}

	private QueueFileUtils() {
		// Default No-Arg Constructor
	}

	public static void addFailedTask(BaseTask inTask) throws AppException {
		add(inTask, failedTaskFile);
	}

	public static void addNewTask(BaseTask inTask) throws AppException {
		add(inTask, queuedtaskFile);
	}

	public static BaseTask fetchFailedTask() throws AppException {
		return fetch(failedTaskFile);
	}

	public static BaseTask fetchTask() throws AppException {
		return fetch(queuedtaskFile);
	}

	public static int getFailedTaskQueueSize() {
		return getQueueSize(failedTaskFile);
	}

	public static int getTaskQueueSize() {
		return getQueueSize(queuedtaskFile);
	}

	public static void initializeCleanup() throws IOException {
		File queueFile = new File(taskQueueFilePath);
		if (getTaskQueueSize() == 0) {
			queuedtaskFile.close();
			LOGGER.info("Delete Task Queue File? :: " + FileUtils.deleteQuietly(queueFile));
		} else {
			LOGGER.info("Cannot Delete non-Empty Task Queue File :: " + taskQueueFilePath);
		}
		failedTaskFile.close();
		queueFile = new File(failedTaskQueueFilePath);
		LOGGER.info("Delete Failed Task Queue File? :: " + FileUtils.deleteQuietly(queueFile));
	}

	public static boolean isFailedTaskQueueEmpty() {
		return getFailedTaskQueueSize() == 0;
	}

	public static boolean isTaskQueueEmpty() {
		return getTaskQueueSize() == 0;
	}

	private static void add(BaseTask inTask, QueueFile inFile) throws AppException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(inTask);
			inFile.add(baos.toByteArray());
		} catch (final IOException ex) {
			throw new AppException(ex);
		}
	}

	private static BaseTask fetch(QueueFile inFile) throws AppException {
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		BaseTask task = null;
		try {
			bis = new ByteArrayInputStream(inFile.peek());
			ois = new ObjectInputStream(bis);
			Object temp = ois.readObject();
			if (temp != null && BaseTask.class.isAssignableFrom(temp.getClass())) {
				task = BaseTask.class.cast(temp);
			}
			inFile.remove(); // Remove top queue item from file
		} catch (final ClassNotFoundException | IOException ex) {
			throw new AppException(ex);
		} finally {
			IOUtils.closeQuietly(ois);
			IOUtils.closeQuietly(bis);
		}
		return task;
	}

	private static int getQueueSize(QueueFile inQueueFile) {
		return inQueueFile.size();
	}
}
