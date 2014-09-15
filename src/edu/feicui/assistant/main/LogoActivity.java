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
 * Logo��ʾ�������Ӻ��Զ���ת��MenuActivity
 * 
 * @author Sogrey
 * 
 */
public class LogoActivity extends BaseActivity {

	protected Handler mHandler;
	/* Logo ������ת�ӳ٣����룩 */
	private static final int DELAYED = 2000;
	/* Logo ������תMenu ���� �¼������� */
	private static final int LOGO_GOTO_MENU = 0x1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		initComps();
	}

	/** ��ʼ���齨 ����View�⣩ */
	private void initComps() {
		mHandler = new LogoHandle();
		Message msg = Message.obtain();
		msg.what = LOGO_GOTO_MENU;
		/* �ӳ����뷢����Ϣ */
		mHandler.sendMessageDelayed(msg, DELAYED);
	}

	class LogoHandle extends Handler {
		/** Handle��Ϣ���� */
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case LOGO_GOTO_MENU:// ��ת��Menu����
				gotoMenuActivity();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	/** Logo ������ת�� Menu ���� */
	private void gotoMenuActivity() {
		Intent intent = new Intent(LogoActivity.this, MenuActivity.class);
		startActivity(intent);
		finish();
	}
}
