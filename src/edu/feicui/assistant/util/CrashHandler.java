package edu.feicui.assistant.util;

import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * ���񲢴�����Щû����try...catch������쳣
 * 
 * @author Sogrey
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler mDefaultExceptionHandler;
	/**
	 * ����ģʽ
	 * 
	 * һ�������ֻ����һ��ʵ��
	 * 
	 * 1����һ��˽�о�̬��Ա 
	 * 2����һ��������̬����getInstance�õ����˽�о�̬��Ա 
	 * 3����һ��˽�еĹ��췽����������ʵ������
	 */
	private static CrashHandler sInstance;

	/* һ��������̬����getInstance�õ����˽�о�̬��Ա */
	public static CrashHandler getInstance() {
		/* ˫�ؼ������ */
		if (sInstance == null) {
			synchronized (CrashHandler.class) {
				if (sInstance == null) {
					sInstance = new CrashHandler();
				}
			}
		}
		return sInstance;
	}

	private CrashHandler() {
	}

	/** ��ʼ�� */
	public void init() {
		mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);

	}

	/** �����쳣 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		outputException(thread, ex);
		if (mDefaultExceptionHandler != null) {
			mDefaultExceptionHandler.uncaughtException(thread, ex);
		}
	}

	/** ���쳣������ļ� */
	private void outputException(Thread thread, Throwable Throwed) {
		try {
			PrintWriter pWriter = new PrintWriter(Constants.LOG_PATH);
			Throwable throwable = Throwed;
			do {
				throwable.printStackTrace(pWriter);
			} while ((throwable = throwable.getCause()) != null);
			pWriter.flush();
			pWriter.close();
		} catch (Throwable ex) {
		}
	}
}
