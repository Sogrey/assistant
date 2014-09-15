/**
 * 
 */
package edu.feicui.assistant.battery;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
 * 电池维护
 * 
 * @author Sogrey
 * 
 */
public class ChargeActivity extends BaseActivity implements Hearer {
	/** 当前电池使用状态 */
	protected TextView mTxtUseStateNow;
	/** 当前电量状态 */
	protected TextView mTxtLevelNow;
	/** 电池状态 */
	protected ImageView mImgBulbState;
	/** 快速充电-灯泡 */
	protected ImageView mImgBulbQuick;
	/** 循环充电-灯泡 */
	protected ImageView mImgBulbLoop;
	/** 涓流充电-灯泡 */
	protected ImageView mImgBulbTrickle;
	/** 快速充电-tint */
	protected TextView mTxtTintQuick;
	/** 快速充电-状态 */
	protected TextView mTxtStateQuick;
	/** 循环充电-tint */
	protected TextView mTxtTintLoop;
	/** 循环充电-状态 */
	protected TextView mTxtStateLoop;
	/** 涓流充电-tint */
	protected TextView mTxtTintTrickle;
	/** 涓流充电-状态 */
	protected TextView mTxtStateTrickle;
	/** 接口未状态 */
	private static final int BATTERY_NO_PLUGGED = 0;
	/** 未充电状态 */
	private static final int BATTERY_CHARGE_STATE_NONE = 0x01;
	/** 快速充电状态 */
	private static final int BATTERY_CHARGE_STATE_QUICK = 0x02;
	/** 循环充电状态 */
	private static final int BATTERY_CHARGE_STATE_LOOP = 0x03;
	/** 涓流充电状态 */
	private static final int BATTERY_CHARGE_STATE_TRICKLE = 0x04;
	/** 已充满状态 */
	private static final int BATTERY_CHARGE_STATE_FULL = 0x05;
	/** 快速充电电压门槛值上限 */
	private static final int BATTERY_CHARGE_QUICK = 80;
	/** 循环充电电压门槛值上限 */
	private static final int BATTERY_CHARGE_LOOP = 95;
	/** 涓流充电电压门槛值上限 */
	private static final int BATTERY_CHARGE_TRICKLE = 100;
	/** 电池充电状态 */
	protected int mState = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charging_battery);
		initViews();
		initBulb();
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

	/** 初始化View组建 */
	private void initViews() {
		mTxtUseStateNow = (TextView) findViewById(R.id.txt_usenormal_battery);
		mTxtLevelNow = (TextView) findViewById(R.id.txt_levelnow_battery);
		mImgBulbState = (ImageView) findViewById(R.id.img_charging_state_battery);
		mImgBulbQuick = (ImageView) findViewById(R.id.img_bulb_quickcharge_battery);
		mImgBulbLoop = (ImageView) findViewById(R.id.img_bulb_loopcharge_battery);
		mImgBulbTrickle = (ImageView) findViewById(R.id.img_bulb_tricklecharge_battery);
		mTxtTintQuick = (TextView) findViewById(R.id.txt_tint_quickcharge_battery);
		mTxtStateQuick = (TextView) findViewById(R.id.txt_state_quickcharge_battery);
		mTxtTintLoop = (TextView) findViewById(R.id.txt_tint_loopcharge_battery);
		mTxtStateLoop = (TextView) findViewById(R.id.txt_state_loopcharge_battery);
		mTxtTintTrickle = (TextView) findViewById(R.id.txt_tint_tricklecharge_battery);
		mTxtStateTrickle = (TextView) findViewById(R.id.txt_state_tricklecharge_battery);
	}

	/** 初始化灯泡状态和提示文本 */
	private void initBulb() {
		mImgBulbQuick.setImageResource(R.drawable.bulb_11);
		mImgBulbLoop.setImageResource(R.drawable.bulb_11);
		mImgBulbTrickle.setImageResource(R.drawable.bulb_11);
		mTxtTintQuick.setTextColor(getResources().getColor(R.color.black));
		mTxtStateQuick.setText(getString(R.string.txt_tint_waiting_battery));
		mTxtStateQuick.setTextColor(getResources().getColor(R.color.black));
		mTxtTintLoop.setTextColor(getResources().getColor(R.color.black));
		mTxtStateLoop.setText(getString(R.string.txt_tint_waiting_battery));
		mTxtStateLoop.setTextColor(getResources().getColor(R.color.black));
		mTxtTintTrickle.setTextColor(getResources().getColor(R.color.black));
		mTxtStateTrickle.setText(getString(R.string.txt_tint_waiting_battery));
		mTxtStateTrickle.setTextColor(getResources().getColor(R.color.black));
	}

	@Override
	public void update(BaseReceiver receiver, Intent data) {
		if (data != null) {
			int level = data.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int plugged = data.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			updataBatteryCharge(plugged, level);
		}
	}

	/**
	 * 根据接口状态及当前电量刷新界面显示
	 * 
	 * @param plugged
	 *            接口状态
	 * @param level
	 *            当前电压
	 * */
	private void updataBatteryCharge(int plugged, int level) {
		int state = 0;
		if (plugged == BATTERY_NO_PLUGGED) {// 接口未连接
			state = BATTERY_CHARGE_STATE_NONE;
			mTxtUseStateNow.setText(R.string.txt_no_charging_battery);
			mImgBulbState.setImageResource(R.drawable.level_battery);
			mImgBulbState.setImageLevel(level);
			mTxtLevelNow.setText(level + getString(R.string.txt_percent));
			mImgBulbQuick.setImageResource(R.drawable.bulb_11);
			mImgBulbLoop.setImageResource(R.drawable.bulb_11);
			mImgBulbTrickle.setImageResource(R.drawable.bulb_11);
			mTxtTintQuick.setTextColor(getResources().getColor(R.color.black));
			mTxtStateQuick
					.setText(getString(R.string.txt_tint_waiting_battery));
			mTxtStateQuick.setTextColor(getResources().getColor(R.color.black));
			mTxtTintLoop.setTextColor(getResources().getColor(R.color.black));
			mTxtStateLoop.setText(getString(R.string.txt_tint_waiting_battery));
			mTxtStateLoop.setTextColor(getResources().getColor(R.color.black));
			mTxtTintTrickle
					.setTextColor(getResources().getColor(R.color.black));
			mTxtStateTrickle
					.setText(getString(R.string.txt_tint_waiting_battery));
			mTxtStateTrickle.setTextColor(getResources()
					.getColor(R.color.black));
		} else {// 接口连接
			mTxtUseStateNow.setText(R.string.txt_charging_now_battery);
			mImgBulbState.setImageResource(R.drawable.battery_charging);
			mTxtLevelNow.setText(level + getString(R.string.txt_percent));
			if (level <= BATTERY_CHARGE_QUICK) {
				state = BATTERY_CHARGE_STATE_QUICK;
			} else if (level <= BATTERY_CHARGE_LOOP) {
				state = BATTERY_CHARGE_STATE_LOOP;
			} else if (level < BATTERY_CHARGE_TRICKLE) {
				state = BATTERY_CHARGE_STATE_TRICKLE;
			} else {
				state = BATTERY_CHARGE_STATE_FULL;
			}
			if (mState != state) {
				mState = state;
			}
			upDataBulbsState(mState);
		}
	}

	/**
	 * 充电状态发生变化时更改灯泡及相关提示文本
	 * 
	 * @param state
	 *            电池当前充电状态
	 * @param level
	 *            电池电量
	 */
	private void upDataBulbsState(int state) {
		switch (state) {
		case BATTERY_CHARGE_STATE_QUICK:// 快速充电状态
			mImgBulbQuick.setImageResource(R.drawable.bulb_flash);
			((AnimationDrawable) (mImgBulbQuick.getDrawable())).start();
			mImgBulbLoop.setImageResource(R.drawable.bulb_11);
			mImgBulbTrickle.setImageResource(R.drawable.bulb_11);
			mTxtTintQuick.setTextColor(getResources().getColor(
					R.color.dark_green));
			mTxtStateQuick.setText(getString(R.string.txt_tint_doing_battery));
			mTxtStateQuick.setTextColor(getResources().getColor(
					R.color.dark_green));
			mTxtTintLoop.setTextColor(getResources().getColor(R.color.black));
			mTxtStateLoop.setText(getString(R.string.txt_tint_waiting_battery));
			mTxtStateLoop.setTextColor(getResources().getColor(R.color.black));
			mTxtTintTrickle
					.setTextColor(getResources().getColor(R.color.black));
			mTxtStateTrickle
					.setText(getString(R.string.txt_tint_waiting_battery));
			mTxtStateTrickle.setTextColor(getResources()
					.getColor(R.color.black));
			break;
		case BATTERY_CHARGE_STATE_LOOP:// 循环充电状态
			mImgBulbQuick.setImageResource(R.drawable.bulb_01);
			mImgBulbLoop.setImageResource(R.drawable.bulb_flash);
			((AnimationDrawable) (mImgBulbLoop.getDrawable())).start();
			mImgBulbTrickle.setImageResource(R.drawable.bulb_11);
			mTxtTintQuick.setTextColor(getResources().getColor(R.color.white));
			mTxtStateQuick.setText(getString(R.string.txt_tint_done_battery));
			mTxtStateQuick.setTextColor(getResources().getColor(R.color.white));
			mTxtTintLoop.setTextColor(getResources().getColor(
					R.color.dark_green));
			mTxtStateLoop.setText(getString(R.string.txt_tint_doing_battery));
			mTxtStateLoop.setTextColor(getResources().getColor(
					R.color.dark_green));
			mTxtTintTrickle
					.setTextColor(getResources().getColor(R.color.black));
			mTxtStateTrickle
					.setText(getString(R.string.txt_tint_waiting_battery));
			mTxtStateTrickle.setTextColor(getResources()
					.getColor(R.color.black));
			break;
		case BATTERY_CHARGE_STATE_TRICKLE:// 涓流充电状态
			mImgBulbQuick.setImageResource(R.drawable.bulb_01);
			mImgBulbLoop.setImageResource(R.drawable.bulb_01);
			mImgBulbTrickle.setImageResource(R.drawable.bulb_flash);
			((AnimationDrawable) (mImgBulbTrickle.getDrawable())).start();
			mTxtTintQuick.setTextColor(getResources().getColor(R.color.white));
			mTxtStateQuick.setText(getString(R.string.txt_tint_done_battery));
			mTxtStateQuick.setTextColor(getResources().getColor(R.color.white));
			mTxtTintLoop.setTextColor(getResources().getColor(R.color.white));
			mTxtStateLoop.setText(getString(R.string.txt_tint_done_battery));
			mTxtStateLoop.setTextColor(getResources().getColor(R.color.white));
			mTxtTintTrickle.setTextColor(getResources().getColor(
					R.color.dark_green));
			mTxtStateTrickle
					.setText(getString(R.string.txt_tint_doing_battery));
			mTxtStateTrickle.setTextColor(getResources().getColor(
					R.color.dark_green));
			break;
		case BATTERY_CHARGE_STATE_FULL:// 已充满状态
			mTxtUseStateNow.setText(R.string.txt_full_battery);
			mImgBulbState.setImageResource(R.drawable.battery_100);
			mImgBulbQuick.setImageResource(R.drawable.bulb_01);
			mImgBulbLoop.setImageResource(R.drawable.bulb_01);
			mImgBulbTrickle.setImageResource(R.drawable.bulb_01);
			mTxtTintQuick.setTextColor(getResources().getColor(R.color.white));
			mTxtStateQuick.setText(getString(R.string.txt_tint_done_battery));
			mTxtStateQuick.setTextColor(getResources().getColor(R.color.white));
			mTxtTintLoop.setTextColor(getResources().getColor(R.color.white));
			mTxtStateLoop.setText(getString(R.string.txt_tint_done_battery));
			mTxtStateLoop.setTextColor(getResources().getColor(R.color.white));
			mTxtTintTrickle
					.setTextColor(getResources().getColor(R.color.white));
			mTxtStateTrickle.setText(getString(R.string.txt_tint_done_battery));
			mTxtStateTrickle.setTextColor(getResources()
					.getColor(R.color.white));
			break;
		}
	}
}
