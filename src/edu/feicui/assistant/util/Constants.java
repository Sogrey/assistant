package edu.feicui.assistant.util;

import android.os.Environment;

/**
 * �����б�
 * 
 * @author Sogrey
 * */
public final class Constants {

	/** ����������ð�ť��ʾ����ʱ���ʽ */
	public final static String FMT_TIME = "kk:mm";
	/** ���������ʾ���ڸ�ʽ */
	public final static String FMT_DATE = "yyyy-MM-dd EEEE";
	/** SD��·�� */
	public final static String PATH_SDCARD = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	/**
	 * Log�ļ�����·��<br>
	 * "/sdcard/assistant/assistant.txt"
	 */
	public final static String LOG_PATH = PATH_SDCARD
			+ "/assistant/assistant.txt";
	/**
	 * txt�ļ�����·��<br>
	 * "/sdcard/assistant/info.txt"
	 */
	public final static String INFO_PATH = PATH_SDCARD + "/assistant/info.txt";
	/** �������ͼƬ��ʱ��������ʱ���ʽ�� */
	public final static String FMT_FILENAME = "yyyyMMddkkmmssSSS";

	/** ��ؼ�ر�ǩTabSpec */
	public static final String MONITOR = "Monitor";
	/** ���ά����ǩTabSpec */
	public static final String MAINTENANCE = "Maintenance";

	/** �ֻ����ٱ�ǩTabSpec */
	public static final String SPEEDUP = "Speedup";
	/** ϵͳ����ǩTabSpec */
	public static final String SYSTEM_TEST = "System Test";

	/** ϵͳ�����ǩTabSpec */
	public static final String SYSTEM_SOFTWARE = "System Software";
	/** �û������ǩTabSpec */
	public static final String USER_SOFTWARE = "User Software";
	/** Ӧ������ -System/User */
	public static final String TYPE_SOFTWARE = "Type_Software";

	/** ͨ����¼��ʾ���ڸ�ʽ */
	public final static String FMT_DATE_CALLS = "yyyy-MM-dd HH:mm:ss";
	/** ͨ����¼��ʾͨ������ʱ���ʽ */
	public final static String FMT_DURATION_CALLS = "HH:mm:ss";

	/** ͨ����¼��ǩTabSpec */
	public static final String CALLS = "Calls";
	/** ��ϵ�˱�ǩTabSpec */
	public static final String PEOPLE = "People";
}
