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
 * ����
 * 
 * @author Sogrey
 * 
 */
public class AlarmActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener, OnTimeSetListener {

	/** ҳ����� �ı� */
	protected TextView mTxtTitle;
	/** ���� ���� �ı� */
	protected TextView mTxtDate;
	/** ����ʱ�� ��ť */
	protected Button mBtnSetTime;
	/** ��������/�ر����� */
	protected ToggleButton mTglSwitch;
	/** �������� ��¼����ʱ�� */
	protected Calendar mCalendar;
	/** ��ʱ������ */
	protected AlarmManager mAlarmMgr;
	/** Intent һ����װ�� ����ʱ�����д��ݷ���Ŀ�� */
	protected PendingIntent mPendingIntent;
	/** ����ʱ�� �ı� */
	protected String mSetTimeString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		initViews();
		initComps();
	}

	/** ��ʼ���齨����View�� */
	private void initComps() {
		/* ��ʼ�� mCalendar ��¼��ʱ����ǵ��ø÷�����ʱ�� */
		mCalendar = Calendar.getInstance();
		/* ��ȡϵͳ��ʽ������� */
		mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		/* ��ʱ����Ŀ�� */
		Intent intent = new Intent(AlarmActivity.this, DialogUtil.class);
		intent.putExtra(DialogUtil.DIALOG_NAME_STRING,
				DialogUtil.DIALOG_ID_ALARM);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
	}

	/** ��ʼ��View�齨 */
	private void initViews() {
		mTxtTitle = (TextView) findViewById(R.id.txt_title_page);
		/* ����ҳ����� */
		mTxtTitle.setText(getTitle());
		/* ���ڸ�ʽ */
		String patternDate = Constants.FMT_DATE;
		/* new ��SimpleDateFormat ����sdf */
		SimpleDateFormat sdfDate = new SimpleDateFormat(patternDate);
		/* ��ʽ����ǰʱ�丳�� dateString */
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
		case R.id.btn_alarm_settime:// ����ʱ��
			/* ��ȡres/values/strings.xml ���ַ��� */
			String cancelString = getString(R.string.cancel);
			Calendar calendar = Calendar.getInstance();

			TimePickerDialog tPickerDialog = new TimePickerDialog(
					this/* context ������ */,
					this/* OnTimeSetListener callBack �¼������� */,
					calendar.get(Calendar.HOUR_OF_DAY)/* hourOfDay ����һ����Сʱ��24�� */,
					calendar.get(Calendar.MINUTE)/* minute ������� */, true/*
																		 * is24HourView
																		 * �Ƿ�Ϊ24Сʱ��
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
		case R.id.tgl_switch_alarm:// ���ӿ���
			if (isChecked) {
				/**
				 * ������ʱ���� <br>
				 * ���� type ���������� <br>
				 * ���� triggerAtMillis �����Ӵ���ʱ�� <br>
				 * ���� operation ��ʱ�䵽��֮����ʲô
				 */
				if (!TextUtils.isEmpty(mSetTimeString)) {// �ж���û���趨ʱ��
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
				/* ȡ��mPendingIntent���ӣ�PendingIntent��Equals�����ж���ͬ����������ȡ�� */
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
		/* ����Calendar�е�Сʱ�ͷ��� */
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
