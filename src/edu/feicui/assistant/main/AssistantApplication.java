/**
 * 
 */
package edu.feicui.assistant.main;

import edu.feicui.assistant.util.CrashHandler;
import android.app.Application;

/**
 * Application��������Ӧ��
 * 
 * @author Sogrey
 * 
 */
public class AssistantApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler.getInstance().init();// ��ʼ��
	}
}
