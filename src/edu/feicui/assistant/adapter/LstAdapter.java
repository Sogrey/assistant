package edu.feicui.assistant.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.feicui.assistant.R;

/**
 * 软件管理-列表视图<br>
 * ListView 适配器
 * 
 * @author Sogrey
 * 
 */
public class LstAdapter extends BaseAdapter {

	/** 需要适配器处理的List */
	List<PackageInfo> mList;
	/** 创建包管理对象 */
	PackageManager mPackageMgr;
	/** 上下文对象 */
	Context mContext;

	public LstAdapter(Context context) {
		mContext = context;
		mPackageMgr = mContext.getPackageManager();
	}

	/** 传入需要该适配器处理的集合 */
	public void setSets(List<PackageInfo> list) {
		mList = list;
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
		ListHolder holder;
		if (view == null) {
			holder = new ListHolder();
			view = ((Activity) mContext).getLayoutInflater().inflate(
					edu.feicui.assistant.R.layout.item_list_software, null);
			holder.icon = (ImageView) view.findViewById(R.id.img_icon_list_app);
			holder.label = (TextView) view.findViewById(R.id.txt_name_list_app);
			holder.packageName = (TextView) view
					.findViewById(R.id.txt_package_list_app);
			view.setTag(holder);
		} else {
			holder = (ListHolder) view.getTag();
		}
		PackageInfo info = mList.get(position);
		holder.icon.setImageDrawable(mPackageMgr
				.getApplicationIcon(info.applicationInfo));
		holder.label.setText(mPackageMgr
				.getApplicationLabel(info.applicationInfo));
		holder.packageName.setText(info.applicationInfo.packageName);
		return view;
	}

	/** 保存获取到的控件，下次不需要再findViewById()了 */
	class ListHolder {
		ImageView icon;
		TextView label;
		TextView packageName;
	}
}
