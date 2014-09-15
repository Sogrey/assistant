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
 * ϵͳ����
 * 
 * @author Sogrey
 * 
 */
public class SpeedupActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	/** The maximum number of entries to return in the list */
	protected static final int SERVICES_NUM_MAX = 1000;
	/** ����� ���� */
	protected ActivityManager mActivityMgr;
	/** ������ ���� */
	protected PackageManager mPackageMgr;
	/** ��Ҫ�����������List */
	protected List<ItemData> mList;
	/** ϵͳ����-ListView��������-Adapter */
	protected SpeedupListAdapter mLstAdapter;
	/** ϵͳ����-Ӧ������-txt */
	protected TextView mTxtAppNum;
	/** ϵͳ����-�ڴ�ʹ�����-txt */
	protected TextView mTxtMemoryuse;
	/** ϵͳ����-�ڴ�ʹ�ðٷֱ�-txt */
	protected TextView mTxtMemoryPercent;
	/** ϵͳ����-�ڴ�ʹ�ðٷֱ�-txt */
	protected ProgressBar mPgbMemoryUsed;
	/** ϵͳ����-һ�����ٰ�ť-btn */
	protected Button mBtnOneKey;
	/** ϵͳ����-չʾ�������е�app-lst */
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

	/** ��ȡ�������еĳ����б� */
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
		/* ��ȡ��������memoryInfo��Debug����MemoryInfo������ActivityManager���µ� */
		android.os.Debug.MemoryInfo memoryInfo = null;
		for (RunningAppProcessInfo info : appInfos) {
			try {
				pkgInfo = mPackageMgr.getPackageInfo(info.processName,
						PackageManager.GET_ACTIVITIES);
				appInfo = pkgInfo.applicationInfo;
				if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 // ȷ������ϵͳӦ��
						&& (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 // ȷ������ϵͳӦ������
				) {
					data = new ItemData();
					data.packageName = info.processName;
					data.icon = mPackageMgr.getApplicationIcon(appInfo);
					data.label = mPackageMgr.getApplicationLabel(appInfo)
							.toString();
					// ѭ���������з��񣬲���ȷ������process��ϵ
					for (RunningServiceInfo runningServiceInfo : runningServiceInfos) {
						/*
						 * �ڰ�׿�У�ͨ�������ÿ������ᴴ��һ���˻��� ������û��˻�ֻ���������ķ���Ȩ; uid����User
						 * ID��д����������ʾΨһ�û�; һ���û������Ӧһ���˻���һ���˻���Ӧһ���û�����
						 * info-������أ�runningServiceInfo-������أ�
						 * ������ǵ�ID��ͬ��ʾ����һ����ͬһ��Ӧ�ó���
						 */
						if (info.uid == runningServiceInfo.uid) {
							data.serviceNum++;
						}
					}
					/*
					 * getProcessMemoryInfo��Ӧ�ó���ռ���ڴ���Ϣ �����������ӦUID���飬
					 * ���أ������˻���ռ���ڴ���Ϣ
					 */
					memoryInfo = mActivityMgr
							.getProcessMemoryInfo(new int[] { info.uid })[0];
					// XXX û�л�ȡ������
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
			 * getMemoryInfo �Ĳ�����һ��ActivityManager.MemoryInfo����
			 * ���������ı�ActivityManager.MemoryInfo�������ֵ��
			 * ActivityManager.MemoryInfo��һ���������ͣ� �����ⲿ����outInfo��ֵҲ�ᷢ���ı�
			 */
			mActivityMgr.getMemoryInfo(outInfo);
			mTxtMemoryuse.setText(getString(
					R.string.txt_memoryuse_hardware,
					/* totalMem ������API LEVEL 16���� */
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

	/** ��ʼ�������� */
	private void initAdapters() {
		mLstAdapter = new SpeedupListAdapter(this);
		mLstAppRuning.setAdapter(mLstAdapter);
		mLstAppRuning.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_onekey_speedup_hardware:// һ�����ٰ�ť
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
		holder.chb_select.toggle();// �л�CheckBox��ѡ��״̬
		mList.get(position).checked = holder.chb_select.isChecked();
	}

	/** һ�������� */
	private void oneKey() {
		// TODO ���һ��������
		/* ��android�У����ڹ㲥���ƣ����Ծ�̬ע�ᣬ���̱�ɱ������ܻᱻ���� */
		for (ItemData data : mList) {
			if (data.checked && !getPackageName().equals(data.packageName)) {
				mActivityMgr.killBackgroundProcesses(data.packageName);
			}
		}
		initDatas();
		mLstAdapter.notifyDataSetChanged();
	}

	/**
	 * ϵͳ���ٽ���List������
	 * 
	 * @author Sogrey
	 * 
	 */
	public class SpeedupListAdapter extends BaseAdapter {
		/** �����Ķ��� */
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
					 * android.text.format.Formatterר��������ʽ����
					 * formatFileSize�����͸�ʽ���ļ��ȴ�С
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

	/** �����ȡ���Ŀؼ����´β���Ҫ��findViewById()�� */
	class SpeedupHolder {
		/** Ӧ��ͼ�� */
		ImageView icon;
		/** Ӧ�������ı� */
		TextView label;
		/** �ڴ�ʹ���ı� */
		TextView details;
		/** ����ѡ��� */
		CheckBox chb_select;
	}

	/** ����ÿ��item���� */
	class ItemData {
		/** ItemData-Ӧ��ͼ�� */
		Drawable icon;
		/** ItemData-Ӧ������ */
		String label;
		/** ItemData-������Ŀ */
		int serviceNum;
		/** ItemData-�ڴ�ʹ�� */
		int memory;
		/** ItemData-��ʼ������󱣴�ѡ���ѡ��״̬ */
		boolean checked = false;
		/** ItemData-Ӧ�ó������ */
		String packageName;
	}
}
