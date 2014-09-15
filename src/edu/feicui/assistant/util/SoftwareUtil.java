package edu.feicui.assistant.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 软件管理工具
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

	/** 获取单例模式对象 */
	public static SoftwareUtil getInstance(Context context) {
		/** 双重检查锁定 */
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

	/** 刷新包(主要用于删除应用后，从列表中也移除) */
	public void refreshPackage() {
		mPackageInfos = mPackageMgr
				.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		mSystems = new ArrayList<PackageInfo>();
		mUsers = new ArrayList<PackageInfo>();
		for (PackageInfo info : mPackageInfos) {
			mAppInfo = info.applicationInfo;
			if ((mAppInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 // 满足则一定是系统程序
					|| (mAppInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 // 升级过的系统程序
			) {
				mSystems.add(info);
			} else {
				mUsers.add(info);
			}
		}
	}

	/**
	 * 获取设备中所有应用PackageInfo
	 * */
	public List<PackageInfo> getAllAppInfos() {
		/* 将List复制返回，为防止用户误改 */
		List<PackageInfo> list = new ArrayList<PackageInfo>(mPackageInfos);
		return list;
	}

	/** 获取设备中系统应用PackageInfo */
	public List<PackageInfo> getSystemAppInfos() {
		List<PackageInfo> list = new ArrayList<PackageInfo>(mSystems);
		return list;
	}

	/** 获取设备中用户应用PackageInfo */
	public List<PackageInfo> getUserAppInfos() {
		List<PackageInfo> list = new ArrayList<PackageInfo>(mUsers);
		return list;
	}
}
