package edu.feicui.assistant.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * ���������
 * 
 * @author Sogrey
 * 
 */
public final class SoftwareUtil {

	private final Context mContext;
	private PackageManager mPackageMgr;
	private List<PackageInfo> mPackageInfos;
	private List<PackageInfo> mSystems;
	private List<PackageInfo> mUsers;
	private ApplicationInfo mAppInfo;

	private static SoftwareUtil sInstance;

	/** ��ȡ����ģʽ���� */
	public static SoftwareUtil getInstance(Context context) {
		/** ˫�ؼ������ */
		if (sInstance == null) {
			synchronized (CrashHandler.class) {
				if (sInstance == null) {
					sInstance = new SoftwareUtil(
							context.getApplicationContext());
				}
			}
		}
		return sInstance;
	}

	private SoftwareUtil(Context context) {
		mContext = context;
		mPackageMgr = mContext.getPackageManager();
		refreshPackage();
	}

	/** ˢ�°�(��Ҫ����ɾ��Ӧ�ú󣬴��б���Ҳ�Ƴ�) */
	public void refreshPackage() {
		mPackageInfos = mPackageMgr
				.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		mSystems = new ArrayList<PackageInfo>();
		mUsers = new ArrayList<PackageInfo>();
		for (PackageInfo info : mPackageInfos) {
			mAppInfo = info.applicationInfo;
			if ((mAppInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 // ������һ����ϵͳ����
					|| (mAppInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 // ��������ϵͳ����
			) {
				mSystems.add(info);
			} else {
				mUsers.add(info);
			}
		}
	}

	/**
	 * ��ȡ�豸������Ӧ��PackageInfo
	 * */
	public List<PackageInfo> getAllAppInfos() {
		/* ��List���Ʒ��أ�Ϊ��ֹ�û���� */
		List<PackageInfo> list = new ArrayList<PackageInfo>(mPackageInfos);
		return list;
	}

	/** ��ȡ�豸��ϵͳӦ��PackageInfo */
	public List<PackageInfo> getSystemAppInfos() {
		List<PackageInfo> list = new ArrayList<PackageInfo>(mSystems);
		return list;
	}

	/** ��ȡ�豸���û�Ӧ��PackageInfo */
	public List<PackageInfo> getUserAppInfos() {
		List<PackageInfo> list = new ArrayList<PackageInfo>(mUsers);
		return list;
	}
}
