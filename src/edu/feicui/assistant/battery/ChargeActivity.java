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
 * ���ά��
 * 
 * @author Sogrey
 * 
 */
public class ChargeActivity extends BaseActivity implements Hearer {
	/** ��ǰ���ʹ��״̬ */
	protected TextView mTxtUseStateNow;
	/** ��ǰ����״̬ */
	protected TextView mTxtLevelNow;
	/** ���״̬ */
	protected ImageView mImgBulbState;
	/** ���ٳ��-���� */
	protected ImageView mImgBulbQuick;
	/** ѭ�����-���� */
	protected ImageView mImgBulbLoop;
	/** ������-���� */
	protected ImageView mImgBulbTrickle;
	/** ���ٳ��-tint */
	protected TextView mTxtTintQuick;
	/** ���ٳ��-״̬ */
	protected TextView mTxtStateQuick;
	/** ѭ�����-tint */
	protected TextView mTxtTintLoop;
	/** ѭ�����-״̬ */
	protected TextView mTxtStateLoop;
	/** ������-tint */
	protected TextView mTxtTintTrickle;
	/** ������-״̬ */
	protected TextView mTxtStateTrickle;
	/** �ӿ�δ״̬ */
	private static final int BATTERY_NO_PLUGGED = 0;
	/** δ���״̬ */
	private static final int BATTERY_CHARGE_STATE_NONE = 0x01;
	/** ���ٳ��״̬ */
	private static final int BATTERY_CHARGE_STATE_QUICK = 0x02;
	/** ѭ�����״̬ */
	private static final int BATTERY_CHARGE_STATE_LOOP = 0x03;
	/** ������״̬ */
	private static final int BATTERY_CHARGE_STATE_TRICKLE = 0x04;
	/** �ѳ���״̬ */
	private static final int BATTERY_CHARGE_STATE_FULL = 0x05;
	/** ���ٳ���ѹ�ż�ֵ���� */
	private static final int BATTERY_CHARGE_QUICK = 80;
	/** ѭ������ѹ�ż�ֵ���� */
	private static final int BATTERY_CHARGE_LOOP = 95;
	/** �������ѹ�ż�ֵ���� */
	private static final int BATTERY_CHARGE_TRICKLE = 100;
	/** ��س��״̬ */
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

	/** ��ʼ��View�齨 */
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

	/** ��ʼ������״̬����ʾ�ı� */
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
	 * ���ݽӿ�״̬����ǰ����ˢ�½�����ʾ
	 * 
	 * @param plugged
	 *            �ӿ�״̬
	 * @param level
	 *            ��ǰ��ѹ
	 * */
	private void updataBatteryCharge(int plugged, int level) {
		int state = 0;
		if (plugged == BATTERY_NO_PLUGGED) {// �ӿ�δ����
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
		} else {// �ӿ�����
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
	 * ���״̬�����仯ʱ���ĵ��ݼ������ʾ�ı�
	 * 
	 * @param state
	 *            ��ص�ǰ���״̬
	 * @param level
	 *            ��ص���
	 */
	private void upDataBulbsState(int state) {
		switch (state) {
		case BATTERY_CHARGE_STATE_QUICK:// ���ٳ��״̬
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
		case BATTERY_CHARGE_STATE_LOOP:// ѭ�����״̬
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
		case BATTERY_CHARGE_STATE_TRICKLE:// ������״̬
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
		case BATTERY_CHARGE_STATE_FULL:// �ѳ���״̬
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
