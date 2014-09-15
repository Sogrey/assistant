/**
 * 
 */
package edu.feicui.assistant.base;

import android.app.TabActivity;
import android.os.Bundle;
import edu.feicui.assistant.util.LogWrapper;

/**
 * @author Sogrey
 * 
 */
public class BaseTabActivity extends TabActivity {

	/* 获取当前类的名称 */
	protected final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogWrapper.d(TAG, "Tab onCreate");
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogWrapper.d(TAG, "Tab onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogWrapper.d(TAG, "Tab onResume");
	}

	@Override
	protected void onPause() {
		LogWrapper.d(TAG, "Tab onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		LogWrapper.d(TAG, "Tab onStop");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		LogWrapper.d(TAG, "Tab onRestart");
	}

	@Override
	protected void onDestroy() {
		LogWrapper.d(TAG, "Tab onDestroy");
		super.onDestroy();
	}
}
