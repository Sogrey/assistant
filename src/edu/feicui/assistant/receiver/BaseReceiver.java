package edu.feicui.assistant.receiver;

import java.util.ArrayList;
import java.util.List;

import edu.feicui.assistant.baseinterface.Hearer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 广播接收转发器
 * 
 * @author Administrator
 * 
 */
public abstract class BaseReceiver extends BroadcastReceiver {
	/** 申明一个上下文对象，表示当前应用 */
	protected Context mContext;
	/** 申明IntentFilter类对象，意图过滤 */
	protected IntentFilter mFilter;
	/** 广播是否注册 */
	private boolean mRegisted;
	/** 听众队列 */
	protected List<Hearer> mHearers;
	/** 广播数据 */
	protected Intent mData;

	public BaseReceiver(Context context) {
		mContext = context.getApplicationContext();
		mFilter = getFilter();
		mHearers = new ArrayList<Hearer>();
	}

	/** 添加听众 */
	public void addHearer(Hearer hearer) {
		/* 判断要添加的“听众”不为空，并且在听众名单里还没有则添加进来 */
		if (hearer != null && !mHearers.contains(hearer)) {
			mHearers.add(hearer);
			hearer.update(this, mData);
		}
		onHearerChanged();
	}

	/** 删除听众 */
	public void delHearer(Hearer hearer) {
		mHearers.remove(hearer);
		onHearerChanged();
	}

	/** 听众发生变化时调用 */
	protected void onHearerChanged() {
		if (mHearers.size() > 0 && !mRegisted) {
			register();
		}
		if (mHearers.size() == 0 && mRegisted) {
			unregister();
		}
	}

	/** 获取广播数据 */
	public Intent getData() {
		return mData;
	}

	/** 抽象方法，获取IntentFilter对象 */
	protected abstract IntentFilter getFilter();

	/** 广播注册与否 */
	protected boolean isRegistered() {
		return mRegisted;
	}

	/** 注册广播 */
	protected void register() {
		if (!mRegisted) {
			mContext.registerReceiver(this, mFilter);
			mRegisted = true;
		}
	}

	/** 注销广播 */
	protected void unregister() {
		if (mRegisted) {
			mContext.unregisterReceiver(this);
			mRegisted = false;
		}
	}

	/**
	 * 处理接收广播
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
