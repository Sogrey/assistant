package edu.feicui.assistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建表，表升级
 * 
 * @author Sogrey
 * 
 */
public class AddressDBHelper extends SQLiteOpenHelper {

	/** 数据库名 */
	private final static String DB_NAME = "people.db";
	/** 数据库版本号 */
	public final static int DB_VERSION = 1;
	/** 电话表名 */
	public final static String TABLE_ADDRESS = "address";
	/** 电话列-ID */
	public final static String COLUMN_ADDRESS_ID = "_id";
	/** 电话列-姓名 */
	public final static String COLUMN_ADDRESS_NAME = "name";
	/** 电话列-电话 */
	public final static String COLUMN_ADDRESS_PHONE = "phone";

	/** 短信表名 */
	public final static String TABLE_SMS = "sms";
	/** 短信列-ID */
	public final static String COLUMN_SMS_ID = "_id";
	/** 短信列-姓名 */
	public final static String COLUMN_SMS_NAME = "name";
	/** 短信列-电话 */
	public final static String COLUMN_SMS_PHONE = "phone";
	/** 短信列-短信内容 */
	public final static String COLUMN_SMS_CONTENT = "content";

	public AddressDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * 创建表
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 *      .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlTel = "CREATE TABLE " + TABLE_ADDRESS + " ( " // 表名
				+ COLUMN_ADDRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // ID
				+ COLUMN_ADDRESS_NAME + " TEXT, " // 姓名
				+ COLUMN_ADDRESS_PHONE + " TEXT )"; // 电话
		db.execSQL(sqlTel);// 执行SQL语句，创建表（返回值为空）
		String sqlSms = "CREATE TABLE " + TABLE_SMS + " ( " // 短信表名
				+ COLUMN_SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // 短信ID
				+ COLUMN_SMS_NAME + " TEXT, " // 短信姓名
				+ COLUMN_SMS_PHONE + " TEXT, " // 短信电话号码
				+ COLUMN_SMS_CONTENT + " TEXT )"; // 短信内容
		db.execSQL(sqlSms);// 执行SQL语句，创建表（返回值为空）
	}

	/**
	 * 升级表
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 *      .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
