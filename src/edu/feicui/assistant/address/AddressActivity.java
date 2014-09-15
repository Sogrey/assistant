package edu.feicui.assistant.address;

import java.io.InputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.main.MenuActivity;

/**
 * ͨѶ¼
 * 
 * @author Sogrey
 * 
 */
public class AddressActivity extends BaseActivity implements
		OnItemLongClickListener, OnClickListener,
		android.view.View.OnClickListener, TextWatcher, OnItemClickListener {

	/** ������ϵ�������Ĳ˵�-��绰 */
	protected static final int CALL = 0x00;
	/** ������ϵ�������Ĳ˵�-������ */
	protected static final int SMS = 0x01;
	/** Longѡ��Ի���ID */
	protected static final int DIALOG_CALL_AND_SMS_MENU = 0x10;
	/** ���˵��������˵��Ի���ID */
	protected static final int DIALOG_SHOW_MENU = 0x11;
	/** ɾ�����жԻ���ID */
	protected static final int DIALOG_DELETE_ALL_MENU = 0x12;
	/** Tel�� */
	protected static final String TEL_TO = "tel:";
	/** smsto�� */
	protected static final String SMS_TO = "smsto:";
	/** sms_body�� */
	protected static final String SMS_BODY = "sms_body";
	/** ѡ�еĵ绰���룬���ڴ�绰�������� */
	private String mName;
	/** ѡ�еĵ绰���룬���ڴ�绰�������� */
	private String mNumber;
	/** ContentResolver���ݽ����� ���� */
	protected ContentResolver mContentResolver;
	/** ListView������ */
	LstAdapter adapter;
	/** ҳ����� �ı� */
	protected TextView mTxtTitle;
	/** ListView��� ��Ա���� */
	protected ListView mLstAddress;
	/** �˵��� */
	protected View mLytMenu;
	/** ��������չʾ�� */
	protected View mLytContent;
	/** �½��� */
	protected Button mBtnNew;
	/** ���Ҽ� */
	protected Button mBtnFind;
	/** ���ż� */
	protected Button mBtnCall;
	/** �˵��� */
	protected Button mBtnMenu;
	/** �˳��� */
	protected Button mBtnExit;
	/** ��������� */
	protected EditText mEdtInput;
	/** ѡ����ϵ�˵�RAW_CONTACT_ID����Ϊ��ϵ��Ψһ��ʶ */
	private long mRawId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people);
		initComps();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initViews();
	}

	private void initComps() {
		mContentResolver = getContentResolver();
	}

	/** ��ȡ��ϵ������-Cursor */
	private Cursor readDatas() {
		Cursor cursor = mContentResolver.query(Phone.CONTENT_URI, new String[] {
				Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER,
				Phone.RAW_CONTACT_ID, RawContacts._ID }, null, null,
				Phone.DISPLAY_NAME + " ASC");
		/** ���´����ǽ����������ļ� */

		/*
		 * String infoString = ""; while (cursor.moveToNext()) { for (int i = 0;
		 * i < cursor.getColumnCount(); i++) { infoString += cursor.getString(i)
		 * + ","; } infoString += "\n\n"; FileUtil.write(infoString); }
		 */

		return cursor;
	}

	private void initViews() {
		mTxtTitle = (TextView) findViewById(R.id.txt_title_page);
		/* ����ҳ����� */
		mTxtTitle.setText(getTitle());
		mLstAddress = (ListView) findViewById(R.id.lst_address);
		initAdapter();
		mLstAddress.setOnItemClickListener(this);
		mLstAddress.setOnItemLongClickListener(this);

		mLytMenu = findViewById(R.id.lyt_menu_address);
		mBtnNew = (Button) findViewById(R.id.btn_add_people_address);
		mBtnNew.setOnClickListener(this);
		mBtnFind = (Button) findViewById(R.id.btn_find_people_address);
		mBtnFind.setOnClickListener(this);
		mBtnCall = (Button) findViewById(R.id.btn_call_people_address);
		mBtnCall.setOnClickListener(this);
		mBtnMenu = (Button) findViewById(R.id.btn_menu_people_address);
		mBtnMenu.setOnClickListener(this);
		mBtnExit = (Button) findViewById(R.id.btn_exit_people_address);
		mBtnExit.setOnClickListener(this);
		mEdtInput = (EditText) findViewById(R.id.edt_find_address);
		mEdtInput.addTextChangedListener(this);
		mEdtInput.setOnClickListener(this);
		mLytContent = findViewById(R.id.lyt_content_address);
	}

	private void initAdapter() {
		adapter = new LstAdapter(this, readDatas());
		mLstAddress.setAdapter(adapter);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence characters, int start, int before,
			int count) {
		String input = mEdtInput.getText().toString();
		Cursor cursor = mContentResolver.query(Phone.CONTENT_URI,// Ҫ��������Uri
				new String[] { Phone._ID,// _ID ��ͬID������ʾͬһ����
						Phone.DISPLAY_NAME,// ����
						Phone.NUMBER,// �绰
						Phone.RAW_CONTACT_ID,// ������ϵ��Ψһ��ʶ
						RawContacts._ID },// ͷ��ID
				Phone.NUMBER + " like '%" + input + "%' or "// �绰����ģ������
						+ Phone.DISPLAY_NAME + " like '%" + input + "%'", // ����ģ������
				null,// ����������Ӧֵ�ļ�ֵ��
				Phone.DISPLAY_NAME + " ASC");// ��������������
		adapter.changeCursor(cursor);// �����������α�
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		mRawId = cursor.getLong(cursor.getColumnIndex(Phone.RAW_CONTACT_ID));// RAW_CONTACT_ID����Ψһ��ʶһ����ϵ��
		Intent intent = new Intent(this, EditActivity.class);
		intent.putExtra("raw", mRawId);
		intent.putExtra("type", 2);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		mNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
		mName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
		showDialog(DIALOG_CALL_AND_SMS_MENU);
		return true;
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CALL_AND_SMS_MENU:// ������ϵ�˵���ѡ��ͨ������Ϣѡ��Ի���
			return createCallDialog();
		case DIALOG_SHOW_MENU:// ���˵��������˵��Ի���
			return createShowMenuDialog();
		case DIALOG_DELETE_ALL_MENU:// ɾ�����ж���ȷ�϶Ի���
			return createDeleteAllDialog();
		default:
			return super.onCreateDialog(id);
		}
	}

	/** ɾ�����ж���ȷ�϶Ի��� */
	private Dialog createDeleteAllDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.txt_title_isdelall_address);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, this);
		return builder.create();
	}

	/** ���˵��������˵��Ի��� */
	private Dialog createShowMenuDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_show_menu_adress, null);
		/* LayoutInflater�ṩһ��ת�����̣���֮ǰ��xml�����ļ�ת���ɵ�Ҫ��ʾ�����ʵ�������� View */
		Button btnShowAll = (Button) view
				.findViewById(R.id.btn_show_menu_showall_address);
		btnShowAll.setOnClickListener(this);
		Button btnDeleteAll = (Button) view
				.findViewById(R.id.btn_show_menu_deleteall_address);
		btnDeleteAll.setOnClickListener(this);
		Button btnBack = (Button) view
				.findViewById(R.id.btn_show_menu_back_address);
		btnBack.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);// ��WIN�Ի�����view��������
		return builder.create();
	}

	/** ������ϵ�˵���ѡ��ͨ������Ϣѡ��Ի��� */
	private Dialog createCallDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.txt_dialog_title_address));
		builder.setItems(R.array.stringarray_item_dialog_address, this);
		builder.setNegativeButton(getString(R.string.cancel), null);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Intent intent = null;
		switch (which) {
		case CALL:// ��绰
		{
			intent = new Intent();
			intent.setAction(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(TEL_TO + mNumber));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}
			break;
		case SMS:// ������
		{
			intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(SMS_TO + mNumber));
			intent.putExtra(SMS_BODY, "");
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}
			break;

		case DialogInterface.BUTTON_POSITIVE:// ɾ��������ϵ��ȷ��
		{
			// ɾ������
			deleteAllPeople();
		}
			break;
		case DialogInterface.BUTTON_NEGATIVE:// ȡ��
		{
			// ȡ�� Nothing to do!
		}
			break;
		default:
			break;
		}
	}


	/** ɾ��������ϵ�� */
	private void deleteAllPeople() {
		Uri uri = ContactsContract.RawContacts.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,
						"true").build();
		mContentResolver.delete(uri, null, null);// RawContacts��-�˻����
		Toast.makeText(this,
				getString(R.string.txt_show_menu_deleteall_address),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_add_people_address:// �����ϵ��
		{
			Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra("type", 1);
			startActivity(intent);
		}
			break;
		case R.id.btn_find_people_address:// ������ϵ��
			if (View.GONE == mEdtInput.getVisibility()) {
				mEdtInput.setVisibility(View.VISIBLE);
				mEdtInput.requestFocus();// ʹEditText��ȡ����

			} else {
				mEdtInput.setVisibility(View.GONE);
				// mEdtInput.setFocusable(false);
			}
			break;
		case R.id.btn_call_people_address:// ͨ����¼
		{
			Intent intent = new Intent(this, CallsActivity.class);
			startActivity(intent);
			this.finish();
		}
			break;
		case R.id.btn_menu_people_address:// �˵�
		{
			showDialog(DIALOG_SHOW_MENU);
		}
			break;
		case R.id.btn_exit_people_address:// �˳�
		{
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
			this.finish();
		}
			break;
		case R.id.edt_find_address:// EditText������ᵯ�������
			mEdtInput.requestFocus();// ʹEditText��ȡ����
			break;

		case R.id.btn_show_menu_showall_address:// ��ʾ����
		{
			Cursor cursor = readDatas();
			adapter.changeCursor(cursor);
			mEdtInput.setText("");
			mEdtInput.setVisibility(View.GONE);
			dismissDialog(DIALOG_SHOW_MENU);
		}
			break;

		case R.id.btn_show_menu_deleteall_address:// ɾ������
		{
			dismissDialog(DIALOG_SHOW_MENU);
			showDialog(DIALOG_DELETE_ALL_MENU);
		}
			break;

		case R.id.btn_show_menu_back_address:// ����
			dismissDialog(DIALOG_SHOW_MENU);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:// �˵���
			if (View.GONE == mLytMenu.getVisibility())
				mLytMenu.setVisibility(View.VISIBLE);
			else {
				mLytMenu.setVisibility(View.GONE);
				mEdtInput.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	class LstAdapter extends CursorAdapter {
		/** �����-���� */
		final int INDEX_NAME;
		/** �����-�绰���� */
		final int INDEX_NUMBER;
		/** �����- */
		final int INDEX_RAW;

		public LstAdapter(Context context, Cursor cursor) {
			super(context, cursor);
			cursor.getColumnCount();
			INDEX_NAME = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			INDEX_NUMBER = cursor.getColumnIndex(Phone.NUMBER);
			INDEX_RAW = cursor.getColumnIndex(RawContacts._ID);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = getLayoutInflater()
					.inflate(R.layout.item_address, null);
			ViewHolder holder = new ViewHolder();
			holder.icon = (ImageView) view.findViewById(R.id.img_photo_address);
			holder.name = (TextView) view
					.findViewById(R.id.txt_item_name_address);
			holder.number = (TextView) view
					.findViewById(R.id.txt_item_phone_address);
			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			String name = cursor.getString(INDEX_NAME);
			String number = cursor.getString(INDEX_NUMBER);
			holder.name
					.setText(getString(R.string.txt_item_name_address, name));
			holder.number.setText(getString(R.string.txt_item_number_address,
					number));
			long photoId = cursor.getLong(INDEX_RAW);
			/* ��ID׺��Uri�ϣ���ʾĳ���ض���ID�������׺��ʾһ�࣬������ */
			Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, photoId);
			/*
			 * ��ȡͷ��Bitmap�� ��һ��������ContentResolver �ڶ�������������ID��Uri ����ֵΪ��Ƭ������
			 */
			InputStream input = Contacts.openContactPhotoInputStream(
					mContentResolver, uri);
			if (input != null) {
				Bitmap bmp = BitmapFactory.decodeStream(input);
				holder.icon.setImageBitmap(bmp);
			} else {
				holder.icon.setImageResource(R.drawable.icon_tab_user_software);
			}
		}
	}

	class ViewHolder {
		/** ImageView-ͷ�� */
		ImageView icon;
		/** TextView-���� */
		TextView name;
		/** TextView-�绰 */
		TextView number;
	}

}
