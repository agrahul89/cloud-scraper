package pvt.webscraper.constants;

public final class AppConstants {

	public static final String CONST_URL_FILE_EXTN = "ext";
	public static final String CONST_URL_FILE_NAME = "filename";

	public static final int EXIT_CODE_FAILURE = 1;
	public static final int EXIT_CODE_SUCCESS = 0;

	public static final String PROP_CLOUD_ID = "jsoup.cloud.id";
	public static final String PROP_CONN_TIME_OUT_MINS = "jsoup.connect.time.out.mins";
	public static final String PROP_CONTENT_SIZE_MBYTES = "jsoup.content.size.mbytes";
	public static final String PROP_FAILED_TASK_PATH = "jsoup.task.path.failed";
	public static final String PROP_INVALID_CHARS_WIN = "jsoup.invalid.chars.win";
	public static final String PROP_QUEUE_READ_PAUSE_TM_MILLIS = "jsoup.queue.read.pause.time.millis";
	public static final String PROP_QUEUED_TASK_PATH = "jsoup.task.path.queued";
	public static final String PROP_REPLACE_CHARS_WIN = "jsoup.replace.chars.win";
	public static final String PROP_THREAD_POOL_SIZE = "jsoup.thread.pool.size";

	public static final String STR_DOT = ".";
	public static final String STR_EQUALS = "=";
	public static final String STR_PARAMS_PAGENUM = "pageNum";
	public static final String STR_SEMICOLON = ";";
	public static final String STR_SPACE = " ";
	public static final String STR_UNDER_SCORE = "_";

	public static final String TMPLT_PROP_DWNLD_URL_REGEX_GRP = "jsoup.dwnldUrl.regex.grp.{0}";
	public static final String TMPLT_PROP_FILE_INFO = "jsoup.files.info.{0}";

	private AppConstants() {
		// Default No-Arg Constructor
	}
}
