package edu.feicui.assistant.hardware;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;

/**
 * 系统加速
 * 
 * @author Sogrey
 * 
 */
public class SpeedupActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	/** The maximum number of entries to return in the list */
	protected static final int SERVICES_NUM_MAX = 1000;
	/** 活动管理 对象 */
	protected ActivityManager mActivityMgr;
	/** 包管理 对象 */
	protected PackageManager mPackageMgr;
	/** 需要适配器处理的List */
	protected List<ItemData> mList;
	/** 系统加速-ListView的适配器-Adapter */
	protected SpeedupListAdapter mLstAdapter;
	/** 系统加速-应用名称-txt */
	protected TextView mTxtAppNum;
	/** 系统加速-内存使用情况-txt */
	protected TextView mTxtMemoryuse;
	/** 系统加速-内存使用百分比-txt */
	protected TextView mTxtMemoryPercent;
	/** 系统加速-内存使用百分比-txt */
	protected ProgressBar mPgbMemoryUsed;
	/** 系统加速-一键加速按钮-btn */
	protected Button mBtnOneKey;
	/** 系统加速-展示正在运行的app-lst */
	protected ListView mLstAppRuning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speedup_hardware);
		initViews();
		initDatas();
		initAdapters();
	}

	private void initViews() {
		mTxtAppNum = (TextView) findViewById(R.id.txt_appnum_hardware);
		mTxtMemoryuse = (TextView) findViewById(R.id.txt_memoryuse_hardware);
		mTxtMemoryPercent = (TextView) findViewById(R.id.txt_memoryused_percent_hardware);
		mPgbMemoryUsed = (ProgressBar) findViewById(R.id.pgb_memoryused_percent_hardware);
		mBtnOneKey = (Button) findViewById(R.id.btn_onekey_speedup_hardware);
		mBtnOneKey.setOnClickListener(this);
		mLstAppRuning = (ListView) findViewById(R.id.lst_softlist_hardware);

	}

	/** 获取正在运行的程序列表 */
	private void initDatas() {
		mActivityMgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mPackageMgr = getPackageManager();
		mList = new ArrayList<ItemData>();
		List<ActivityManager.RunningAppProcessInfo> appInfos = mActivityMgr
				.getRunningAppProcesses();
		List<RunningServiceInfo> runningServiceInfos = mActivityMgr
				.getRunningServices(SERVICES_NUM_MAX);

		ItemData data = null;
		PackageInfo pkgInfo = null;
		ApplicationInfo appInfo = null;
		/* 获取单个程序memoryInfo用Debug包下MemoryInfo，不是ActivityManager包下的 */
		android.os.Debug.MemoryInfo memoryInfo = null;
		for (RunningAppProcessInfo info : appInfos) {
			try {
				pkgInfo = mPackageMgr.getPackageInfo(info.processName,
						PackageManager.GET_ACTIVITIES);
				appInfo = pkgInfo.applicationInfo;
				if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 // 确定不是系统应用
						&& (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 // 确定不是系统应用升级
				) {
					data = new ItemData();
					data.packageName = info.processName;
					data.icon = mPackageMgr.getApplicationIcon(appInfo);
					data.label = mPackageMgr.getApplicationLabel(appInfo)
							.toString();
					// 循环遍历所有服务，才能确定它和process关系
					for (RunningServiceInfo runningServiceInfo : runningServiceInfos) {
						/*
						 * 在安卓中，通常会针对每个程序会创建一个账户， 而这个用户账户只有这个程序的访问权; uid就是User
						 * ID缩写，他用来表示唯一用户; 一个用户程序对应一个账户，一个账户对应一个用户程序；
						 * info-进程相关，runningServiceInfo-服务相关；
						 * 如果他们的ID相同表示他们一定是同一个应用程序
						 */
						if (info.uid == runningServiceInfo.uid) {
							data.serviceNum++;
						}
					}
					/*
					 * getProcessMemoryInfo是应用程序占用内存信息 参数：程序对应UID数组，
					 * 返回：各个账户所占用内存信息
					 */
					memoryInfo = mActivityMgr
							.getProcessMemoryInfo(new int[] { info.uid })[0];
					// XXX 没有获取到数据
					data.memory = memoryInfo.otherPss;
					mList.add(data);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			mTxtAppNum.setText(getString(R.string.txt_appnum_hardware,
					mList.size()));
			ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
			/*
			 * getMemoryInfo 的参数是一个ActivityManager.MemoryInfo对象，
			 * 这个方法会改变ActivityManager.MemoryInfo里的属性值，
			 * ActivityManager.MemoryInfo是一个引用类型， 所以外部对象outInfo的值也会发生改变
			 */
			mActivityMgr.getMemoryInfo(outInfo);
			mTxtMemoryuse.setText(getString(
					R.string.txt_memoryuse_hardware,
					/* totalMem 适用于API LEVEL 16以上 */
					Formatter.formatFileSize(this, outInfo.totalMem
							- outInfo.availMem),
					Formatter.formatFileSize(this, outInfo.availMem)));
			mTxtMemoryPercent.setText(getString(
					R.string.txt_memoryused_percent_hardware, 100
							* (outInfo.totalMem - outInfo.availMem)
							/ outInfo.totalMem));
			mPgbMemoryUsed.setMax((int) outInfo.totalMem);
			mPgbMemoryUsed
					.setProgress((int) (outInfo.totalMem - outInfo.availMem));
		}
	}

	/** 初始化适配器 */
	private void initAdapters() {
		mLstAdapter = new SpeedupListAdapter(this);
		mLstAppRuning.setAdapter(mLstAdapter);
		mLstAppRuning.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_onekey_speedup_hardware:// 一键加速按钮
			oneKey();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SpeedupHolder holder = (SpeedupHolder) view.getTag();
		holder.chb_select.toggle();// 切换CheckBox的选中状态
		mList.get(position).checked = holder.chb_select.isChecked();
	}

	/** 一键清理功能 */
	private void oneKey() {
		// TODO 添加一键清理功能
		/* 在android中，由于广播机制，可以静态注册，进程被杀死后可能会被唤醒 */
		for (ItemData data : mList) {
			if (data.checked && !getPackageName().equals(data.packageName)) {
				mActivityMgr.killBackgroundProcesses(data.packageName);
			}
		}
		initDatas();
		mLstAdapter.notifyDataSetChanged();
	}

	/**
	 * 系统加速界面List适配器
	 * 
	 * @author Sogrey
	 * 
	 */
	public class SpeedupListAdapter extends BaseAdapter {
		/** 上下文对象 */
		protected Context mContext;

		public SpeedupListAdapter(Context context) {
			mContext = context;
		}

		/*
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return mList == null ? 0 : mList.size();
		}

		/*
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		/*
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return mList.get(position).hashCode();
		}

		/*
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			SpeedupHolder holder;
			if (view == null) {
				holder = new SpeedupHolder();
				view = getLayoutInflater()
						.inflate(
								edu.feicui.assistant.R.layout.item_listview_speedup_hardware,
								null);
				holder.icon = (ImageView) view
						.findViewById(R.id.icon_app_hardware);
				holder.label = (TextView) view
						.findViewById(R.id.txt_appname_hardware);
				holder.details = (TextView) view
						.findViewById(R.id.txt_details_hardware);
				holder.chb_select = (CheckBox) view
						.findViewById(R.id.chb_check_hardware);
				view.setTag(holder);
			} else {
				holder = (SpeedupHolder) view.getTag();
			}
			ItemData data = mList.get(position);
			holder.icon.setImageDrawable(data.icon);
			holder.label.setText(data.label);
			holder.details.setText(R.string.txt_details_hardware);
			String details = getString(R.string.txt_details_hardware,
					data.serviceNum,
					/*
					 * android.text.format.Formatter专门用来格式化，
					 * formatFileSize用来和格式化文件等大小
					 */
					Formatter.formatFileSize(SpeedupActivity.this, data.memory));
			holder.details.setText(details);
			if (data.packageName.equals(SpeedupActivity.this.getPackageName())) {
				holder.chb_select.setVisibility(View.INVISIBLE);
			} else {
				holder.chb_select.setVisibility(View.VISIBLE);
				holder.chb_select.setChecked(data.checked);
			}

			return view;
		}
	}

	/** 保存获取到的控件，下次不需要再findViewById()了 */
	class SpeedupHolder {
		/** 应用图标 */
		ImageView icon;
		/** 应用名称文本 */
		TextView label;
		/** 内存使用文本 */
		TextView details;
		/** 清理选择框 */
		CheckBox chb_select;
	}

	/** 保存每条item数据 */
	class ItemData {
		/** ItemData-应用图标 */
		Drawable icon;
		/** ItemData-应用名称 */
		String label;
		/** ItemData-服务数目 */
		int serviceNum;
		/** ItemData-内存使用 */
		int memory;
		/** ItemData-初始清理而后保存选择框选择状态 */
		boolean checked = false;
		/** ItemData-应用程序包名 */
		String packageName;
	}
}
