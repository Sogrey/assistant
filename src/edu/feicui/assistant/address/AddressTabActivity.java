package edu.feicui.assistant.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseTabActivity;
import edu.feicui.assistant.util.Constants;

/**
 * 通讯录
 * 
 * @author Sogrey
 * 
 */
public class AddressTabActivity extends BaseTabActivity implements
		OnTabChangeListener {
	/** 标签页 */
	protected TabHost mHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hardware);
		initViews();
	}

	private void initViews() {
		mHost = getTabHost();// TabHost 宿主 所有标签项目依附于他

		LayoutInflater inflater = getLayoutInflater();
		TabSpec tabSpecOne = mHost.newTabSpec(Constants.PEOPLE);// TabSpec不能new
		// 参数是标签名字作为标签为以识别
		View viewOne = inflater.inflate(R.layout.base_tab_label, null);
		((ImageView) viewOne.findViewById(R.id.img_indicator_icon_tab))
				.setImageResource(R.drawable.icon_tab_user_software);
		((TextView) viewOne.findViewById(R.id.txt_indicator_label_tab))
				.setText(R.string.txt_tab_people_address);
		Intent intentOne = new Intent(this, AddressActivity.class);
		tabSpecOne.setIndicator(viewOne);
		tabSpecOne.setContent(intentOne);
		mHost.addTab(tabSpecOne);

		TabSpec tabSpecTwo = mHost.newTabSpec(Constants.CALLS);// TabSpec不能new
		// 参数是标签名字作为标签为以识别

		View viewTwo = inflater.inflate(R.layout.base_tab_label, null);
		((ImageView) viewTwo.findViewById(R.id.img_indicator_icon_tab))
				.setImageResource(R.drawable.btn_call_address);
		((TextView) viewTwo.findViewById(R.id.txt_indicator_label_tab))
				.setText(R.string.txt_tab_calls_address);
		Intent intentTwo = new Intent();
		intentTwo.setClass(this, CallsActivity.class);
		tabSpecTwo.setIndicator(viewTwo);
		tabSpecTwo.setContent(intentTwo);// 设置标签内容
		mHost.addTab(tabSpecTwo);// 添加该标签

		mHost.setOnTabChangedListener(this);// tab切换事件监听
	}

	/**
	 * @param tabId
	 *            Tab ID ,就是newTabSpec的参数
	 * */
	@Override
	public void onTabChanged(String tabId) {
		Toast.makeText(this, tabId, Toast.LENGTH_SHORT).show();
	}

}
