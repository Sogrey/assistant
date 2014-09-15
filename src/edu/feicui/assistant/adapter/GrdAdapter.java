package edu.feicui.assistant.adapter;

import java.util.List;

import edu.feicui.assistant.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * �������-������ͼ<br>
 * GridView ������
 * 
 * @author Sogrey
 * 
 */
public class GrdAdapter extends BaseAdapter {
	/** ��Ҫ�����������List */
	List<PackageInfo> mList;
	/** ������������� */
	PackageManager mPackageMgr;
	/** �����Ķ��� */
	Context mContext;

	public GrdAdapter(Context context) {
		mContext = context;
		mPackageMgr = mContext.getPackageManager();
	}

	/** ������Ҫ������������ļ��� */
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
		GridHolder holder;
		if (view == null) {
			holder = new GridHolder();
			view = ((Activity) mContext).getLayoutInflater().inflate(
					edu.feicui.assistant.R.layout.item_grid_software, null);
			holder.icon = (ImageView) view.findViewById(R.id.img_icon_grid_app);
			holder.label = (TextView) view.findViewById(R.id.txt_name_grid_app);
			view.setTag(holder);
		} else {
			holder = (GridHolder) view.getTag();
		}
		PackageInfo info = mList.get(position);
		holder.icon.setImageDrawable(mPackageMgr
				.getApplicationIcon(info.applicationInfo));
		holder.label.setText(mPackageMgr
				.getApplicationLabel(info.applicationInfo));
		return view;
	}

	/** �����ȡ���Ŀؼ����´β���Ҫ��findViewById()�� */
	class GridHolder {
		ImageView icon;
		TextView label;
	}
}
