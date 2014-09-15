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
 * 对话框工具类
 * 
 * @author Sogrey
 * 
 */
public class DialogUtil extends BaseActivity implements OnDismissListener {

	/** DIALOG name（通用） */
	public static final String DIALOG_NAME_STRING = "DIALOG";
	/** 闹铃响起提示对话框ID */
	public static final int DIALOG_ID_ALARM = 0x1;
	/** 没有摄像头对话框ID */
	public static final int DIALOG_ID_NOCAMERA_CAMERA = 0x2;
	/** 音乐播放器 */
	private MediaPlayer mMediaPlayer;
	/** 对话框ID 用于区分 */
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

	/** 创建没有相机功能对话框 */
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

	/** 创建闹铃响铃对话框 */
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
			/* 获取系统默认闹铃铃声 */
			Uri uri = RingtoneManager.getActualDefaultRingtoneUri(this,
					RingtoneManager.TYPE_ALARM);
			/* 根据系统铃音创建音乐播放类 */
			mMediaPlayer = MediaPlayer.create(this, uri);
			/* 设置循环 */
			mMediaPlayer.setLooping(true);
			/* 开始播放 */
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
