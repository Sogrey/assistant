package edu.feicui.assistant.util;

import android.os.Environment;

/**
 * 常量列表
 * 
 * @author Sogrey
 * */
public final class Constants {

	/** 闹铃界面设置按钮显示设置时间格式 */
	public final static String FMT_TIME = "kk:mm";
	/** 闹铃界面显示日期格式 */
	public final static String FMT_DATE = "yyyy-MM-dd EEEE";
	/** SD卡路径 */
	public final static String PATH_SDCARD = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	/**
	 * Log文件保存路径<br>
	 * "/sdcard/assistant/assistant.txt"
	 */
	public final static String LOG_PATH = PATH_SDCARD
			+ "/assistant/assistant.txt";
	/**
	 * txt文件保存路径<br>
	 * "/sdcard/assistant/info.txt"
	 */
	public final static String INFO_PATH = PATH_SDCARD + "/assistant/info.txt";
	/** 相机保存图片按时间命名，时间格式化 */
	public final static String FMT_FILENAME = "yyyyMMddkkmmssSSS";

	/** 电池监控标签TabSpec */
	public static final String MONITOR = "Monitor";
	/** 电池维护标签TabSpec */
	public static final String MAINTENANCE = "Maintenance";

	/** 手机加速标签TabSpec */
	public static final String SPEEDUP = "Speedup";
	/** 系统检测标签TabSpec */
	public static final String SYSTEM_TEST = "System Test";

	/** 系统软件标签TabSpec */
	public static final String SYSTEM_SOFTWARE = "System Software";
	/** 用户软件标签TabSpec */
	public static final String USER_SOFTWARE = "User Software";
	/** 应用类型 -System/User */
	public static final String TYPE_SOFTWARE = "Type_Software";

	/** 通话记录显示日期格式 */
	public final static String FMT_DATE_CALLS = "yyyy-MM-dd HH:mm:ss";
	/** 通话记录显示通化持续时间格式 */
	public final static String FMT_DURATION_CALLS = "HH:mm:ss";

	/** 通话记录标签TabSpec */
	public static final String CALLS = "Calls";
	/** 联系人标签TabSpec */
	public static final String PEOPLE = "People";
}
