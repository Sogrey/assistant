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
 * ϵ�y�z�yģ�K
 * 
 * @author Sogrey
 * 
 */
public class SystemTestActivity extends BaseActivity implements
		OnGroupClickListener, OnChildClickListener {
	/** CPU ��Ϣ�ļ�·�� */
	protected static final String PATH_CPUINFO = "/proc/cpuinfo";
	/** ���}���ϣ�һά�� */
	protected String[] mGroup;
	/** ���ݼ��ϣ���ά�� */
	protected List<List<String>> mDataLists;
	/** ��չ����ListView���� */
	protected ExpandableListView mElsSystemTest;
	/** ��չ����ListView���������� */
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

	/** ��ʼ������ */
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

	/** ��������Ϣ������ļ� */
	private void outputInFile(List<List<String>> mDataLists) {
		String infoString = "";
		for (List<String> list : mDataLists) {
			for (String data : list) {
				infoString += data + "\n";
			}
		}
		FileUtil.write(infoString);
	}

	/** ��ȡ�豸������Ϣ */
	private List<String> gleanBase() {
		List<String> list = new ArrayList<String>();
		/*-----��ȡ�ֻ�Ʒ��-----*/
		list.add(getString(R.string.txt_brand_hardware) + Build.BRAND);
		/*-----��ȡ��Ʒ����-----*/
		list.add(getString(R.string.txt_product_hardware) + Build.PRODUCT);
		/*-----��ȡ��������-----*/
		list.add(getString(R.string.txt_model_hardware) + Build.MODEL);
		/*-----��ȡ�豸�ͺ�-----*/
		list.add(getString(R.string.txt_devtype_hardware) + Build.DEVICE);
		/*-----��ȡϵͳ�汾-----*/
		list.add(getString(R.string.txt_sysver_hardware)
				+ Build.VERSION.RELEASE);
		TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		/*-----��ȡ�ֻ�����-----*/
		list.add(getString(R.string.txt_isbn_hardware)
				+ telephonyMgr.getDeviceId());
		/*-----��ȡ�ֻ���-----*/
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
		/*-----��ȡ��Ӫ��-----*/
		list.add(getString(R.string.txt_service_hardware) + operator);
		/*-----��ȡ�����汾-----*/
		list.add(getString(R.string.txt_baseband_hardware)
				+ SystemProperties.get("gsm.version.baseband"));
		return list;
	}

	/** ��ȡCPU��Ϣ */
	private List<String> gleanCPU() {
		List<String> list = new ArrayList<String>();
		/*-----��ȡCPU�ͺ�-----*/
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
		/*-----��ȡCPU������-----*/
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
		/*-----��ȡCPU���Ƶ��-----*/
		list.add(getString(R.string.txt_maxfreq_hardware,
				FileUtil.read(cpuDevFolder + "cpu0/cpufreq/cpuinfo_max_freq")));
		/*-----��ȡCPU���Ƶ��-----*/
		list.add(getString(R.string.txt_minfreq_hardware,
				FileUtil.read(cpuDevFolder + "cpu0/cpufreq/cpuinfo_min_freq")));
		/*-----��ȡCPU��ǰƵ��-----*/
//		list.add(getString(R.string.txt_curfreq_hardware,
//				FileUtil.read(cpuDevFolder + "cpu0/cpufreq/cpuinfo_cur_freq")));
		String cur = ShellExcuter.exec("cat", cpuDevFolder
				+ "cpu0/cpufreq/cpuinfo_cur_freq");
		list.add(getString(R.string.txt_curfreq_hardware, cur));
		// XXX ���ܻ�ȡ��������
		// list.add(getString(R.string.txt_curfreq_hardware,
		// FileUtil.read(cpuDevFolder + "/cpu0/cpufreq/cpuinfo_cur_freq")));

		return list;
	}

	/** ��ȡ�豸�ڴ���Ϣ */
	@SuppressWarnings("deprecation")
	private List<String> gleanMemory() {
		List<String> list = new ArrayList<String>();
		Pattern pattern;
		Matcher matcher;
		/*-----��ȡ�����ڴ�����-----*/
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
		/*-----��ȡ�ֻ������ڴ�-----*/
		ActivityManager activityMgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		activityMgr.getMemoryInfo(outInfo);
		list.add(getString(R.string.txt_memavaitotal_hardware,
				Formatter.formatFileSize(this, outInfo.availMem)));
		/*-----��ȡsd���ڴ�����-----*/
		File sdcard = Environment.getExternalStorageDirectory();
		// sdcard.getTotalSpace() API 9����
		// list.add(getString(R.string.txt_memavaitotal_hardware,
		// Formatter.formatFileSize(this, sdcard.getTotalSpace())));
		StatFs sFs = new StatFs(sdcard.getAbsolutePath());
		long total = 1L * sFs.getBlockSize() * sFs.getBlockCount();
		list.add(getString(R.string.txt_memsdtotal_hardware,
				Formatter.formatFileSize(this, total)));
		return list;
	}

	/** ��ȡ�豸�ֱ��� */
	private List<String> gleanDefinition() {
		List<String> list = new ArrayList<String>();
		/*-----��ȡ�ֻ���Ļ�ֱ���-----*/
		DisplayMetrics outMetrics = new DisplayMetrics();
		/*
		 * ��ȡ���ڹ���ͨ�����ڹ���õ���Ļ��Ϣ ��ȡĬ����Ļ��Ϣ ��ȡ��Ļ�ߴ���Ϣ��ͨ�������������
		 */
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		list.add(getString(R.string.txt_split_screen_hardware)
				+ outMetrics.widthPixels
				+ getString(R.string.txt_pix_sizemul_hardware)
				+ outMetrics.heightPixels);
		Camera camera = null;
		/* �ж���������ͷ�豸 */
		if (Camera.getNumberOfCameras() > 0) {
			camera = Camera.open();
			Parameters parameters = camera.getParameters();
			/*-----��ȡ��Ƭ���߶�-----*/
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
			/*-----��ȡ�����-----*/
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

	/** ��ȡ�豸���� */
	private List<String> gleanPixel() {
		List<String> list = new ArrayList<String>();
		/*-----��ȡ�ֻ�����-----*/
		Camera camera = null;
		/* �ж���������ͷ�豸 */
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
		/*-----��ȡ�����ܶ�-----*/
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		list.add(getString(R.string.txt_dot_density_hardware)
				+ outMetrics.densityDpi);
		/*-----��ȡ��㴥��-----*/
		// ��ȡMotionEvent��֧�����з���
		Method[] methods = MotionEvent.class.getDeclaredMethods();
		/* �Ƿ�֧�ֶ�㴥�� */
		boolean isMulPointer = false;
		for (Method method : methods) {
			if (method.getName().equals("getPointerCount")
					|| method.getName().equals("getPointerId")) {// �����ܹ��ж�֧�ֶ�㴥�صķ�����
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

	/** ��ȡ�豸������Ϣ */
	private List<String> gleanWIFI() {
		List<String> list = new ArrayList<String>();
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		/*-----��ȡWIFI���ӵ�-----*/
		if (!TextUtils.isEmpty(wifiInfo.getSSID())) {
			list.add(getString(R.string.txt_wifi_connectto_hardware)
					+ wifiInfo.getSSID());
		} else {
			list.add(getString(R.string.txt_wifi_connectto_hardware)
					+ getString(R.string.txt_wifi_noconnect_hardware));
		}
		/*-----��ȡWIFI��ַ-----*/
		int ipInt = wifiInfo.getIpAddress();
		String ipAdress = Formatter.formatIpAddress(ipInt);
		list.add(getString(R.string.txt_wifi_ip_hardware) + ipAdress);
		/*-----��ȡWIFI�����ٶ�-----*/
		if (!TextUtils.isEmpty(wifiInfo.getSSID())) {
			list.add(getString(R.string.txt_wifi_rate_hardware)
					+ wifiInfo.getLinkSpeed() + wifiInfo.LINK_SPEED_UNITS);
		} else {
			list.add(getString(R.string.txt_wifi_rate_hardware)
					+ getString(R.string.txt_wifi_noconnect_hardware));
		}
		/*-----��ȡMAC��ַ-----*/
		list.add(getString(R.string.txt_wifi_mac_hardware)
				+ wifiInfo.getMacAddress());
		/*-----��ȡ����״̬-----*/
		/* ͨ����̬�����õ����Ӳ����Ӧ���� */
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		/* ������ַ */
		list.add(getString(R.string.txt_wifi_bt_add_hardware)
				+ bluetooth.getAddress());
		/* �������� */
		list.add(getString(R.string.txt_wifi_bt_name_hardware)
				+ bluetooth.getName());
		/* ����״̬���ĸ��� */
		int btState = bluetooth.getState();
		switch (btState) {
		case BluetoothAdapter.STATE_ON:// �����Ѵ�
			list.add(getString(R.string.txt_wifi_bt_state_hardware)
					+ getString(R.string.txt_wifi_bt_state_on_hardware));
			break;
		case BluetoothAdapter.STATE_OFF:// �����ѹر�
			list.add(getString(R.string.txt_wifi_bt_state_hardware)
					+ getString(R.string.txt_wifi_bt_state_off_hardware));
			break;
		case BluetoothAdapter.STATE_TURNING_ON:// �������ڴ�
			list.add(getString(R.string.txt_wifi_bt_state_hardware)
					+ getString(R.string.txt_wifi_bt_state_turningon_hardware));
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:// �������ڹر�
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
	 * �������¼�����
	 * 
	 * @param parent
	 *            ExpandableListView���
	 * @param view
	 *            ������ķ�����Ŀ
	 * @param groupPosition
	 *            ���������
	 * @param id
	 *            �������ID
	 * @return true:���۵���չ��������չ�����ղ�£��false������չ������£
	 * */
	@Override
	public boolean onGroupClick(ExpandableListView parent, View view,
			int groupPosition, long id) {
		Toast.makeText(this, mGroup[groupPosition], Toast.LENGTH_SHORT).show();
		return false;
	}

	/**
	 * ��������Ŀ����¼�����
	 * 
	 * @param parent
	 *            ExpandableListView���
	 * @param view
	 *            ������ķ�������Ŀ
	 * @param groupPosition
	 *            ���������
	 * @param childPosition
	 *            �����������Ŀ���
	 * @param id
	 *            �����������ĿID
	 * @return true:���۵���չ��������չ�����ղ�£��false������չ������£
	 * */
	@Override
	public boolean onChildClick(ExpandableListView parent, View view,
			int groupPosition, int childPosition, long id) {
		Toast.makeText(this, mDataLists.get(groupPosition).get(childPosition),
				Toast.LENGTH_SHORT).show();
		return false;
	}

	class ElsAdapter extends BaseExpandableListAdapter {
		/** �����ĸ��� */
		@Override
		public int getGroupCount() {
			return mGroup == null ? 0 : mGroup.length;
		}

		/**
		 * ��þ����Ա�ĸ���
		 * 
		 * @param groupPosition
		 *            ��ID
		 **/
		@Override
		public int getChildrenCount(int groupPosition) {
			return mDataLists == null || groupPosition >= mDataLists.size()// ��ֹû����������ݶ������ĽǱ�Խ��
					|| mDataLists.get(groupPosition) == null ? 0 : mDataLists
					.get(groupPosition).size();
		}

		/**
		 * �����
		 * 
		 * @param groupPosition
		 *            ��ID
		 * */
		@Override
		public Object getGroup(int groupPosition) {
			return mGroup[groupPosition];
		}

		/**
		 * ��þ���������Ա
		 * 
		 * @param groupPosition
		 *            ��ID
		 * @param childPosition
		 *            ���ԱID
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mDataLists.get(groupPosition).get(childPosition);
		}

		/**
		 * �����ID
		 * 
		 * @param groupPosition
		 *            ��ID
		 * */
		@Override
		public long getGroupId(int groupPosition) {
			return mGroup[groupPosition].hashCode();
		}

		/**
		 * ��þ���������ԱID
		 * 
		 * @param groupPosition
		 *            ��ID
		 * @param childPosition
		 *            ���ԱID
		 * */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return mDataLists.get(groupPosition).get(childPosition).hashCode();
		}

		/** �Ƿ�����Id�ı� */
		@Override
		public boolean hasStableIds() {
			return false;
		}

		/**
		 * ��þ����鲼��
		 * 
		 * @param groupPosition
		 *            ��ID
		 * @param isExpanded
		 *            ���Ƿ�չ��
		 * @param convertView
		 *            �鸴��View����
		 * @param parent
		 *            ������
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
		 * ��þ���������Ա���� ��þ����鲼��
		 * 
		 * @param groupPosition
		 *            ��ID
		 * @param childPosition
		 *            ���ԱID
		 * @param isLastChild
		 *            ���Ա�Ƿ������һ��
		 * @param convertView
		 *            ����Ŀ����View����
		 * @param parent
		 *            ������
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
		 * ����������Ա�Ƿ��ѡ��
		 * 
		 * @param groupPosition
		 *            ��ID
		 * @param childPosition
		 *            ���ԱID
		 * @return true:����Ŀ���Ա������false�����ܵ��
		 * */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	/** ��ViewHolder */
	class groupViewHolder {
		/** ������ */
		TextView group;
	}

	/** ����ĿViewHolder */
	class childrenViewHolder {
		/** ��ϸ��Ϣ */
		TextView info;
	}

}
