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
 * �༭��ϵ�ˡ��½���ϵ��
 * 
 * @author Sogrey
 * 
 */
public class EditActivity extends BaseActivity implements OnClickListener,
		android.content.DialogInterface.OnClickListener {
	/** �½���ϵ�� */
	protected static final int NEW_ADD = 0x01;
	/** �༭��ϵ�� */
	protected static final int EDIT = 0x02;
	/** ContentResolver���ݽ����� ���� */
	protected ContentResolver mContentResolver;
	/**��ת����ҳ���Intent*/
	Intent intent;
	/**��ת����ҳ���Ŀ��-�༭������*/
	int mType;
	/** ָ����ϵ��ID */
	long mRaw;
	/** ҳ����� ���� */
	private TextView mTxtTitle;
	/** ͷ�� ���� */
	private ImageView mImgIcon;
	/** ���� ���� */
	private EditText mEdtName;
	/** �ֻ� ���� */
	private EditText mEdtNumber;
	/** ��ַ ���� */
	private EditText mEdtAddress;
	/** E-mail ���� */
	private EditText mEdtEmail;
	/** QQ ���� */
	private EditText mEdtQq;
	/** ��ע ���� */
	private EditText mEdtNote;
	/** ��ť���� */
	private Button mBtnSave;
	/** ��ťɾ�� */
	private Button mBtnDelete;
	/** ��ť���� */
	private Button mBtnBack;
	/** �޸���ϵ��ǰ���ȼ��������ϵ�˵������Ϣ�Ƿ����(Ϊ��) */
	private boolean mIsEmptyName = false, mIsEmptyNumber = false,
			mIsEmptyEmail = false, mIsEmptyPostCode = false,
			mIsEmptyQq = false, mIsEmptyNote = false;
	/** �޸�ǰ��ϵ������ */
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
		case NEW_ADD:// �����ϵ��
			// Nothing to do!
			break;
		case EDIT:// �༭��ϵ��
			initDatas();
			break;

		default:
			break;
		}
	}

	/** ��ʼ��View��� */
	private void initViews() {
		mTxtTitle = (TextView) findViewById(R.id.txt_title_page);
		/* ����ҳ����� */
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
//		case NEW_ADD:// �����ϵ��
//			mBtnDelete.setVisibility(View.GONE);
//			break;
//		case EDIT:// �༭��ϵ��
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

	/** ��ʼ������-������ */
	private void initDatas() {
		mRaw = intent.getLongExtra("raw", -1);
		Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, mRaw);
		uri = Uri.withAppendedPath(uri, Contacts.Data.CONTENT_DIRECTORY);
		Cursor cursor = mContentResolver.query(uri, null, null, null, null);

		int indexMimetype = cursor.getColumnIndex(Contacts.Data.MIMETYPE);//
		while (cursor.moveToNext()) {
			String mime = cursor.getString(indexMimetype);
			if (Phone.CONTENT_ITEM_TYPE.equals(mime)) {// �������绰
				int indexName = cursor.getColumnIndex(Phone.DISPLAY_NAME);
				mName = cursor.getString(indexName);
				mEdtName.setText(mName);
				int indexNumber = cursor.getColumnIndex(Phone.NUMBER);
				mEdtNumber.setText(cursor.getString(indexNumber));
				if (cursor.getString(indexName) == null) {// ����޸�ǰ�绰�Ƿ�Ϊ��
					mIsEmptyName = true;
				}
				if (cursor.getString(indexNumber) == null) {// ����޸�ǰ�绰�Ƿ�Ϊ��
					mIsEmptyNumber = true;
				}
			}
			if (RawContacts.CONTENT_ITEM_TYPE.equals(mime)) {// ͷ��
				int indexIcon = cursor.getColumnIndex(RawContacts._ID);
				long photoId = cursor.getLong(indexIcon);
				/* ��ID׺��Uri�ϣ���ʾĳ���ض���ID�������׺��ʾһ�࣬������ */
				Uri uriIcon = ContentUris.withAppendedId(Contacts.CONTENT_URI,
						photoId);
				/*
				 * ��ȡͷ��Bitmap�� ��һ��������ContentResolver �ڶ�������������ID��Uri ����ֵΪ��Ƭ������
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
				if (cursor.getString(indexEmail) == null) {// ����޸�ǰEmail�Ƿ�Ϊ��
					mIsEmptyEmail = true;
				}
			}
			if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mime)) {// ��ַ
				int indexAddress = cursor
						.getColumnIndex(StructuredPostal.POSTCODE);
				mEdtAddress.setText(cursor.getString(indexAddress));
				if (cursor.getString(indexAddress) == null) {// ����޸�ǰ��ַ�Ƿ�Ϊ��
					mIsEmptyPostCode = true;
				}
			}
			if (Im.CONTENT_ITEM_TYPE.equals(mime)) {// ��ʱ����
				// int indexProtocol = cursor.getColumnIndex(Im.PROTOCOL);
				int indexQq = cursor.getColumnIndex(Im.DATA);
				mEdtQq.setText(cursor.getString(indexQq));
				if (cursor.getString(indexQq) == null) {// ����޸�ǰQQ�Ƿ�Ϊ��
					mIsEmptyQq = true;
				}
			}
			if (Note.CONTENT_ITEM_TYPE.equals(mime)) {// ��ע
				int indexNote = cursor.getColumnIndex(Note.NOTE);
				mEdtNote.setText(cursor.getString(indexNote));
				if (cursor.getString(indexNote) == null) {// ����޸�ǰ��ע�Ƿ�Ϊ��
					mIsEmptyNote = true;
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_add_save_address:// ����
			switch (mType) {
			case NEW_ADD:// �½���ϵ�˱���
				savePeople();// �½���ϵ��-newInsert
				break;
			case EDIT:// �༭��ϵ�˱���
				editPeople();// �༭��ϵ��-upDate
				break;

			default:
				break;
			}
			break;
		case R.id.btn_add_delete_address:// ɾ��
			deleteOne();
			break;
		case R.id.btn_add_back_address:// ����
			back2Adress();
			break;

		default:
			break;
		}
	}

	/** ɾ����ǰ��ϵ�� */
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
		case DialogInterface.BUTTON_POSITIVE:// ȷ��ɾ��
			mContentResolver.delete(ContactsContract.RawContacts.CONTENT_URI,
					ContactsContract.RawContacts._ID + "=?",
					new String[] { mRaw + "" });

			Toast.makeText(this,
					getString(R.string.txt_deleted_address, mName),
					Toast.LENGTH_LONG).show();
			back2Adress();
			break;
		case DialogInterface.BUTTON_NEGATIVE:// ȡ��ɾ��
			// Noting to do��
		default:
			break;
		}
	}

	/** �༭��ϵ�� */
	private void editPeople() {

		ContentValues values = new ContentValues();
		values.clear();
		/*---����---*/
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

		/*---�绰---*/
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
		/*---��ַ---*/
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
		/*---��ע---*/
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

	/** �½���ϵ�˳�ʼ����Ϊ�� */
	private void savePeople() {
		// ���������ļ���,ʹ������
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		// ���������е�ÿһ���������
		ContentProviderOperation operationRaw = ContentProviderOperation
				.newInsert(RawContacts.CONTENT_URI)// RawContacts��-�˻����
				.withValue(RawContacts.ACCOUNT_NAME, null)// �˻���Ϊnull
				.build();// ����RawContacts.CONTENT_URI��builder����
		operations.add(operationRaw);

		// ���������Ϣ
		/*--����--*/
		ContentProviderOperation operationName = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts��-�˻����
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// ���õ�0�β����ķ���ֵ��ǰ�������˻�ID������Ϊ���β���Idֵ
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)// �����������ͣ�����Ҫʹ��MIMEytpeId
																			// ֱ�Ӵ�����Ӧ�ַ���
				.withValue(StructuredName.DISPLAY_NAME,
						mEdtName.getText().toString())// ��������
				.build();// ����RawContacts.CONTENT_URI��builder����
		operations.add(operationName);
		/*--�绰--*/
		ContentProviderOperation operationPhone = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts��-�˻����
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// ���õ�0�β����ķ���ֵ��ǰ�������˻�ID������Ϊ���β���Idֵ
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)// �����������ͣ�����Ҫʹ��MIMEytpeId
																	// ֱ�Ӵ�����Ӧ�ַ���
				.withValue(Phone.NUMBER, mEdtNumber.getText().toString())// ����绰����
				.withValue(Phone.TYPE, Phone.TYPE_MOBILE)// �绰����-�ƶ��绰

				.build();// ����RawContacts.CONTENT_URI��builder����
		operations.add(operationPhone);
		/*--��ַ--*/
		ContentProviderOperation operationPostcode = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts��-�˻����
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// ���õ�0�β����ķ���ֵ��ǰ�������˻�ID������Ϊ���β���Idֵ
				.withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)// �����������ͣ�����Ҫʹ��MIMEytpeId
				// ֱ�Ӵ�����Ӧ�ַ���
				.withValue(StructuredPostal.POSTCODE,
						mEdtAddress.getText().toString())// �ʱ�
				.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME)// �ʱ�����-��ͥ�ʱ�

				.build();// ����RawContacts.CONTENT_URI��builder����
		operations.add(operationPostcode);// ����ʱ�
		/*--E-mail--*/
		ContentProviderOperation operationEmail = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts��-�˻����
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// ���õ�0�β����ķ���ֵ��ǰ�������˻�ID������Ϊ���β���Idֵ
				.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)// �����������ͣ�����Ҫʹ��MIMEytpeId
				// ֱ�Ӵ�����Ӧ�ַ���
				.withValue(Email.ADDRESS, mEdtEmail.getText().toString())// E-mail��ַ
				.withValue(Email.TYPE, Email.TYPE_HOME)// E-mail��ַ����-��ͥ E-mail��ַ

				.build();// ����RawContacts.CONTENT_URI��builder����
		operations.add(operationEmail);// ���E-mail��ַ
		/*--QQ--*/
		ContentProviderOperation operationQq = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts��-�˻����
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// ���õ�0�β����ķ���ֵ��ǰ�������˻�ID������Ϊ���β���Idֵ
				.withValue(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE)// �����������ͣ�����Ҫʹ��MIMEytpeId
				// ֱ�Ӵ�����Ӧ�ַ���
				.withValue(Im.DATA, mEdtQq.getText().toString())// QQ����
				.withValue(Im.TYPE, Im.PROTOCOL_QQ)// IM����- QQ����

				.build();// ����RawContacts.CONTENT_URI��builder����
		operations.add(operationQq);// ���QQ����
		/*--��ע--*/
		ContentProviderOperation operationNote = ContentProviderOperation
				.newInsert(Data.CONTENT_URI)// RawContacts��-�˻����
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)// ���õ�0�β����ķ���ֵ��ǰ�������˻�ID������Ϊ���β���Idֵ
				.withValue(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)// �����������ͣ�����Ҫʹ��MIMEytpeId
				// ֱ�Ӵ�����Ӧ�ַ���
				.withValue(Note.NOTE, mEdtNote.getText().toString())// ��ע
				.build();// ����RawContacts.CONTENT_URI��builder����
		operations.add(operationNote);// ��ӱ�ע

		if (!TextUtils.isEmpty(mEdtName.getText().toString())) {
			// ִ����������
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

	/** ����*/
	private void back2Adress() {
		Intent intent = new Intent(this, AddressActivity.class);
		startActivity(intent);
		this.finish();
	}

}
