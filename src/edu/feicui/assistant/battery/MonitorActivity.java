/**
 * 
 */
package edu.feicui.assistant.battery;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.baseinterface.Hearer;
import edu.feicui.assistant.receiver.BaseReceiver;
import edu.feicui.assistant.receiver.BatteryReceiver;

/**
 * 电池监控
 * 
 * @author Sogrey
 * 
 */
public class MonitorActivity extends BaseActivity implements Hearer {
	/** 电池当前电量图形显示 */
	protected ImageView mImgBattery;
	/** 电量 */
	protected TextView mTxtLevel;
	/** 电池状态 */
	protected TextView mTxtState;
	/** 电池温度 */
	protected TextView mTxtTempture;
	/** 电池电压 */
	protected TextView mTxtVoltage;
	/** 健康状态 */
	protected TextView mTxtHealth;
	/** 接口状态 */
	protected TextView mTxtPlugged;
	/** 制造工艺 */
	protected TextView mTxtWorkmanship;

	protected static final Double TEMPTURE_RATIO = 10.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_battery);
		initViews();
	}

	@Override
	protected void onStart() {
		super.onStart();
		BatteryReceiver.getInstance(this).addHearer(this);
	}

	@Override
	protected void onStop() {
		BatteryReceiver.getInstance(this).delHearer(this);
		super.onStop();
	}

	private void initViews() {
		mImgBattery = (ImageView) findViewById(R.id.img_level_battery);
		mTxtLevel = (TextView) findViewById(R.id.txt_battery_level);
		mTxtState = (TextView) findViewById(R.id.txt_state_battery);
		mTxtTempture = (TextView) findViewById(R.id.txt_temperature_battery);
		mTxtVoltage = (TextView) findViewById(R.id.txt_voltage_battery);
		mTxtHealth = (TextView) findViewById(R.id.txt_health_battery);
		mTxtPlugged = (TextView) findViewById(R.id.txt_plugged_battery);
		mTxtWorkmanship = (TextView) findViewById(R.id.txt_workmanship_battery);
		mImgBattery = (ImageView) findViewById(R.id.img_level_battery);
	}

	@Override
	public void update(BaseReceiver receiver, Intent data) {
		if (data != null) {
			String technology = data
					.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);// 获取电池制造工艺
			updataBatteryTechnology(technology);
			int level = data.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);// 获取电池当前电量
			updataBatteryLevel(level);
			int tempture = data
					.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);// 获取电池当前温度
			updataBatteryTempture(tempture);
			int voltage = data.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);// 获取电池电压
			updataBatteryVoltage(voltage);
			int state = data.getIntExtra(BatteryManager.EXTRA_STATUS, 0);// 获取电池当前状态
			updataBatteryState(state);
			int health = data.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);// 获取电池当前健康状态
			updataBatteryHealth(health);
			int plugged = data.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);// 获取电池当前接口状态
			updataBatteryPlugged(plugged);
		}
	}

	/** 制造工艺 */
	private void updataBatteryTechnology(String technology) {
		mTxtWorkmanship.setText(technology);
	}

	/** 电池电量 */
	private void updataBatteryLevel(int level) {
		mTxtLevel.setText(level + getString(R.string.txt_percent));
		mImgBattery.setImageLevel(level);
	}

	/** 获取电池使用状态 */
	private void updataBatteryState(int state) {
		String chargeString = null;
		switch (state) {
		case BatteryManager.BATTERY_STATUS_CHARGING:// 充电
			chargeString = getString(R.string.txt_charging_battery);
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:// 放电、为别人充电
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:// 放电、正常使用中
			chargeString = getString(R.string.txt_no_charging_battery);
			break;
		case BatteryManager.BATTERY_STATUS_FULL:// 已充满
			chargeString = getString(R.string.txt_full_battery);
			break;
		case BatteryManager.BATTERY_STATUS_UNKNOWN:// 未知
		default:
			chargeString = getString(R.string.txt_unknown_battery);
			break;
		}
		mTxtState.setText(chargeString);
	}

	/** 获取电池温度状态 */
	private void updataBatteryTempture(int tempture) {
		mTxtTempture.setText(tempture / TEMPTURE_RATIO
				+ getString(R.string.txt_celsius_battery));
	}

	/** 获取电池电压状态 */
	private void updataBatteryVoltage(int voltage) {
		mTxtVoltage
				.setText(voltage + getString(R.string.txt_millivolt_battery));
	}

	/** 获取电池健康状态 */
	private void updataBatteryHealth(int health) {
		String healthString = null;
		switch (health) {
		case BatteryManager.BATTERY_HEALTH_GOOD:// 良好
			healthString = getString(R.string.txt_good_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:// 温度过高
			healthString = getString(R.string.txt_over_heat_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_DEAD:// 电池报废
			healthString = getString(R.string.txt_dead_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:// 电压过高
			healthString = getString(R.string.txt_over_vcltage_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:// 未知原因导致电池损坏
			healthString = getString(R.string.txt_unspecified_failure_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_COLD:// 温度过低
			healthString = getString(R.string.txt_cold_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_UNKNOWN:// 未知
		default:
			healthString = getString(R.string.txt_unknown_battery);
			break;
		}
		mTxtHealth.setText(healthString);
	}

	/** 获取电池接口状态 */
	private void updataBatteryPlugged(int plugged) {
		String pluggedString = null;
		switch (plugged) {
		case BatteryManager.BATTERY_PLUGGED_AC:// 充电器连接
			pluggedString = getString(R.string.txt_ac_battery);
			break;
		case BatteryManager.BATTERY_PLUGGED_USB:// USB连接
			pluggedString = getString(R.string.txt_usb_battery);
			break;
		case BatteryManager.BATTERY_PLUGGED_WIRELESS:// 无线充电中
			pluggedString = getString(R.string.txt_wireless_battery);
			break;
		case 0:// 接口未连接
		default:
			pluggedString = getString(R.string.txt_noplugged_battery);
			break;
		}
		mTxtPlugged.setText(pluggedString);
	}
}
