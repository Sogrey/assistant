/**
 * 
 */
package edu.feicui.assistant.software;

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
public class SoftwareActivity extends BaseTabActivity implements
		OnTabChangeListener {
	/** ��ǩҳ */
	protected TabHost mHost;
	/** ��תϵͳ�����ʶ */
	protected static final int TYPE_SOFTWARE_SYSTEM = 0x0;
	/** ��ת�û������ʶ */
	protected static final int TYPE_SOFTWARE_USER = 0x1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_software);
		initViews();
	}

	private void initViews() {
		mHost = getTabHost();// TabHost ���� ���б�ǩ��Ŀ��������
		LayoutInflater inflater = getLayoutInflater();
		/* TabSpec����new,�����Ǳ�ǩ������Ϊ��ǩΪ��ʶ�� */
		TabSpec tabSpecOne = mHost.newTabSpec(Constants.SYSTEM_SOFTWARE);

		View viewOne = inflater.inflate(R.layout.base_tab_label, null);
		((ImageView) viewOne.findViewById(R.id.img_indicator_icon_tab))
				.setImageResource(R.drawable.icon_tab_sysytem_software);
		((TextView) viewOne.findViewById(R.id.txt_indicator_label_tab))
				.setText(R.string.txttitle_tab_system_software);
		Intent intentOne = new Intent();
		intentOne.setClass(this, CommonSoftwareActivity.class);
		intentOne.putExtra(Constants.TYPE_SOFTWARE, TYPE_SOFTWARE_SYSTEM);
		tabSpecOne.setIndicator(viewOne);
		tabSpecOne.setContent(intentOne);// ���ñ�ǩ����
		mHost.addTab(tabSpecOne);// ��Ӹñ�ǩ

		TabSpec tabSpecTwo = mHost.newTabSpec(Constants.USER_SOFTWARE);
		View viewTwo = inflater.inflate(R.layout.base_tab_label, null);
		((ImageView) viewTwo.findViewById(R.id.img_indicator_icon_tab))
				.setImageResource(R.drawable.icon_tab_user_software);
		((TextView) viewTwo.findViewById(R.id.txt_indicator_label_tab))
				.setText(R.string.txttitle_tab_user_software);
		Intent intentTwo = new Intent(this, CommonSoftwareActivity.class);
		intentTwo.putExtra(Constants.TYPE_SOFTWARE, TYPE_SOFTWARE_USER);
		tabSpecTwo.setIndicator(viewTwo);
		tabSpecTwo.setContent(intentTwo);
		mHost.addTab(tabSpecTwo);
		mHost.setOnTabChangedListener(this);// tab�л��¼�����
	}

	@Override
	public void onTabChanged(String tabId) {
		Toast.makeText(this, tabId, Toast.LENGTH_SHORT).show();
	}
}
