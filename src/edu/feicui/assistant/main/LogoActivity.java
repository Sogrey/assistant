/**
 * 
 */
package edu.feicui.assistant.main;

import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Logo显示，两秒钟后自动跳转到MenuActivity
 * 
 * @author Sogrey
 * 
 */
public class LogoActivity extends BaseActivity {

	protected Handler mHandler;
	/* Logo 界面跳转延迟（毫秒） */
	private static final int DELAYED = 2000;
	/* Logo 界面跳转Menu 界面 事件处理标记 */
	private static final int LOGO_GOTO_MENU = 0x1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		initComps();
	}

	/** 初始化组建 （除View外） */
	private void initComps() {
		mHandler = new LogoHandle();
		Message msg = Message.obtain();
		msg.what = LOGO_GOTO_MENU;
		/* 延迟两秒发送消息 */
		mHandler.sendMessageDelayed(msg, DELAYED);
	}

	class LogoHandle extends Handler {
		/** Handle消息处理 */
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case LOGO_GOTO_MENU:// 跳转至Menu界面
				gotoMenuActivity();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	/** Logo 界面跳转至 Menu 界面 */
	private void gotoMenuActivity() {
		Intent intent = new Intent(LogoActivity.this, MenuActivity.class);
		startActivity(intent);
		finish();
	}
}
