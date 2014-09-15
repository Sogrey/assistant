/**
 * 
 */
package edu.feicui.assistant.battery;

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
 * @author Sogrey
 * 
 */
public class BatteryActivity extends BaseTabActivity implements
		OnTabChangeListener {
	/** ��ǩҳ */
	protected TabHost mHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);
		initViews();
	}

	private void initViews() {
		mHost = getTabHost();// TabHost ���� ���б�ǩ��Ŀ��������
		TabSpec tabSpecOne = mHost.newTabSpec(Constants.MONITOR);// TabSpec����new
		// �����Ǳ�ǩ������Ϊ��ǩΪ��ʶ��
		LayoutInflater inflater = getLayoutInflater();
		View viewOne = inflater.inflate(R.layout.base_tab_label, null);
		((ImageView) viewOne.findViewById(R.id.img_indicator_icon_tab))
				.setImageResource(R.drawable.icon_indicator_tab_monitor_battery);
		((TextView) viewOne.findViewById(R.id.txt_indicator_label_tab))
				.setText(R.string.txttitle_tab_moniter_battery);
		Intent intentOne = new Intent();
		intentOne.setClass(this, MonitorActivity.class);
		tabSpecOne.setIndicator(viewOne);
		tabSpecOne.setContent(intentOne);// ���ñ�ǩ����
		mHost.addTab(tabSpecOne);// ��Ӹñ�ǩ

		TabSpec tabSpecTwo = mHost.newTabSpec(Constants.MAINTENANCE);// TabSpec����new
		// �����Ǳ�ǩ������Ϊ��ǩΪ��ʶ��
		View viewTwo = inflater.inflate(R.layout.base_tab_label, null);
		((ImageView) viewTwo.findViewById(R.id.img_indicator_icon_tab))
				.setImageResource(R.drawable.icon_indicator_tab_maintenance_battery);
		((TextView) viewTwo.findViewById(R.id.txt_indicator_label_tab))
				.setText(R.string.txttitle_tab_maintenance_battery);
		Intent intentTwo = new Intent(this, ChargeActivity.class);
		tabSpecTwo.setIndicator(viewTwo);
		tabSpecTwo.setContent(intentTwo);
		mHost.addTab(tabSpecTwo);

		mHost.setOnTabChangedListener(this);// tab�л��¼�����
	}

	/**
	 * @param tabId
	 *            Tab ID ,����newTabSpec�Ĳ���
	 * */
	@Override
	public void onTabChanged(String tabId) {
		Toast.makeText(this, tabId, Toast.LENGTH_SHORT).show();
	}

}
