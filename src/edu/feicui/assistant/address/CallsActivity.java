package edu.feicui.assistant.address;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.util.Constants;

/**
 * 通话记录
 * 
 * @author Sogrey
 * 
 */
public class CallsActivity extends BaseActivity {
	/** ContentResolver内容解析器 对象 */
	protected ContentResolver mContentResolver;
	/** 扫描通话记录数据的游标 */
	Cursor cursor;
	/** ListView组件 成员对象 */
	protected ListView mLstCalls;
	/** 页面标题 文本 */
	protected TextView mTxtTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calls);
		initDatas();
		initViews();
		initAdapters();
	}

	/** 初始化通话记录数据 */
	private void initDatas() {
		mContentResolver = getContentResolver();
		cursor = mContentResolver.query(Calls.CONTENT_URI, null, null, null,
				Calls.DATE + " DESC");
	}

	/** 初始化View组件 */
	private void initViews() {
		mTxtTitle = (TextView) findViewById(R.id.txt_title_page);
		/* 设置页面标题 */
		mTxtTitle.setText(getTitle());
		mLstCalls = (ListView) findViewById(R.id.lst_calls);
	}

	/** 初始化适配器 */
	private void initAdapters() {
		LstCallsAdapter callsAdapter = new LstCallsAdapter(this, cursor);
		mLstCalls.setAdapter(callsAdapter);
	}

	/** ListView适配器 */
	class LstCallsAdapter extends CursorAdapter {
		/** 列序号-姓名 */
		final int INDEX_NAME;
		/** 列序号-电话号码 */
		final int INDEX_NUMBER;
		/** 列序号-呼叫类型 */
		final int INDEX_TYPE;
		/** 列序号-日期 */
		final int INDEX_DATE;
		/** 列序号-通话时长 */
		final int INDEX_DURATION;
		/** 日期格式化格式 */
		final SimpleDateFormat FMT_DATE;
		/** 时间格式化格式 */
		final SimpleDateFormat FMT_TIME;

		public LstCallsAdapter(Context context, Cursor cursor) {
			super(context, cursor);
			INDEX_NAME = cursor.getColumnIndex(Calls.CACHED_NAME);
			INDEX_NUMBER = cursor.getColumnIndex(Calls.NUMBER);
			INDEX_TYPE = cursor.getColumnIndex(Calls.TYPE);
			INDEX_DATE = cursor.getColumnIndex(Calls.DATE);
			INDEX_DURATION = cursor.getColumnIndex(Calls.DURATION);
			FMT_DATE = new SimpleDateFormat(Constants.FMT_DATE_CALLS);
			FMT_TIME = new SimpleDateFormat(Constants.FMT_DURATION_CALLS);
			FMT_TIME.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = getLayoutInflater().inflate(R.layout.item_calls, null);
			ViewHolder holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.txt_name_calls);
			holder.number = (TextView) view
					.findViewById(R.id.txt_phone_number_calls);
			holder.type = (TextView) view.findViewById(R.id.txt_type_calls);
			holder.date = (TextView) view.findViewById(R.id.txt_date_calls);
			holder.duration = (TextView) view
					.findViewById(R.id.txt_called_time_calls);
			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			String name = cursor.getString(INDEX_NAME);
			String number = cursor.getString(INDEX_NUMBER);
			int type = cursor.getInt(INDEX_TYPE);
			long date = cursor.getLong(INDEX_DATE);
			long duration = cursor.getLong(INDEX_DURATION);// 秒
			holder.name
					.setText("-1".equals(number) || TextUtils.isEmpty(name) ? getString(R.string.txt_unknown_people_calls)
							: name);
			holder.number
					.setText("-1".equals(number) ? getString(R.string.txt_unknown_number_calls)
							: number);
			String callType = null;
			switch (type) {
			case Calls.INCOMING_TYPE:// 呼入
				callType = getString(R.string.txt_incoming_type_calls);
				holder.type.setTextColor(getResources().getColor(
						R.color.dark_green));
				break;
			case Calls.OUTGOING_TYPE:// 呼出
				callType = getString(R.string.txt_outgoing_type_calls);
				holder.type.setTextColor(getResources().getColor(R.color.blue));
				break;
			case Calls.MISSED_TYPE:// 未接
				callType = getString(R.string.txt_missed_type_calls);
				holder.type.setTextColor(getResources().getColor(R.color.red));
				break;
			default:// 未知
				callType = getString(R.string.txt_unknown);
				holder.type.setTextColor(getResources()
						.getColor(R.color.yellow));
				break;
			}
			holder.type.setText(callType);
			Date time = new Date(date);
			holder.date.setText(FMT_DATE.format(time));

			// holder.duration.setText(duration/3600 + ":"+duration/60 +
			// ":"+duration%60 );
			holder.duration.setText(FMT_TIME.format(duration * 1000L));
		}

	}

	class ViewHolder {
		/** TextView-姓名 */
		TextView name;
		/** TextView-电话 */
		TextView number;
		/** TextView-呼叫类型 */
		TextView type;
		/** TextView-日期 */
		TextView date;
		/** TextView-通话时长 */
		TextView duration;
	}
}
