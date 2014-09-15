package edu.feicui.assistant.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.main.MenuActivity;

/**
 * �Ի��򹤾���
 * 
 * @author Sogrey
 * 
 */
public class DialogUtil extends BaseActivity implements OnDismissListener {

	/** DIALOG name��ͨ�ã� */
	public static final String DIALOG_NAME_STRING = "DIALOG";
	/** ����������ʾ�Ի���ID */
	public static final int DIALOG_ID_ALARM = 0x1;
	/** û������ͷ�Ի���ID */
	public static final int DIALOG_ID_NOCAMERA_CAMERA = 0x2;
	/** ���ֲ����� */
	private MediaPlayer mMediaPlayer;
	/** �Ի���ID �������� */
	private int mID;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mID = intent.getIntExtra(DIALOG_NAME_STRING, 0);
		showDialog(mID);
	}

	@Override
	protected void onDestroy() {
		if (mMediaPlayer != null)
			mMediaPlayer.release();
		super.onDestroy();
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case DIALOG_ID_ALARM:
			return createAlarmDialog(args);
		case DIALOG_ID_NOCAMERA_CAMERA:
			return createNoCameraDialog(args);

		default:
			return super.onCreateDialog(id, args);
		}
	}

	/** ����û��������ܶԻ��� */
	private Dialog createNoCameraDialog(Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.txt_title_dialog_camera));
		// builder.setIcon(R.drawable.icon_alarm_set);
		builder.setMessage(getString(R.string.txt_message_dialog_nocamera_camera));
		builder.setPositiveButton(
				getString(R.string.txt_dialog_button_iknown_alarm), null);
		Dialog dialog = builder.create();
		dialog.setOnDismissListener(this);
		return dialog;
	}

	/** ������������Ի��� */
	private Dialog createAlarmDialog(Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.hint_alarm));
		builder.setIcon(R.drawable.icon_alarm_set);
		builder.setMessage(getString(R.string.txt_dialog_message_timenow_alarm));
		builder.setPositiveButton(
				getString(R.string.txt_dialog_button_iknown_alarm), null);
		Dialog dialog = builder.create();
		dialog.setOnDismissListener(this);
		return dialog;
	}

	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch (id) {
		case DIALOG_ID_ALARM:
			/* ��ȡϵͳĬ���������� */
			Uri uri = RingtoneManager.getActualDefaultRingtoneUri(this,
					RingtoneManager.TYPE_ALARM);
			/* ����ϵͳ�����������ֲ����� */
			mMediaPlayer = MediaPlayer.create(this, uri);
			/* ����ѭ�� */
			mMediaPlayer.setLooping(true);
			/* ��ʼ���� */
			mMediaPlayer.start();
			break;

		default:
			super.onPrepareDialog(id, dialog, args);
			break;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		Intent intent = new Intent(this, MenuActivity.class);
		startActivity(intent);
		finish();
	}

}
