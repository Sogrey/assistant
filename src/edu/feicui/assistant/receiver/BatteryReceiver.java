/**
 * 
 */
package edu.feicui.assistant.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * µç³Ø¹ã²¥¼àÌýÆ÷
 * 
 * @author Sogrey
 * 
 */
public class BatteryReceiver extends BaseReceiver {

	private static BatteryReceiver mInstance;

	public static BatteryReceiver getInstance(Context context) {
		if (mInstance == null) {
			synchronized (BatteryReceiver.class) {
				if (mInstance == null) {
					mInstance = new BatteryReceiver(context);
				}
			}
		}
		return mInstance;
	}

	private BatteryReceiver(Context context) {
		super(context);
	}

	@Override
	protected IntentFilter getFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		return filter;
	}
}
