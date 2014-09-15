package edu.feicui.assistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ������������
 * 
 * @author Sogrey
 * 
 */
public class AddressDBHelper extends SQLiteOpenHelper {

	/** ���ݿ��� */
	private final static String DB_NAME = "people.db";
	/** ���ݿ�汾�� */
	public final static int DB_VERSION = 1;
	/** �绰���� */
	public final static String TABLE_ADDRESS = "address";
	/** �绰��-ID */
	public final static String COLUMN_ADDRESS_ID = "_id";
	/** �绰��-���� */
	public final static String COLUMN_ADDRESS_NAME = "name";
	/** �绰��-�绰 */
	public final static String COLUMN_ADDRESS_PHONE = "phone";

	/** ���ű��� */
	public final static String TABLE_SMS = "sms";
	/** ������-ID */
	public final static String COLUMN_SMS_ID = "_id";
	/** ������-���� */
	public final static String COLUMN_SMS_NAME = "name";
	/** ������-�绰 */
	public final static String COLUMN_SMS_PHONE = "phone";
	/** ������-�������� */
	public final static String COLUMN_SMS_CONTENT = "content";

	public AddressDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * ������
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 *      .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlTel = "CREATE TABLE " + TABLE_ADDRESS + " ( " // ����
				+ COLUMN_ADDRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // ID
				+ COLUMN_ADDRESS_NAME + " TEXT, " // ����
				+ COLUMN_ADDRESS_PHONE + " TEXT )"; // �绰
		db.execSQL(sqlTel);// ִ��SQL��䣬����������ֵΪ�գ�
		String sqlSms = "CREATE TABLE " + TABLE_SMS + " ( " // ���ű���
				+ COLUMN_SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // ����ID
				+ COLUMN_SMS_NAME + " TEXT, " // ��������
				+ COLUMN_SMS_PHONE + " TEXT, " // ���ŵ绰����
				+ COLUMN_SMS_CONTENT + " TEXT )"; // ��������
		db.execSQL(sqlSms);// ִ��SQL��䣬����������ֵΪ�գ�
	}

	/**
	 * ������
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 *      .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
