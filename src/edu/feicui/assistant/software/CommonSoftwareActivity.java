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
	/** 选项对话框ID */
	protected static final int DIALOG_ID_OPTION_SOFT = 0x10;
	/** 选项对话框列表条目-详细信息ID */
	protected static final int DIALOG_ID_OPTION_INFO_SOFT = 0x00;
	/** 选项对话框列表条目-打开应用ID */
	protected static final int DIALOG_ID_OPTION_OPEN_SOFT = 0x01;
	/** 选项对话框列表条目-程序卸载ID */
	protected static final int DIALOG_ID_OPTION_UNINSTAL_SOFT = 0x02;
	/** 应用Uri的协议：schema */
	protected static final String PACKAGE_SOFT = "package:";
	/** 应用的类型-系统应用/用户应用 */
	protected String mTxtAppType;
	/** 扫描到应用数目 */
	protected TextView mTxtAppNum;
	/** 查看方式切换按钮 */
	protected ToggleButton mTglSwitch;
	/** ListView对象 */
	protected ListView mLstView;
	/** GridView对象 */
	protected GridView mGrdView;
	/** GridView适配器 */
	protected GrdAdapter mGrdAdapter;
	/** ListView适配器 */
	protected LstAdapter mLstAdapter;
	/** 需要适配器处理的List */
	List<PackageInfo> mList;
	/** 应用类型-0：系统应用；1：用户应用 */
	protected int mType;
	/** 选中条目ID */
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

	/** 初始化适配器 */
	private void initAdapters() {
		mList = new ArrayList<PackageInfo>();
		switch (mType) {
		case SoftwareActivity.TYPE_SOFTWARE_SYSTEM:// 系统应用
			mList = SoftwareUtil.getInstance(this).getSystemAppInfos();
			mTxtAppType = getString(R.string.txt_system_software);
			break;
		case SoftwareActivity.TYPE_SOFTWARE_USER:// 用户应用
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
		case R.id.tgl_switch_software:// 切换按钮，切换用户界面和系统界面
			if (isChecked) {// 切换按钮选中，ListView显示，GridView隐藏（默认未选中）
				mLstView.setVisibility(View.VISIBLE);
				mGrdView.setVisibility(View.GONE);
			} else {// 切换按钮未选中，ListView隐藏，GridView显示
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
		case DIALOG_ID_OPTION_SOFT:// 软件选项对话框
			return createSoftDialogOption();
		default:
			return super.onCreateDialog(id);
		}
	}

	/** 创建软件选项对话框 */
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
		case DIALOG_ID_OPTION_INFO_SOFT:// 详细信息
			// APILevel 2.3 以上软件详情页action
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			// 设置data为 package uri ： "package:"+包名
			intent.setData(Uri.parse(PACKAGE_SOFT + info.packageName));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case DIALOG_ID_OPTION_OPEN_SOFT:// 打开应用
//			intent.setAction(Intent.ACTION_DELETE);
			 try{
				 intent = this.getPackageManager().getLaunchIntentForPackage(info.packageName);
			        startActivity(intent);
			    }catch(Exception e){
			        Toast.makeText(this, "没有安装", Toast.LENGTH_LONG).show();
			    }
			break;
		case DIALOG_ID_OPTION_UNINSTAL_SOFT:// 程序卸载
			intent.setAction(Intent.ACTION_DELETE);
			// 设置data为 package uri ： "package:"+包名
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