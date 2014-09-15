package edu.feicui.assistant.hardware;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.util.FileUtil;
import edu.feicui.assistant.util.ShellExcuter;

/**
 * 系統檢測模塊
 * 
 * @author Sogrey
 * 
 */
public class SystemTestActivity extends BaseActivity implements
		OnGroupClickListener, OnChildClickListener {
	/** CPU 信息文件路径 */
	protected static final String PATH_CPUINFO = "/proc/cpuinfo";
	/** 標題集合（一维） */
	protected String[] mGroup;
	/** 数据集合（二维） */
	protected List<List<String>> mDataLists;
	/** 可展开的ListView对象 */
	protected ExpandableListView mElsSystemTest;
	/** 可展开的ListView适配器对象 */
	protected ElsAdapter mElsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_systemtest_hardware);
		initViews();
		initDatas();
		initAdapters();
	}

	private void initViews() {
		mElsSystemTest = (ExpandableListView) findViewById(R.id.els_systemtest_hardware);
		mElsSystemTest.setOnGroupClickListener(this);
		mElsSystemTest.setOnChildClickListener(this);
	}

	/** 初始化数据 */
	private void initDatas() {
		mGroup = getResources().getStringArray(
				R.array.stringarray_item_groups_hard);
		mDataLists = new ArrayList<List<String>>();
		mDataLists.add(gleanBase());
		mDataLists.add(gleanCPU());
		mDataLists.add(gleanMemory());
		mDataLists.add(gleanDefinition());
		mDataLists.add(gleanPixel());
		mDataLists.add(gleanWIFI());
//		outputInFile(mDataLists);
	}

	/** 将所有信息输出到文件 */
	private void outputInFile(List<List<String>> mDataLists) {
		String infoString = "";
		for (List<String> list : mDataLists) {
			for (String data : list) {
				infoString += data + "\n";
			}
		}
		FileUtil.write(infoString);
	}

	/** 获取设备基本信息 */
	private List<String> gleanBase() {
		List<String> list = new ArrayList<String>();
		/*-----获取手机品牌-----*/
		list.add(getString(R.string.txt_brand_hardware) + Build.BRAND);
		/*-----获取产品名称-----*/
		list.add(getString(R.string.txt_product_hardware) + Build.PRODUCT);
		/*-----获取对外名称-----*/
		list.add(getString(R.string.txt_model_hardware) + Build.MODEL);
		/*-----获取设备型号-----*/
		list.add(getString(R.string.txt_devtype_hardware) + Build.DEVICE);
		/*-----获取系统版本-----*/
		list.add(getString(R.string.txt_sysver_hardware)
				+ Build.VERSION.RELEASE);
		TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		/*-----获取手机串号-----*/
		list.add(getString(R.string.txt_isbn_hardware)
				+ telephonyMgr.getDeviceId());
		/*-----获取手机号-----*/
		list.add(getString(R.string.txt_phonenum_hardware)
				+ telephonyMgr.getLine1Number());
		String op = telephonyMgr.getSimOperator();
		String operator;
		if ("46000".equals(op) || "46002".equals(op)) {
			operator = getString(R.string.txt_mobile_hardware);
		} else if ("46001".equals(op)) {
			operator = getString(R.string.txt_woo_hardware);
		} else if ("46003".equals(op)) {
			operator = getString(R.string.txt_telecommunication_hardware);
		} else {
			operator = getString(R.string.txt_unknown);
		}
		/*-----获取运营商-----*/
		list.add(getString(R.string.txt_service_hardware) + operator);
		/*-----获取基带版本-----*/
		list.add(getString(R.string.txt_baseband_hardware)
				+ SystemProperties.get("gsm.version.baseband"));
		return list;
	}

	/** 获取CPU信息 */
	private List<String> gleanCPU() {
		List<String> list = new ArrayList<String>();
		/*-----获取CPU型号-----*/
		{
			String cpuinfo = FileUtil.read("/proc/cpuinfo");
			Pattern pattern = Pattern.compile("^Processor\\s*:\\s*(.+)$",
					Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(cpuinfo);
			while (matcher.find()) {
				list.add(getString(R.string.txt_cputype_hardware,
						matcher.group(1)));
			}
		}
		String cpuDevFolder = "/sys/devices/system/cpu/";
		/*-----获取CPU核心数-----*/
		{
			File cpuDevFile = new File(cpuDevFolder);
			File[] f = cpuDevFile.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					return Pattern.matches("^cpu\\d$", filename);
				}
			});
			list.add(getString(R.string.txt_cpunum_hardware, f.length));
		}
		/*-----获取CPU最高频率-----*/
		list.add(getString(R.string.txt_maxfreq_hardware,
				FileUtil.read(cpuDevFolder + "cpu0/cpufreq/cpuinfo_max_freq")));
		/*-----获取CPU最低频率-----*/
		list.add(getString(R.string.txt_minfreq_hardware,
				FileUtil.read(cpuDevFolder + "cpu0/cpufreq/cpuinfo_min_freq")));
		/*-----获取CPU当前频率-----*/
//		list.add(getString(R.string.txt_curfreq_hardware,
//				FileUtil.read(cpuDevFolder + "cpu0/cpufreq/cpuinfo_cur_freq")));
		String cur = ShellExcuter.exec("cat", cpuDevFolder
				+ "cpu0/cpufreq/cpuinfo_cur_freq");
		list.add(getString(R.string.txt_curfreq_hardware, cur));
		// XXX 可能获取不到数据
		// list.add(getString(R.string.txt_curfreq_hardware,
		// FileUtil.read(cpuDevFolder + "/cpu0/cpufreq/cpuinfo_cur_freq")));

		return list;
	}

	/** 获取设备内存信息 */
	@SuppressWarnings("deprecation")
	private List<String> gleanMemory() {
		List<String> list = new ArrayList<String>();
		Pattern pattern;
		Matcher matcher;
		/*-----获取运行内存总量-----*/
		String meminfo = FileUtil.read("/proc/meminfo");
		pattern = Pattern.compile("^MemTotal\\s*:\\s*(\\d+)\\s*kB$",
				Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(meminfo);
		if (matcher.find()) {
			String memTotal = matcher.group(1);
			long memTotalLong = 0;
			try {
				memTotalLong = Long.parseLong(memTotal);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			list.add(getString(
					R.string.txt_memruntotal_hardware,
					Formatter.formatFileSize(this, memTotalLong
							* FileUtil.BUFFERSIZE)));
		}
		/*-----获取手机可用内存-----*/
		ActivityManager activityMgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		activityMgr.getMemoryInfo(outInfo);
		list.add(getString(R.string.txt_memavaitotal_hardware,
				Formatter.formatFileSize(this, outInfo.availMem)));
		/*-----获取sd卡内存总量-----*/
		File sdcard = Environment.getExternalStorageDirectory();
		// sdcard.getTotalSpace() API 9以上
		// list.add(getString(R.string.txt_memavaitotal_hardware,
		// Formatter.formatFileSize(this, sdcard.getTotalSpace())));
		StatFs sFs = new StatFs(sdcard.getAbsolutePath());
		long total = 1L * sFs.getBlockSize() * sFs.getBlockCount();
		list.add(getString(R.string.txt_memsdtotal_hardware,
				Formatter.formatFileSize(this, total)));
		return list;
	}

	/** 获取设备分辨率 */
	private List<String> gleanDefinition() {
		List<String> list = new ArrayList<String>();
		/*-----获取手机屏幕分辨率-----*/
		DisplayMetrics outMetrics = new DisplayMetrics();
		/*
		 * 获取窗口管理，通过窗口管理得到屏幕信息 获取默认屏幕信息 获取屏幕尺寸信息，通过参数反馈结果
		 */
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		list.add(getString(R.string.txt_split_screen_hardware)
				+ outMetrics.widthPixels
				+ getString(R.string.txt_pix_sizemul_hardware)
				+ outMetrics.heightPixels);
		Camera camera = null;
		/* 判断有无摄像头设备 */
		if (Camera.getNumberOfCameras() > 0) {
			camera = Camera.open();
			Parameters parameters = camera.getParameters();
			/*-----获取照片最大尺度-----*/
			List<Size> pictureSizes = new ArrayList<Size>();
			pictureSizes = parameters.getSupportedPictureSizes();
			int pictureWidthMax = 0;
			int pictureHeightMax = 0;
			int pictureSizeMax = 0;
			for (Size size : pictureSizes) {
				int pictureWidth = size.width;
				int pictureHeight = size.height;
				int pictureSize = pictureWidth * pictureHeight;
				if (pictureSize > pictureSizeMax) {
					pictureSizeMax = pictureSize;
					pictureWidthMax = pictureWidth;
					pictureHeightMax = pictureHeight;
				}
			}
			camera.release();
			list.add(getString(R.string.txt_pic_maxsize_hardware)
					+ pictureWidthMax
					+ getString(R.string.txt_pix_sizemul_hardware)
					+ pictureHeightMax);
			/*-----获取闪光灯-----*/
			List<String> flashModes = new ArrayList<String>();
			flashModes = parameters.getSupportedFlashModes();
			if (flashModes.size() > 1) {
				list.add(getString(R.string.txt_flash_hardware)
						+ getString(R.string.txt_flash_yes_hardware));
			} else {
				list.add(getString(R.string.txt_flash_hardware)
						+ getString(R.string.txt_flash_no_hardware));
			}
		} else {
			list.add(getString(R.string.txt_pic_maxsize_hardware)
					+ getString(R.string.txt_no_camera_hardware));
			list.add(getString(R.string.txt_flash_hardware)
					+ getString(R.string.txt_no_camera_hardware));
		}
		return list;
	}

	/** 获取设备像素 */
	private List<String> gleanPixel() {
		List<String> list = new ArrayList<String>();
		/*-----获取手机像素-----*/
		Camera camera = null;
		/* 判断有无摄像头设备 */
		if (Camera.getNumberOfCameras() > 0) {
			camera = Camera.open();
			Parameters parameters = camera.getParameters();
			List<Size> pictureSizes = new ArrayList<Size>();
			pictureSizes = parameters.getSupportedPictureSizes();
			int pictureSizeMax = 0;
			for (Size size : pictureSizes) {
				int pictureSize = size.width * size.height;
				if (pictureSize > pictureSizeMax) {
					pictureSizeMax = pictureSize;
				}
			}
			camera.release();
			list.add(getString(R.string.txt_def_hardware) + pictureSizeMax
					/ 10000 + "w");
		}
		/*-----获取像素密度-----*/
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		list.add(getString(R.string.txt_dot_density_hardware)
				+ outMetrics.densityDpi);
		/*-----获取多点触控-----*/
		// 获取MotionEvent中支持所有方法
		Method[] methods = MotionEvent.class.getDeclaredMethods();
		/* 是否支持多点触控 */
		boolean isMulPointer = false;
		for (Method method : methods) {
			if (method.getName().equals("getPointerCount")
					|| method.getName().equals("getPointerId")) {// 两个能够判定支持多点触控的方法名
				isMulPointer = true;
			}
		}
		if (isMulPointer) {
			list.add(getString(R.string.txt_mulpointer_hardware)
					+ getString(R.string.txt_mulpointer_yes_hardware));
		} else {
			list.add(getString(R.string.txt_mulpointer_hardware)
					+ getString(R.string.txt_mulpointer_no_hardware));
		}
		return list;
	}

	/** 获取设备网络信息 */
	private List<String> gleanWIFI() {
		List<String> list = new ArrayList<String>();
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		/*-----获取WIFI连接到-----*/
		if (!TextUtils.isEmpty(wifiInfo.getSSID())) {
			list.add(getString(R.string.txt_wifi_connectto_hardware)
					+ wifiInfo.getSSID());
		} else {
			list.add(getString(R.string.txt_wifi_connectto_hardware)
					+ getString(R.string.txt_wifi_noconnect_hardware));
		}
		/*-----获取WIFI地址-----*/
		int ipInt = wifiInfo.getIpAddress();
		String ipAdress = Formatter.formatIpAddress(ipInt);
		list.add(getString(R.string.txt_wifi_ip_hardware) + ipAdress);
		/*-----获取WIFI连接速度-----*/
		if (!TextUtils.isEmpty(wifiInfo.getSSID())) {
			list.add(getString(R.string.txt_wifi_rate_hardware)
					+ wifiInfo.getLinkSpeed() + wifiInfo.LINK_SPEED_UNITS);
		} else {
			list.add(getString(R.string.txt_wifi_rate_hardware)
					+ getString(R.string.txt_wifi_noconnect_hardware));
		}
		/*-----获取MAC地址-----*/
		list.add(getString(R.string.txt_wifi_mac_hardware)
				+ wifiInfo.getMacAddress());
		/*-----获取蓝牙状态-----*/
		/* 通过静态方法得到相关硬件对应的类 */
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		/* 蓝牙地址 */
		list.add(getString(R.string.txt_wifi_bt_add_hardware)
				+ bluetooth.getAddress());
		/* 蓝牙名称 */
		list.add(getString(R.string.txt_wifi_bt_name_hardware)
				+ bluetooth.getName());
		/* 连接状态（四个） */
		int btState = bluetooth.getState();
		switch (btState) {
		case BluetoothAdapter.STATE_ON:// 蓝牙已打开
			list.add(getString(R.string.txt_wifi_bt_state_hardware)
					+ getString(R.string.txt_wifi_bt_state_on_hardware));
			break;
		case BluetoothAdapter.STATE_OFF:// 蓝牙已关闭
			list.add(getString(R.string.txt_wifi_bt_state_hardware)
					+ getString(R.string.txt_wifi_bt_state_off_hardware));
			break;
		case BluetoothAdapter.STATE_TURNING_ON:// 蓝牙正在打开
			list.add(getString(R.string.txt_wifi_bt_state_hardware)
					+ getString(R.string.txt_wifi_bt_state_turningon_hardware));
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:// 蓝牙正在关闭
			list.add(getString(R.string.txt_wifi_bt_state_hardware)
					+ getString(R.string.txt_wifi_bt_state_turningoff_hardware));
			break;
		default:
			break;
		}
		return list;
	}

	private void initAdapters() {
		mElsAdapter = new ElsAdapter();
		mElsSystemTest.setAdapter(mElsAdapter);
	}

	/**
	 * 分组点击事件监听
	 * 
	 * @param parent
	 *            ExpandableListView组件
	 * @param view
	 *            被点击的分组条目
	 * @param groupPosition
	 *            被点击组编号
	 * @param id
	 *            被点击组ID
	 * @return true:已折叠的展不开，已展开的收不拢；false：正常展开、收拢
	 * */
	@Override
	public boolean onGroupClick(ExpandableListView parent, View view,
			int groupPosition, long id) {
		Toast.makeText(this, mGroup[groupPosition], Toast.LENGTH_SHORT).show();
		return false;
	}

	/**
	 * 分组子条目点击事件监听
	 * 
	 * @param parent
	 *            ExpandableListView组件
	 * @param view
	 *            被点击的分组子条目
	 * @param groupPosition
	 *            被点击组编号
	 * @param childPosition
	 *            被点击组子条目编号
	 * @param id
	 *            被点击组子条目ID
	 * @return true:已折叠的展不开，已展开的收不拢；false：正常展开、收拢
	 * */
	@Override
	public boolean onChildClick(ExpandableListView parent, View view,
			int groupPosition, int childPosition, long id) {
		Toast.makeText(this, mDataLists.get(groupPosition).get(childPosition),
				Toast.LENGTH_SHORT).show();
		return false;
	}

	class ElsAdapter extends BaseExpandableListAdapter {
		/** 获得组的个数 */
		@Override
		public int getGroupCount() {
			return mGroup == null ? 0 : mGroup.length;
		}

		/**
		 * 获得具体成员的个数
		 * 
		 * @param groupPosition
		 *            组ID
		 **/
		@Override
		public int getChildrenCount(int groupPosition) {
			return mDataLists == null || groupPosition >= mDataLists.size()// 防止没有添加子内容而产生的角标越界
					|| mDataLists.get(groupPosition) == null ? 0 : mDataLists
					.get(groupPosition).size();
		}

		/**
		 * 获得组
		 * 
		 * @param groupPosition
		 *            组ID
		 * */
		@Override
		public Object getGroup(int groupPosition) {
			return mGroup[groupPosition];
		}

		/**
		 * 获得具体组具体成员
		 * 
		 * @param groupPosition
		 *            组ID
		 * @param childPosition
		 *            组成员ID
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mDataLists.get(groupPosition).get(childPosition);
		}

		/**
		 * 获得组ID
		 * 
		 * @param groupPosition
		 *            组ID
		 * */
		@Override
		public long getGroupId(int groupPosition) {
			return mGroup[groupPosition].hashCode();
		}

		/**
		 * 获得具体组具体成员ID
		 * 
		 * @param groupPosition
		 *            组ID
		 * @param childPosition
		 *            组成员ID
		 * */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return mDataLists.get(groupPosition).get(childPosition).hashCode();
		}

		/** 是否允许Id改变 */
		@Override
		public boolean hasStableIds() {
			return false;
		}

		/**
		 * 获得具体组布局
		 * 
		 * @param groupPosition
		 *            组ID
		 * @param isExpanded
		 *            组是否展开
		 * @param convertView
		 *            组复用View布局
		 * @param parent
		 *            父容器
		 * 
		 * */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = convertView;
			groupViewHolder holder;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.item_group_hardware, null);
				holder = new groupViewHolder();
				holder.group = (TextView) view
						.findViewById(R.id.txt_group_hardware);
				view.setTag(holder);
			} else {
				holder = (groupViewHolder) view.getTag();
			}
			holder.group.setText(mGroup[groupPosition]);
			return view;
		}

		/**
		 * 获得具体组具体成员布局 获得具体组布局
		 * 
		 * @param groupPosition
		 *            组ID
		 * @param childPosition
		 *            组成员ID
		 * @param isLastChild
		 *            组成员是否是最后一个
		 * @param convertView
		 *            子条目复用View布局
		 * @param parent
		 *            父容器
		 * 
		 * */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = convertView;
			childrenViewHolder holder;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.item_children_hardware, null);
				holder = new childrenViewHolder();
				holder.info = (TextView) view
						.findViewById(R.id.txt_children_info_hardware);
				view.setTag(holder);
			} else {
				holder = (childrenViewHolder) view.getTag();
			}
			holder.info.setText(mDataLists.get(groupPosition)
					.get(childPosition));
			return view;
		}

		/**
		 * 具体组具体成员是否可选择
		 * 
		 * @param groupPosition
		 *            组ID
		 * @param childPosition
		 *            组成员ID
		 * @return true:子条目可以被点击，false：则不能点击
		 * */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	/** 组ViewHolder */
	class groupViewHolder {
		/** 组别标题 */
		TextView group;
	}

	/** 子条目ViewHolder */
	class childrenViewHolder {
		/** 详细信息 */
		TextView info;
	}

}
