/**
 * 
 */
package edu.feicui.assistant.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.feicui.assistant.R;
import edu.feicui.assistant.address.AddressActivity;
import edu.feicui.assistant.alarm.AlarmActivity;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.battery.BatteryActivity;
import edu.feicui.assistant.hardware.HardwareActivity;
import edu.feicui.assistant.software.SoftwareActivity;

/**
 * @author Sogrey
 * 
 */
public class MenuActivity extends BaseActivity implements OnClickListener,
		OnLongClickListener {

	/** Menu界面各功能按钮 */
	protected Button mBtnSoftware, mBtnAddress, mBtnAlarm, mBtnBattery,
			mBtnCamera, mBtnHardware;
	/** Menu界面各功能提示文本 */
	protected TextView mTxtHin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		initViews();
	}

	/** 初始化View组件 */
	private void initViews() {
		mTxtHin = (TextView) findViewById(R.id.txt_hint);
		mBtnAddress = (Button) findViewById(R.id.btn_address);
		mBtnAddress.setOnClickListener(this);
		mBtnAddress.setOnLongClickListener(this);
		mBtnAlarm = (Button) findViewById(R.id.btn_alarm);
		mBtnAlarm.setOnClickListener(this);
		mBtnAlarm.setOnLongClickListener(this);
		mBtnBattery = (Button) findViewById(R.id.btn_battery);
		mBtnBattery.setOnClickListener(this);
		mBtnBattery.setOnLongClickListener(this);
		mBtnCamera = (Button) findViewById(R.id.btn_camera);
		mBtnCamera.setOnClickListener(this);
		mBtnCamera.setOnLongClickListener(this);
		mBtnHardware = (Button) findViewById(R.id.btn_hardware);
		mBtnHardware.setOnClickListener(this);
		mBtnHardware.setOnLongClickListener(this);
		mBtnSoftware = (Button) findViewById(R.id.btn_software);
		mBtnSoftware.setOnClickListener(this);
		mBtnSoftware.setOnLongClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_address:// 通讯录
			mTxtHin.setText(R.string.hint_address);
			break;
		case R.id.btn_alarm:// 闹铃
			mTxtHin.setText(R.string.hint_alarm);
			break;
		case R.id.btn_battery:// 电池
			mTxtHin.setText(R.string.hint_battery);
			break;
		case R.id.btn_camera:// 相机
			mTxtHin.setText(R.string.hint_camera);
			break;
		case R.id.btn_hardware:// 硬件加速
			mTxtHin.setText(R.string.hint_hardware);
			break;
		case R.id.btn_software:// 软件管家
			mTxtHin.setText(R.string.hint_software);
			break;
		}
	}

	@Override
	public boolean onLongClick(View view) {

		int id = view.getId();
		Intent intent = new Intent();
		switch (id) {
		case R.id.btn_address:// 通讯录
			// intent.setClass(MenuActivity.this, AddressTabActivity.class);
			intent.setClass(MenuActivity.this, AddressActivity.class);
			break;
		case R.id.btn_alarm:// 闹钟
			intent.setClass(MenuActivity.this, AlarmActivity.class);
			break;
		case R.id.btn_battery:// 电池管家
			intent.setClass(MenuActivity.this, BatteryActivity.class);
			break;
		case R.id.btn_camera:// 相机
			// intent.setClass(MenuActivity.this, CameraActivity.class);
			intent.setAction("android.media.action.IMAGE_CAPTURE");
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			break;
		case R.id.btn_hardware:// 硬件加速
			intent.setClass(MenuActivity.this, HardwareActivity.class);
			break;
		case R.id.btn_software:// 软件管家
			intent.setClass(MenuActivity.this, SoftwareActivity.class);
			break;
		}
		startActivity(intent);
		return false;
	}

}
