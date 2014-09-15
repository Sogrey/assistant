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
 * 通讯录
 * 
 * @author Sogrey
 * 
 */
public class AddressActivity extends BaseActivity implements
		OnItemLongClickListener, OnClickListener,
		android.view.View.OnClickListener, TextWatcher, OnItemClickListener {

	/** 长按联系人上下文菜单-打电话 */
	protected static final int CALL = 0x00;
	/** 长按联系人上下文菜单-发短信 */
	protected static final int SMS = 0x01;
	/** Long选项对话框ID */
	protected static final int DIALOG_CALL_AND_SMS_MENU = 0x10;
	/** 按菜单键弹出菜单对话框ID */
	protected static final int DIALOG_SHOW_MENU = 0x11;
	/** 删除所有对话框ID */
	protected static final int DIALOG_DELETE_ALL_MENU = 0x12;
	/** Tel： */
	protected static final String TEL_TO = "tel:";
	/** smsto： */
	protected static final String SMS_TO = "smsto:";
	/** sms_body： */
	protected static final String SMS_BODY = "sms_body";
	/** 选中的电话号码，用于打电话，发短信 */
	private String mName;
	/** 选中的电话号码，用于打电话，发短信 */
	private String mNumber;
	/** ContentResolver内容解析器 对象 */
	protected ContentResolver mContentResolver;
	/** ListView适配器 */
	LstAdapter adapter;
	/** 页面标题 文本 */
	protected TextView mTxtTitle;
	/** ListView组件 成员对象 */
	protected ListView mLstAddress;
	/** 菜单栏 */
	protected View mLytMenu;
	/** 搜索内容展示区 */
	protected View mLytContent;
	/** 新建键 */
	protected Button mBtnNew;
	/** 查找键 */
	protected Button mBtnFind;
	/** 拨号键 */
	protected Button mBtnCall;
	/** 菜单键 */
	protected Button mBtnMenu;
	/** 退出键 */
	protected Button mBtnExit;
	/** 查找输入框 */
	protected EditText mEdtInput;
	/** 选定联系人的RAW_CONTACT_ID，作为联系人唯一标识 */
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

	/** 获取联系人数据-Cursor */
	private Cursor readDatas() {
		Cursor cursor = mContentResolver.query(Phone.CONTENT_URI, new String[] {
				Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER,
				Phone.RAW_CONTACT_ID, RawContacts._ID }, null, null,
				Phone.DISPLAY_NAME + " ASC");
		/** 以下代码是将结果输出到文件 */

		/*
		 * String infoString = ""; while (cursor.moveToNext()) { for (int i = 0;
		 * i < cursor.getColumnCount(); i++) { infoString += cursor.getString(i)
		 * + ","; } infoString += "\n\n"; FileUtil.write(infoString); }
		 */

		return cursor;
	}

	private void initViews() {
		mTxtTitle = (TextView) findViewById(R.id.txt_title_page);
		/* 设置页面标题 */
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
		Cursor cursor = mContentResolver.query(Phone.CONTENT_URI,// 要被检索的Uri
				new String[] { Phone._ID,// _ID 相同ID用来表示同一个人
						Phone.DISPLAY_NAME,// 姓名
						Phone.NUMBER,// 电话
						Phone.RAW_CONTACT_ID,// 区分联系人唯一标识
						RawContacts._ID },// 头像ID
				Phone.NUMBER + " like '%" + input + "%' or "// 电话号码模糊查找
						+ Phone.DISPLAY_NAME + " like '%" + input + "%'", // 姓名模糊查找
				null,// 检索条件对应值的键值队
				Phone.DISPLAY_NAME + " ASC");// 按姓名升序排序
		adapter.changeCursor(cursor);// 更改适配器游标
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		mRawId = cursor.getLong(cursor.getColumnIndex(Phone.RAW_CONTACT_ID));// RAW_CONTACT_ID用来唯一标识一个联系人
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
		case DIALOG_CALL_AND_SMS_MENU:// 长按联系人弹出选择通话、短息选项对话框
			return createCallDialog();
		case DIALOG_SHOW_MENU:// 按菜单键弹出菜单对话框
			return createShowMenuDialog();
		case DIALOG_DELETE_ALL_MENU:// 删除所有二次确认对话框
			return createDeleteAllDialog();
		default:
			return super.onCreateDialog(id);
		}
	}

	/** 删除所有二次确认对话框 */
	private Dialog createDeleteAllDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.txt_title_isdelall_address);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, this);
		return builder.create();
	}

	/** 按菜单键弹出菜单对话框 */
	private Dialog createShowMenuDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_show_menu_adress, null);
		/* LayoutInflater提供一个转换过程，把之前的xml布局文件转换成的要显示的类的实例，返回 View */
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
		builder.setView(view);// 把WIN对话框与view关联起来
		return builder.create();
	}

	/** 长按联系人弹出选择通话、短息选项对话框 */
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
		case CALL:// 打电话
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
		case SMS:// 发短信
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

		case DialogInterface.BUTTON_POSITIVE:// 删除所有联系人确定
		{
			// 删除所有
			deleteAllPeople();
		}
			break;
		case DialogInterface.BUTTON_NEGATIVE:// 取消
		{
			// 取消 Nothing to do!
		}
			break;
		default:
			break;
		}
	}


	/** 删除所有联系人 */
	private void deleteAllPeople() {
		Uri uri = ContactsContract.RawContacts.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,
						"true").build();
		mContentResolver.delete(uri, null, null);// RawContacts表-账户相关
		Toast.makeText(this,
				getString(R.string.txt_show_menu_deleteall_address),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_add_people_address:// 添加联系人
		{
			Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra("type", 1);
			startActivity(intent);
		}
			break;
		case R.id.btn_find_people_address:// 查找联系人
			if (View.GONE == mEdtInput.getVisibility()) {
				mEdtInput.setVisibility(View.VISIBLE);
				mEdtInput.requestFocus();// 使EditText获取焦点

			} else {
				mEdtInput.setVisibility(View.GONE);
				// mEdtInput.setFocusable(false);
			}
			break;
		case R.id.btn_call_people_address:// 通话记录
		{
			Intent intent = new Intent(this, CallsActivity.class);
			startActivity(intent);
			this.finish();
		}
			break;
		case R.id.btn_menu_people_address:// 菜单
		{
			showDialog(DIALOG_SHOW_MENU);
		}
			break;
		case R.id.btn_exit_people_address:// 退出
		{
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
			this.finish();
		}
			break;
		case R.id.edt_find_address:// EditText被点击会弹出软键盘
			mEdtInput.requestFocus();// 使EditText获取焦点
			break;

		case R.id.btn_show_menu_showall_address:// 显示所有
		{
			Cursor cursor = readDatas();
			adapter.changeCursor(cursor);
			mEdtInput.setText("");
			mEdtInput.setVisibility(View.GONE);
			dismissDialog(DIALOG_SHOW_MENU);
		}
			break;

		case R.id.btn_show_menu_deleteall_address:// 删除所有
		{
			dismissDialog(DIALOG_SHOW_MENU);
			showDialog(DIALOG_DELETE_ALL_MENU);
		}
			break;

		case R.id.btn_show_menu_back_address:// 后退
			dismissDialog(DIALOG_SHOW_MENU);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:// 菜单键
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
		/** 列序号-姓名 */
		final int INDEX_NAME;
		/** 列序号-电话号码 */
		final int INDEX_NUMBER;
		/** 列序号- */
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
			/* 把ID缀到Uri上，表示某个特定的ID，如果不缀表示一类，即所有 */
			Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, photoId);
			/*
			 * 获取头像Bitmap流 第一个参数：ContentResolver 第二个参数：包含ID的Uri 返回值为照片输入流
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
		/** ImageView-头像 */
		ImageView icon;
		/** TextView-姓名 */
		TextView name;
		/** TextView-电话 */
		TextView number;
	}

}
