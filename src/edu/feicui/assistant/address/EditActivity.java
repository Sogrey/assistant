package edu.feicui.assistant.address;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;

/**
 * 编辑联系人、新建联系人
 * 
 * @author Sogrey
 * 
 */
public class EditActivity extends BaseActivity implements OnClickListener,
		android.content.DialogInterface.OnClickListener {
	/** 新建联系人 */
	protected static final int NEW_ADD = 0x01;
	/** 编辑联系人 */
	protected static final int EDIT = 0x02;
	/** ContentResolver内容解析器 对象 */
	protected ContentResolver mContentResolver;
	/**跳转到本页面的Intent*/
	Intent intent;
	/**跳转到本页面的目的-编辑、新增*/
	int mType;
	/** 指定联系人ID */
	long mRaw;
	/** 页面标题 对象 */
	private TextView mTxtTitle;
	/** 头像 对象 */
	private ImageView mImgIcon;
	/** 姓名 对象 */
	private EditText mEdtName;
	/** 手机 对象 */
	private EditText mEdtNumber;
	/** 地址 对象 */
	private EditText mEdtAddress;
	/** E-mail 对象 */
	private EditText mEdtEmail;
	/** QQ 对象 */
	private EditText mEdtQq;
	/** 备注 对象 */
	private EditText mEdtNote;
	/** 按钮保存 */
	private Button mBtnSave;
	/** 按钮删除 */
	private Button mBtnDelete;
	/** 按钮返回 */
	private Button mBtnBack;
	/** 修改联系人前，先检查已有联系人的相关信息是否存在(为空) */
	private boolean mIsEmptyName = false, mIsEmptyNumber = false,
			mIsEmptyEmail = false, mIsEmptyPostCode = false,
			mIsEmptyQq = false, mIsEmptyNote = false;
	/** 修改前联系人姓名 */
	private String mName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_address);
		intent = getIntent();
		mContentResolver = getContentResolver();
		initViews();
		mType = intent.getIntExtra("type", 0);
		switch (mType) {
		case NEW_ADD:// 添加联系人
			// Nothing to do!
			break;
		case EDIT:// 编辑联系人
			initDatas();
			break;

		default:
			break;
		}
	}

	/** 初始化View组件 */
	private void initViews() {
		mTxtTitle = (TextView) findViewById(R.id.txt_title_page);
		/* 设置页面标题 */
		mTxtTitle.setText(getTitle());
		mImgIcon = (ImageView) findViewById(R.id.icon_add_icon_address);
		mEdtName = (EditText) findViewById(R.id.edt_add_name_address);
		mEdtNumber = (EditText) findViewById(R.id.edt_add_number_address);
		mEdtAddress = (EditText) findViewById(R.id.edt_add_address_address);
		mEdtEmail = (EditText) findViewById(R.id.edt_add_email_address);
		mEdtQq = (EditText) findViewById(R.id.edt_add_qq_address);
		mEdtNote = (EditText) findViewById(R.id.edt_add_note_address);
		mBtnSave = (Button) findViewById(R.id.btn_add_save_address);
		mBtnSave.setOnClickListener(this);
		mBtnDelete = (Button) findViewById(R.id.btn_add_delete_address);
//		switch (mType) {
//		case NEW_ADD:// 添加联系人
//			mBtnDelete.setVisibility(View.GONE);
//			break;
//		case EDIT:// 编辑联系人
//			mBtnDelete.setVisibility(View.VISIBLE);
//			mBtnDelete.setOnClickListener(this);
//			break;
//android:visibility="gone"
//		default:
//			break;
//		}
		mBtnBack = (Button) findViewById(R.id.btn_add_back_address);
		mBtnBack.setOnClickListener(this);
	}

	/** 初始化数据-读数据 */
	private void initDatas() {
		mRaw = intent.getLongExtra("raw", -1);
		Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, mRaw);
		uri = Uri.withAppendedPath(uri, Contacts.Data.CONTENT_DIRECTORY);
		Cursor cursor = mContentResolver.query(uri, null, null, null, null);

		int indexMimetype = cursor.getColumnIndex(Contacts.Data.MIMETYPE);//
		while (cursor.moveToNext()) {
			String mime = cursor.getString(indexMimetype);
			if (Phone.CONTENT_ITEM_TYPE.equals(mime)) {// 姓名、电话
				int indexName = cursor.getColumnIndex(Phone.DISPLAY_NAME);
				mName = cursor.getString(indexName);
				mEdtName.setText(mName);
				int indexNumber = cursor.getColumnIndex(Phone.NUMBER);
				mEdtNumber.setText(cursor.getString(indexNumber));
				if (cursor.getString(indexName) == null) {// 检查修改前电话是否为空
					mIsEmptyName = true;
				}
				if (cursor.getString(indexNumber) == null) {// 检查修改前电话是否为空
					mIsEmptyNumber = true;
				}
			}
			if (RawContacts.CONTENT_ITEM_TYPE.equals(mime)) {// 头像
				int indexIcon = cursor.getColumnIndex(RawContacts._ID);
				long photoId = cursor.getLong(indexIcon);
				/* 把ID缀到Uri上，表示某个特定的ID，如果不缀表示一类，即所有 */
				Uri uriIcon = ContentUris.withAppendedId(Contacts.CONTENT_URI,
						photoId);
				/*
				 * 获取头像Bitmap流 第一个参数：ContentResolver 第二个参数：包含ID的Uri 返回值为照片输入流
				 */
				InputStream input = Contacts.openContactPhotoInputStream(
						mContentResolver, uri);
				if (input != null) {
					Bitmap bmp = BitmapFactory.decodeStream(input);
					mImgIcon.setImageBitmap(bmp);
				} else {
					mImgIcon.setImageResource(R.drawable.icon_tab_user_software);
				}
			}
			if (Email.CONTENT_ITEM_TYPE.equals(mime)) {// E-mail
				int indexEmail = cursor.getColumnIndex(Email.ADDRESS);
				mEdtEmail.setText(cursor.getString(indexEmail));
				if (cursor.getString(indexEmail) == null) {// 检查修改前Email是否为空
					mIsEmptyEmail = true;
				}
			}
			if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mime)) {// 地址
				int indexAddress = cursor
						.getColumnIndex(StructuredPostal.POSTCODE);
				mEdtAddress.setText(cursor.getString(indexAddress));
				if (cursor.getString(indexAddress) == null) {// 检查修改前地址是否为空
					mIsEmptyPostCode = true;
				}
			}
			if (Im.CONTENT_ITEM_TYPE.equals(mime)) {// 即时聊天
				// int indexProtocol = cursor.getColumnIndex(Im.PROTOCOL);
				int indexQq = cursor.getColumnIndex(Im.DATA);
				mEdtQq.setText(cursor.getString(indexQq));
				if (cursor.getString(indexQq) == null) {// 检查修改前QQ是否为空
					mIsEmptyQq = true;
				}
			}
			if (Note.CONTENT_ITEM_TYPE.equals(mime)) {// 备注
				int indexNote = cursor.getColumnIndex(Note.NOTE);
				mEdtNote.setText(cursor.getString(indexNote));
				if (cursor.getString(indexNote) == null) {// 检查修改前备注是否为空
					mIsEmptyNote = true;
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_add_save_address:// 保存
			switch (mType) {
			case NEW_ADD:// 新建联系人保存
				savePeople();// 新建联系人-newInsert
				break;
			case EDIT:// 编辑联系人保存
				editPeople();// 编辑联系人-upDate
				break;

			default:
				break;
			}
			break;
		case R.id.btn_add_delete_address:// 删除
			deleteOne();
			break;
		case R.id.btn_add_back_address:// 返回
			back2Adress();
			break;

		default:
			break;
		}
	}

	/** 删除当前联系人 */
	private void deleteOne() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.txt_title_isdelthis_address);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, this);
		builder.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:// 确定删除
			mContentResolver.delete(ContactsContract.RawContacts.CONTENT_URI,
					ContactsContract.RawContacts._ID + "=?",
					new String[] { mRaw + "" });

			Toast.makeText(this,
					getString(R.string.txt_deleted_address, mName),
					Toast.LENGTH_LONG).show();
			back2Adress();
			break;
		case DialogInterface.BUTTON_NEGATIVE:// 取消删除
			// Noting to do！
		default:
			break;
		}
	}

	/** 编辑联系人 */
	private void editPeople() {

		ContentValues values = new ContentValues();
		values.clear();
		/*---姓名---*/
		values.put(StructuredName.GIVEN_NAME, mEdtName.getText().toString());
		if (!mIsEmptyName) {
			mContentResolver
					.update(Data.CONTENT_URI, values,
							// " mimetype=? and raw_contact_id=?"
							Data.MIMETYPE + " =? and " + Data.RAW_CONTACT_ID
									+ " =? ",
							new String[] { StructuredName.CONTENT_ITEM_TYPE,
									mRaw + "" });
		} else {
			values.put(Data.RAW_CONTACT_ID, mRaw);
			values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
			mContentResolver.insert(Data.CONTENT_URI, values);
		}
		values.clear();

		/*---电话---*/
		values.put(Phone.NUMBER, mEdtNumber.getText().toString());
		if (!mIsEmptyNumber) {
			mContentResolver.update(Data.CONTENT_URI, values, Data.MIMETYPE
					+ " =? and " + Data.RAW_CONTACT_ID + " =?  and "
					+ Phone.TYPE + " =?",
					new String[] { Phone.CONTENT_ITEM_TYPE, mRaw + "",
							Phone.TYPE_MOBILE + "" });
		} else {
			values.put(Data.RAW_CONTACT_ID, mRaw);
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			values.put(Phone.TYPE, Phone.TYPE_MOBILE);
			mContentResolver.insert(Data.CONTENT_URI, values);

		}
		values.clear();
		/*---地址---*/
		values.put(StructuredPostal.POSTCODE, mEdtAddress.getText().toString());
		if (!mIsEmptyPostCode) {
			mContentResolver.update(Data.CONTENT_URI, values, Data.MIMETYPE
					+ " =? and " + Data.RAW_CONTACT_ID + " =?  ", new String[] {
					StructuredPostal.CONTENT_ITEM_TYPE, mRaw + "", });

		} else {

			values.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
			values.put(Data.RAW_CONTACT_ID, mRaw);
			values.put(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME);
			mContentResolver.insert(Data.CONTENT_URI, values);
		}

		values.clear();
		/*---E-mail---*/
		values.put(Email.ADDRESS, mEdtEmail.getText().toString());// API11
		if (!mIsEmptyEmail) {
			mContentResolver.update(Data.CONTENT_URI, values, Data.MIMETYPE
					+ " =? and " + Data.RAW_CONTACT_ID + " =? ", new String[] {
					Email.CONTENT_ITEM_TYPE, mRaw + "" });

		} else {

			values.put(Data.RAW_CONTACT_ID, mRaw);
			values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
			mContentResolver.insert(Data.CONTENT_URI, values);
		}
		values.clear();
		/*---QQ---*/
		values.put(Im.DATA, mEdtQq.getText().toString());
		if (!mIsEmptyQq) {
			mContentResolver.update(Data.CONTENT_URI, values, Data.MIMETYPE
					+ " =? and " + Data.RAW_CONTACT_ID + " =? ", new String[] {
					Im.CONTENT_ITEM_TYPE, mRaw + "" });
		} else {

			values.put(Data.RAW_CONTACT_ID, mRaw);
			values.put(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);
			values.put(Im.PROTOCOL, Im.DATA);
			mContentResolver.insert(Data.CONTENT_URI, values);
		}
		values.clear();
		/*---备注---*/
		values.put(Note.NOTE, mEdtNote.getText().toString());
		if (!mIsEmptyNote) {
			mContentResolver.update(Data.CONTENT_URI, values, Data.MIMETYPE
					+ " =? and " + Data.RAW_CONTACT_ID + " =? ", new String[] {
					Note.CONTENT_ITEM_TYPE, mRaw + "" });
		} else {

			values.put(Data.RAW_CONTACT_ID, mRaw);
			values.put(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE);
			mContentResolver.insert(Data.CONTENT_URI, values);
		}
		Toast.makeText(
				this,
				getString(R.string.btn_edit_people_success_address, mEdtName
						.getText().toString()), Toast.LENGTH_SHORT).show();
		back2Adress();
	}

	/** 新建联系人初始数据为空 */
	private void savePeople() {
		// 批量操作的集合,使用事务
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		// 批量操作中的每一个具体操作
		ContentProviderOperation operationRaw = ContentProviderOperation
				.newInsert(RawContacts.CONTENT_URI)// RawContacts表-账户相关
				.withValue(RawContacts.ACCOUNT_NAME, null)// 账户名为null
				.build();// 创建RawContacts.CONTENT_URI的builder对象
		operations.add(operationRaw);

		// 插入具体信息
		/*--姓名--*/
		ContentProviderOperation operationName = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts表-账户相关
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// 引用第0次操作的返回值（前面插入的账户ID），作为本次操作Id值
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)// 设置数据类型，不需要使用MIMEytpeId
																			// 直接传入相应字符串
				.withValue(StructuredName.DISPLAY_NAME,
						mEdtName.getText().toString())// 插入姓名
				.build();// 创建RawContacts.CONTENT_URI的builder对象
		operations.add(operationName);
		/*--电话--*/
		ContentProviderOperation operationPhone = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts表-账户相关
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// 引用第0次操作的返回值（前面插入的账户ID），作为本次操作Id值
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)// 设置数据类型，不需要使用MIMEytpeId
																	// 直接传入相应字符串
				.withValue(Phone.NUMBER, mEdtNumber.getText().toString())// 插入电话号码
				.withValue(Phone.TYPE, Phone.TYPE_MOBILE)// 电话类型-移动电话

				.build();// 创建RawContacts.CONTENT_URI的builder对象
		operations.add(operationPhone);
		/*--地址--*/
		ContentProviderOperation operationPostcode = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts表-账户相关
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// 引用第0次操作的返回值（前面插入的账户ID），作为本次操作Id值
				.withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)// 设置数据类型，不需要使用MIMEytpeId
				// 直接传入相应字符串
				.withValue(StructuredPostal.POSTCODE,
						mEdtAddress.getText().toString())// 邮编
				.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME)// 邮编类型-家庭邮编

				.build();// 创建RawContacts.CONTENT_URI的builder对象
		operations.add(operationPostcode);// 添加邮编
		/*--E-mail--*/
		ContentProviderOperation operationEmail = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts表-账户相关
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// 引用第0次操作的返回值（前面插入的账户ID），作为本次操作Id值
				.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)// 设置数据类型，不需要使用MIMEytpeId
				// 直接传入相应字符串
				.withValue(Email.ADDRESS, mEdtEmail.getText().toString())// E-mail地址
				.withValue(Email.TYPE, Email.TYPE_HOME)// E-mail地址类型-家庭 E-mail地址

				.build();// 创建RawContacts.CONTENT_URI的builder对象
		operations.add(operationEmail);// 添加E-mail地址
		/*--QQ--*/
		ContentProviderOperation operationQq = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts表-账户相关
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// 引用第0次操作的返回值（前面插入的账户ID），作为本次操作Id值
				.withValue(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE)// 设置数据类型，不需要使用MIMEytpeId
				// 直接传入相应字符串
				.withValue(Im.DATA, mEdtQq.getText().toString())// QQ号码
				.withValue(Im.TYPE, Im.PROTOCOL_QQ)// IM类型- QQ号码

				.build();// 创建RawContacts.CONTENT_URI的builder对象
		operations.add(operationQq);// 添加QQ号码
		/*--备注--*/
		ContentProviderOperation operationNote = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts表-账户相关
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// 引用第0次操作的返回值（前面插入的账户ID），作为本次操作Id值
				.withValue(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)// 设置数据类型，不需要使用MIMEytpeId
				// 直接传入相应字符串
				.withValue(Note.NOTE, mEdtNote.getText().toString())// 备注
				.build();// 创建RawContacts.CONTENT_URI的builder对象
		operations.add(operationNote);// 添加备注

		if (!TextUtils.isEmpty(mEdtName.getText().toString())) {
			// 执行批量操作
			try {
				mContentResolver.applyBatch(ContactsContract.AUTHORITY,
						operations);
				Toast.makeText(
						this,
						getString(R.string.btn_add_people_success_address,
								mEdtName.getText().toString()),
						Toast.LENGTH_SHORT).show();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
			back2Adress();
		} else {
			Toast.makeText(this, getString(R.string.txt_no_name_address),
					Toast.LENGTH_SHORT).show();
		}
	}

	/** 返回*/
	private void back2Adress() {
		Intent intent = new Intent(this, AddressActivity.class);
		startActivity(intent);
		this.finish();
	}

}
