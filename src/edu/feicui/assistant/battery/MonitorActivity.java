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
 * ��ؼ��
 * 
 * @author Sogrey
 * 
 */
public class MonitorActivity extends BaseActivity implements Hearer {
	/** ��ص�ǰ����ͼ����ʾ */
	protected ImageView mImgBattery;
	/** ���� */
	protected TextView mTxtLevel;
	/** ���״̬ */
	protected TextView mTxtState;
	/** ����¶� */
	protected TextView mTxtTempture;
	/** ��ص�ѹ */
	protected TextView mTxtVoltage;
	/** ����״̬ */
	protected TextView mTxtHealth;
	/** �ӿ�״̬ */
	protected TextView mTxtPlugged;
	/** ���칤�� */
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
					.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);// ��ȡ������칤��
			updataBatteryTechnology(technology);
			int level = data.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);// ��ȡ��ص�ǰ����
			updataBatteryLevel(level);
			int tempture = data
					.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);// ��ȡ��ص�ǰ�¶�
			updataBatteryTempture(tempture);
			int voltage = data.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);// ��ȡ��ص�ѹ
			updataBatteryVoltage(voltage);
			int state = data.getIntExtra(BatteryManager.EXTRA_STATUS, 0);// ��ȡ��ص�ǰ״̬
			updataBatteryState(state);
			int health = data.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);// ��ȡ��ص�ǰ����״̬
			updataBatteryHealth(health);
			int plugged = data.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);// ��ȡ��ص�ǰ�ӿ�״̬
			updataBatteryPlugged(plugged);
		}
	}

	/** ���칤�� */
	private void updataBatteryTechnology(String technology) {
		mTxtWorkmanship.setText(technology);
	}

	/** ��ص��� */
	private void updataBatteryLevel(int level) {
		mTxtLevel.setText(level + getString(R.string.txt_percent));
		mImgBattery.setImageLevel(level);
	}

	/** ��ȡ���ʹ��״̬ */
	private void updataBatteryState(int state) {
		String chargeString = null;
		switch (state) {
		case BatteryManager.BATTERY_STATUS_CHARGING:// ���
			chargeString = getString(R.string.txt_charging_battery);
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:// �ŵ硢Ϊ���˳��
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:// �ŵ硢����ʹ����
			chargeString = getString(R.string.txt_no_charging_battery);
			break;
		case BatteryManager.BATTERY_STATUS_FULL:// �ѳ���
			chargeString = getString(R.string.txt_full_battery);
			break;
		case BatteryManager.BATTERY_STATUS_UNKNOWN:// δ֪
		default:
			chargeString = getString(R.string.txt_unknown_battery);
			break;
		}
		mTxtState.setText(chargeString);
	}

	/** ��ȡ����¶�״̬ */
	private void updataBatteryTempture(int tempture) {
		mTxtTempture.setText(tempture / TEMPTURE_RATIO
				+ getString(R.string.txt_celsius_battery));
	}

	/** ��ȡ��ص�ѹ״̬ */
	private void updataBatteryVoltage(int voltage) {
		mTxtVoltage
				.setText(voltage + getString(R.string.txt_millivolt_battery));
	}

	/** ��ȡ��ؽ���״̬ */
	private void updataBatteryHealth(int health) {
		String healthString = null;
		switch (health) {
		case BatteryManager.BATTERY_HEALTH_GOOD:// ����
			healthString = getString(R.string.txt_good_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:// �¶ȹ���
			healthString = getString(R.string.txt_over_heat_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_DEAD:// ��ر���
			healthString = getString(R.string.txt_dead_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:// ��ѹ����
			healthString = getString(R.string.txt_over_vcltage_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:// δ֪ԭ���µ����
			healthString = getString(R.string.txt_unspecified_failure_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_COLD:// �¶ȹ���
			healthString = getString(R.string.txt_cold_battery);
			break;
		case BatteryManager.BATTERY_HEALTH_UNKNOWN:// δ֪
		default:
			healthString = getString(R.string.txt_unknown_battery);
			break;
		}
		mTxtHealth.setText(healthString);
	}

	/** ��ȡ��ؽӿ�״̬ */
	private void updataBatteryPlugged(int plugged) {
		String pluggedString = null;
		switch (plugged) {
		case BatteryManager.BATTERY_PLUGGED_AC:// ���������
			pluggedString = getString(R.string.txt_ac_battery);
			break;
		case BatteryManager.BATTERY_PLUGGED_USB:// USB����
			pluggedString = getString(R.string.txt_usb_battery);
			break;
		case BatteryManager.BATTERY_PLUGGED_WIRELESS:// ���߳����
			pluggedString = getString(R.string.txt_wireless_battery);
			break;
		case 0:// �ӿ�δ����
		default:
			pluggedString = getString(R.string.txt_noplugged_battery);
			break;
		}
		mTxtPlugged.setText(pluggedString);
	}
}
