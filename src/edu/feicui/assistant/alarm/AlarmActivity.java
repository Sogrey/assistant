package edu.feicui.assistant.alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.util.Constants;
import edu.feicui.assistant.util.DialogUtil;

/**
 * 闹钟
 * 
 * @author Sogrey
 * 
 */
public class AlarmActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener, OnTimeSetListener {

	/** 页面标题 文本 */
	protected TextView mTxtTitle;
	/** 日期 星期 文本 */
	protected TextView mTxtDate;
	/** 设置时间 按钮 */
	protected Button mBtnSetTime;
	/** 开启闹钟/关闭闹钟 */
	protected ToggleButton mTglSwitch;
	/** 日历类型 记录设置时间 */
	protected Calendar mCalendar;
	/** 定时管理器 */
	protected AlarmManager mAlarmMgr;
	/** Intent 一个封装类 ，定时服务中传递服务目的 */
	protected PendingIntent mPendingIntent;
	/** 设置时间 文本 */
	protected String mSetTimeString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		initViews();
		initComps();
	}

	/** 初始化组建（非View） */
	private void initComps() {
		/* 初始化 mCalendar 记录的时间就是调用该方法的时间 */
		mCalendar = Calendar.getInstance();
		/* 获取系统定式管理服务 */
		mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		/* 定时服务目的 */
		Intent intent = new Intent(AlarmActivity.this, DialogUtil.class);
		intent.putExtra(DialogUtil.DIALOG_NAME_STRING,
				DialogUtil.DIALOG_ID_ALARM);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
	}

	/** 初始化View组建 */
	private void initViews() {
		mTxtTitle = (TextView) findViewById(R.id.txt_title_page);
		/* 设置页面标题 */
		mTxtTitle.setText(getTitle());
		/* 日期格式 */
		String patternDate = Constants.FMT_DATE;
		/* new 出SimpleDateFormat 对象sdf */
		SimpleDateFormat sdfDate = new SimpleDateFormat(patternDate);
		/* 格式化当前时间赋给 dateString */
		String dateString = sdfDate.format(new Date());
		mTxtDate = (TextView) findViewById(R.id.txt_alarm_date);
		mTxtDate.setText(dateString);
		mBtnSetTime = (Button) findViewById(R.id.btn_alarm_settime);
		mBtnSetTime.setOnClickListener(this);
		mTglSwitch = (ToggleButton) findViewById(R.id.tgl_switch_alarm);
		mTglSwitch.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View view) {

		int id = view.getId();
		switch (id) {
		case R.id.btn_alarm_settime:// 设置时间
			/* 获取res/values/strings.xml 中字符串 */
			String cancelString = getString(R.string.cancel);
			Calendar calendar = Calendar.getInstance();

			TimePickerDialog tPickerDialog = new TimePickerDialog(
					this/* context 上下文 */,
					this/* OnTimeSetListener callBack 事件监听器 */,
					calendar.get(Calendar.HOUR_OF_DAY)/* hourOfDay 初设一天中小时（24） */,
					calendar.get(Calendar.MINUTE)/* minute 初设分钟 */, true/*
																		 * is24HourView
																		 * 是否为24小时制
																		 */);
			tPickerDialog.setTitle(R.string.txttitle_alarm_set);
			tPickerDialog.setIcon(R.drawable.icon_alarm_set);
			tPickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE,
					cancelString,
					(android.content.DialogInterface.OnClickListener) null);
			tPickerDialog.show();
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		int id = buttonView.getId();
		switch (id) {
		case R.id.tgl_switch_alarm:// 闹钟开关
			if (isChecked) {
				/**
				 * 开启定时服务 <br>
				 * 参数 type ：闹钟类型 <br>
				 * 参数 triggerAtMillis ：闹钟触发时间 <br>
				 * 参数 operation ：时间到了之后做什么
				 */
				if (!TextUtils.isEmpty(mSetTimeString)) {// 判断有没有设定时间
					mAlarmMgr.set(AlarmManager.RTC,
							mCalendar.getTimeInMillis(), mPendingIntent);
					Toast.makeText(this, getString(R.string.alarm_set_start),
							Toast.LENGTH_LONG).show();
				} else {
					mTglSwitch.setChecked(!isChecked);
					Toast.makeText(this, getString(R.string.alarm_set_noset),
							Toast.LENGTH_LONG).show();
				}

			} else {
				/* 取消mPendingIntent闹钟，PendingIntent的Equals方法判断相同动作，都能取消 */
				mAlarmMgr.cancel(mPendingIntent);
				mBtnSetTime.setText(getString(R.string.btn_alarm_settime));
				Toast.makeText(this, getString(R.string.btn_alarm_off),
						Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		/* 设置Calendar中的小时和分钟 */
		mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		mCalendar.set(Calendar.MINUTE, minute);
		mCalendar.set(Calendar.SECOND, 0);
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.FMT_TIME);
		mSetTimeString = sdf.format(mCalendar.getTime());
		mBtnSetTime.setText(mSetTimeString);
		String setTimeHintString = getString(R.string.hint_settime_alarm)
				+ mSetTimeString;
		// mBtnSetTime.setEnabled(false);
		Toast.makeText(this, setTimeHintString, Toast.LENGTH_LONG).show();
		mTglSwitch.setChecked(true);
	}
}
