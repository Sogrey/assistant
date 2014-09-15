/**
 * 
 */
package edu.feicui.assistant.main;

import edu.feicui.assistant.util.CrashHandler;
import android.app.Application;

/**
 * Application代表整个应用
 * 
 * @author Sogrey
 * 
 */
public class AssistantApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler.getInstance().init();// 初始化
	}
}
