package edu.feicui.assistant.util;

import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 捕获并处理那些没有用try...catch捕获的异常
 * 
 * @author Sogrey
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler mDefaultExceptionHandler;
	/**
	 * 单例模式
	 * 
	 * 一个类最多只能有一个实例
	 * 
	 * 1、有一个私有静态成员 
	 * 2、有一个公开静态方法getInstance得到这个私有静态成员 
	 * 3、有一个私有的构造方法（不允许被实例化）
	 */
	private static CrashHandler sInstance;

	/* 一个公开静态方法getInstance得到这个私有静态成员 */
	public static CrashHandler getInstance() {
		/* 双重检查锁定 */
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

	/** 初始化 */
	public void init() {
		mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);

	}

	/** 捕获异常 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		outputException(thread, ex);
		if (mDefaultExceptionHandler != null) {
			mDefaultExceptionHandler.uncaughtException(thread, ex);
		}
	}

	/** 把异常输出到文件 */
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
