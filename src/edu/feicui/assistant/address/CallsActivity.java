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
 * ͨ����¼
 * 
 * @author Sogrey
 * 
 */
public class CallsActivity extends BaseActivity {
	/** ContentResolver���ݽ����� ���� */
	protected ContentResolver mContentResolver;
	/** ɨ��ͨ����¼���ݵ��α� */
	Cursor cursor;
	/** ListView��� ��Ա���� */
	protected ListView mLstCalls;
	/** ҳ����� �ı� */
	protected TextView mTxtTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calls);
		initDatas();
		initViews();
		initAdapters();
	}

	/** ��ʼ��ͨ����¼���� */
	private void initDatas() {
		mContentResolver = getContentResolver();
		cursor = mContentResolver.query(Calls.CONTENT_URI, null, null, null,
				Calls.DATE + " DESC");
	}

	/** ��ʼ��View��� */
	private void initViews() {
		mTxtTitle = (TextView) findViewById(R.id.txt_title_page);
		/* ����ҳ����� */
		mTxtTitle.setText(getTitle());
		mLstCalls = (ListView) findViewById(R.id.lst_calls);
	}

	/** ��ʼ�������� */
	private void initAdapters() {
		LstCallsAdapter callsAdapter = new LstCallsAdapter(this, cursor);
		mLstCalls.setAdapter(callsAdapter);
	}

	/** ListView������ */
	class LstCallsAdapter extends CursorAdapter {
		/** �����-���� */
		final int INDEX_NAME;
		/** �����-�绰���� */
		final int INDEX_NUMBER;
		/** �����-�������� */
		final int INDEX_TYPE;
		/** �����-���� */
		final int INDEX_DATE;
		/** �����-ͨ��ʱ�� */
		final int INDEX_DURATION;
		/** ���ڸ�ʽ����ʽ */
		final SimpleDateFormat FMT_DATE;
		/** ʱ���ʽ����ʽ */
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
			long duration = cursor.getLong(INDEX_DURATION);// ��
			holder.name
					.setText("-1".equals(number) || TextUtils.isEmpty(name) ? getString(R.string.txt_unknown_people_calls)
							: name);
			holder.number
					.setText("-1".equals(number) ? getString(R.string.txt_unknown_number_calls)
							: number);
			String callType = null;
			switch (type) {
			case Calls.INCOMING_TYPE:// ����
				callType = getString(R.string.txt_incoming_type_calls);
				holder.type.setTextColor(getResources().getColor(
						R.color.dark_green));
				break;
			case Calls.OUTGOING_TYPE:// ����
				callType = getString(R.string.txt_outgoing_type_calls);
				holder.type.setTextColor(getResources().getColor(R.color.blue));
				break;
			case Calls.MISSED_TYPE:// δ��
				callType = getString(R.string.txt_missed_type_calls);
				holder.type.setTextColor(getResources().getColor(R.color.red));
				break;
			default:// δ֪
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
		/** TextView-���� */
		TextView name;
		/** TextView-�绰 */
		TextView number;
		/** TextView-�������� */
		TextView type;
		/** TextView-���� */
		TextView date;
		/** TextView-ͨ��ʱ�� */
		TextView duration;
	}
}
