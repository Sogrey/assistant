package edu.feicui.assistant.receiver;

import java.util.ArrayList;
import java.util.List;

import edu.feicui.assistant.baseinterface.Hearer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * �㲥����ת����
 * 
 * @author Administrator
 * 
 */
public abstract class BaseReceiver extends BroadcastReceiver {
	/** ����һ�������Ķ��󣬱�ʾ��ǰӦ�� */
	protected Context mContext;
	/** ����IntentFilter�������ͼ���� */
	protected IntentFilter mFilter;
	/** �㲥�Ƿ�ע�� */
	private boolean mRegisted;
	/** ���ڶ��� */
	protected List<Hearer> mHearers;
	/** �㲥���� */
	protected Intent mData;

	public BaseReceiver(Context context) {
		mContext = context.getApplicationContext();
		mFilter = getFilter();
		mHearers = new ArrayList<Hearer>();
	}

	/** ������� */
	public void addHearer(Hearer hearer) {
		/* �ж�Ҫ��ӵġ����ڡ���Ϊ�գ����������������ﻹû������ӽ��� */
		if (hearer != null && !mHearers.contains(hearer)) {
			mHearers.add(hearer);
			hearer.update(this, mData);
		}
		onHearerChanged();
	}

	/** ɾ������ */
	public void delHearer(Hearer hearer) {
		mHearers.remove(hearer);
		onHearerChanged();
	}

	/** ���ڷ����仯ʱ���� */
	protected void onHearerChanged() {
		if (mHearers.size() > 0 && !mRegisted) {
			register();
		}
		if (mHearers.size() == 0 && mRegisted) {
			unregister();
		}
	}

	/** ��ȡ�㲥���� */
	public Intent getData() {
		return mData;
	}

	/** ���󷽷�����ȡIntentFilter���� */
	protected abstract IntentFilter getFilter();

	/** �㲥ע����� */
	protected boolean isRegistered() {
		return mRegisted;
	}

	/** ע��㲥 */
	protected void register() {
		if (!mRegisted) {
			mContext.registerReceiver(this, mFilter);
			mRegisted = true;
		}
	}

	/** ע���㲥 */
	protected void unregister() {
		if (mRegisted) {
			mContext.unregisterReceiver(this);
			mRegisted = false;
		}
	}

	/**
	 * ������չ㲥
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 *      android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		for (Hearer hearer : mHearers) {
			hearer.update(this, intent);
		}
		mData = intent;
	}
}
