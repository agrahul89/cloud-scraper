package pvt.webscraper.process.executor.tasks.factory;

import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class TaskFactory implements ThreadFactory {

	private static final TaskFactory instance = new TaskFactory();

	private TaskFactory() {
		// No-Arg Default Constructor
	}

	public static ThreadFactory getFactory() {
		return new ThreadFactoryBuilder().setThreadFactory(instance).setDaemon(false).setPriority(Thread.NORM_PRIORITY)
				.build();
	}

	@Override
	public Thread newThread(Runnable inTask) {
		return new Thread(inTask);
	}
}