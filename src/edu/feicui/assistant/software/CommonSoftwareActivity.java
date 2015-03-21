package edu.feicui.assistant.software;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.feicui.assistant.R;
import edu.feicui.assistant.adapter.GrdAdapter;
import edu.feicui.assistant.adapter.LstAdapter;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.util.Constants;
import edu.feicui.assistant.util.SoftwareUtil;

/**
 * @author Sogrey
 * 
 */
public class CommonSoftwareActivity extends BaseActivity implements
		OnCheckedChangeListener, OnItemClickListener, OnClickListener {
	/** ѡ��Ի���ID */
	protected static final int DIALOG_ID_OPTION_SOFT = 0x10;
	/** ѡ��Ի����б���Ŀ-��ϸ��ϢID */
	protected static final int DIALOG_ID_OPTION_INFO_SOFT = 0x00;
	/** ѡ��Ի����б���Ŀ-��Ӧ��ID */
	protected static final int DIALOG_ID_OPTION_OPEN_SOFT = 0x01;
	/** ѡ��Ի����б���Ŀ-����ж��ID */
	protected static final int DIALOG_ID_OPTION_UNINSTAL_SOFT = 0x02;
	/** Ӧ��Uri��Э�飺schema */
	protected static final String PACKAGE_SOFT = "package:";
	/** Ӧ�õ�����-ϵͳӦ��/�û�Ӧ�� */
	protected String mTxtAppType;
	/** ɨ�赽Ӧ����Ŀ */
	protected TextView mTxtAppNum;
	/** �鿴��ʽ�л���ť */
	protected ToggleButton mTglSwitch;
	/** ListView���� */
	protected ListView mLstView;
	/** GridView���� */
	protected GridView mGrdView;
	/** GridView������ */
	protected GrdAdapter mGrdAdapter;
	/** ListView������ */
	protected LstAdapter mLstAdapter;
	/** ��Ҫ�����������List */
	List<PackageInfo> mList;
	/** Ӧ������-0��ϵͳӦ�ã�1���û�Ӧ�� */
	protected int mType;
	/** ѡ����ĿID */
	protected int mSelectedPos = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_software);
		Intent inetnt = getIntent();
		mType = inetnt.getIntExtra(Constants.TYPE_SOFTWARE, 0);
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SoftwareUtil.getInstance(this).refreshPackage();
		initAdapters();
	}

	private void initViews() {
		mTxtAppNum = (TextView) findViewById(R.id.txt_softwarenumber_software);
		mTglSwitch = (ToggleButton) findViewById(R.id.tgl_switch_software);
		mLstView = (ListView) findViewById(R.id.lst_view_software);
		mGrdView = (GridView) findViewById(R.id.grd_view_software);
		mTglSwitch.setOnCheckedChangeListener(this);
		mLstView.setOnItemClickListener(this);
		mGrdView.setOnItemClickListener(this);
	}

	/** ��ʼ�������� */
	private void initAdapters() {
		mList = new ArrayList<PackageInfo>();
		switch (mType) {
		case SoftwareActivity.TYPE_SOFTWARE_SYSTEM:// ϵͳӦ��
			mList = SoftwareUtil.getInstance(this).getSystemAppInfos();
			mTxtAppType = getString(R.string.txt_system_software);
			break;
		case SoftwareActivity.TYPE_SOFTWARE_USER:// �û�Ӧ��
			mList = SoftwareUtil.getInstance(this).getUserAppInfos();
			mTxtAppType = getString(R.string.txt_user_software);
			break;
		}
		mGrdAdapter = new GrdAdapter(this);
		mGrdAdapter.setSets(mList);
		mGrdView.setAdapter(mGrdAdapter);
		mLstAdapter = new LstAdapter(this);
		mLstAdapter.setSets(mList);
		mLstView.setAdapter(mLstAdapter);
		String fmt = getString(R.string.txt_app_type_num_software, mTxtAppType,
				mList.size());
		mTxtAppNum.setText(fmt);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		switch (id) {
		case R.id.tgl_switch_software:// �л���ť���л��û������ϵͳ����
			if (isChecked) {// �л���ťѡ�У�ListView��ʾ��GridView���أ�Ĭ��δѡ�У�
				mLstView.setVisibility(View.VISIBLE);
				mGrdView.setVisibility(View.GONE);
			} else {// �л���ťδѡ�У�ListView���أ�GridView��ʾ
				mLstView.setVisibility(View.GONE);
				mGrdView.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mSelectedPos = position;
		showDialog(DIALOG_ID_OPTION_SOFT);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ID_OPTION_SOFT:// ���ѡ��Ի���
			return createSoftDialogOption();
		default:
			return super.onCreateDialog(id);
		}
	}

	/** �������ѡ��Ի��� */
	private Dialog createSoftDialogOption() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.txttitle_dialog_option_software);
		builder.setItems(R.array.stringarray_item_menu_soft, this);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		PackageInfo info = mList.get(mSelectedPos);
		Intent intent = new Intent();
		switch (which) {
		case DIALOG_ID_OPTION_INFO_SOFT:// ��ϸ��Ϣ
			// APILevel 2.3 �����������ҳaction
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			// ����dataΪ package uri �� "package:"+����
			intent.setData(Uri.parse(PACKAGE_SOFT + info.packageName));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case DIALOG_ID_OPTION_OPEN_SOFT:// ��Ӧ��
//			intent.setAction(Intent.ACTION_DELETE);
			 try{
				 intent = this.getPackageManager().getLaunchIntentForPackage(info.packageName);
			        startActivity(intent);
			    }catch(Exception e){
			        Toast.makeText(this, "û�а�װ", Toast.LENGTH_LONG).show();
			    }
			break;
		case DIALOG_ID_OPTION_UNINSTAL_SOFT:// ����ж��
			intent.setAction(Intent.ACTION_DELETE);
			// ����dataΪ package uri �� "package:"+����
			intent.setData(Uri.parse(PACKAGE_SOFT + info.packageName));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}