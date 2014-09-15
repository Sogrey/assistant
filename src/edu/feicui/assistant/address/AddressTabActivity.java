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
 * ͨѶ¼
 * 
 * @author Sogrey
 * 
 */
public class AddressTabActivity extends BaseTabActivity implements
		OnTabChangeListener {
	/** ��ǩҳ */
	protected TabHost mHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hardware);
		initViews();
	}

	private void initViews() {
		mHost = getTabHost();// TabHost ���� ���б�ǩ��Ŀ��������

		LayoutInflater inflater = getLayoutInflater();
		TabSpec tabSpecOne = mHost.newTabSpec(Constants.PEOPLE);// TabSpec����new
		// �����Ǳ�ǩ������Ϊ��ǩΪ��ʶ��
		View viewOne = inflater.inflate(R.layout.base_tab_label, null);
		((ImageView) viewOne.findViewById(R.id.img_indicator_icon_tab))
				.setImageResource(R.drawable.icon_tab_user_software);
		((TextView) viewOne.findViewById(R.id.txt_indicator_label_tab))
				.setText(R.string.txt_tab_people_address);
		Intent intentOne = new Intent(this, AddressActivity.class);
		tabSpecOne.setIndicator(viewOne);
		tabSpecOne.setContent(intentOne);
		mHost.addTab(tabSpecOne);

		TabSpec tabSpecTwo = mHost.newTabSpec(Constants.CALLS);// TabSpec����new
		// �����Ǳ�ǩ������Ϊ��ǩΪ��ʶ��

		View viewTwo = inflater.inflate(R.layout.base_tab_label, null);
		((ImageView) viewTwo.findViewById(R.id.img_indicator_icon_tab))
				.setImageResource(R.drawable.btn_call_address);
		((TextView) viewTwo.findViewById(R.id.txt_indicator_label_tab))
				.setText(R.string.txt_tab_calls_address);
		Intent intentTwo = new Intent();
		intentTwo.setClass(this, CallsActivity.class);
		tabSpecTwo.setIndicator(viewTwo);
		tabSpecTwo.setContent(intentTwo);// ���ñ�ǩ����
		mHost.addTab(tabSpecTwo);// ��Ӹñ�ǩ

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
