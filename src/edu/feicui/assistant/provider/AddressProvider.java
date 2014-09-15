/**
 * 
 */
package edu.feicui.assistant.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import edu.feicui.assistant.db.AddressDBHelper;

/**
 * 
 * @author Sogrey
 * 
 */
public class AddressProvider extends ContentProvider {
	protected static final String AUTHORITIES = "edu.feicui.assistant.provider.ADDRESS";
	protected static final String PATH_ADDRESS = "address";
	protected static final String PATH_SMS = "sms";
	protected static final int CODE_ADDRESS = 0x01;
	protected static final int CODE_SMS = 0x02;

	SQLiteDatabase mDb;
	UriMatcher mUriMatcher;

	/**
	 * AddressProvider 啟動時調用
	 * 
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		/* getContext()获取上下文对象，上下文对象可以操作所有的资源 */
		AddressDBHelper dbHelper = new AddressDBHelper(getContext());
		mDb = dbHelper.getWritableDatabase();
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITIES, PATH_ADDRESS, CODE_ADDRESS);
		mUriMatcher.addURI(AUTHORITIES, PATH_SMS, CODE_SMS);
		return true;
	}

	/**
	 * 查
	 * 
	 * @param uri
	 *            遥操作的数据
	 * @param projection
	 *            列
	 * @param selection
	 *            删除依据的条件
	 * @param selectionArgs
	 *            删除依据的条件的值
	 * @param sortOrder
	 *            查询结果的排序方式
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 *      java.lang.String[], java.lang.String, java.lang.String[],
	 *      java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CODE_ADDRESS:
			return mDb.query(AddressDBHelper.TABLE_ADDRESS, projection,
					selection, selectionArgs, null, null, sortOrder);
		case CODE_SMS:
			return mDb.query(AddressDBHelper.TABLE_SMS, projection, selection,
					selectionArgs, null, null, sortOrder);
		default:
			return null;
		}
	}

	/*
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CODE_ADDRESS:
			/*
			 * vnd.android.cursor.dir/ 表示安卓游标多条数据大集合 PATH_ADDRESS 是我们自定义的具体Uri类型
			 * vnd.android.cursor.item/ 表示安卓游标多条数据具体某一条
			 */
			return "vnd.android.cursor.dir/" + PATH_ADDRESS;
		case CODE_SMS:
			return "vnd.android.cursor.dir/" + PATH_SMS;
		default:
			return null;
		}
	}

	/**
	 * 增
	 * 
	 * @param uri
	 *            遥操作的数据
	 * @param values
	 *            要插入的数据键值对(不能为空)
	 * @return Uri
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 *      android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CODE_ADDRESS: {
			long id = mDb.insert(AddressDBHelper.TABLE_ADDRESS, "", values);
			return Uri.parse("content://" + AUTHORITIES + "/" + PATH_ADDRESS
					+ "/" + id);
		}
		case CODE_SMS: {
			long id = mDb.insert(AddressDBHelper.TABLE_SMS, "", values);
			return Uri.parse("content://" + AUTHORITIES + "/" + PATH_SMS + "/"
					+ id);
		}
		default:
			return null;
		}
	}

	/**
	 * 刪
	 * 
	 * @param uri
	 *            要操作的数据
	 * @param selection
	 *            删除依据的条件
	 * @param selectionArgs
	 *            删除依据的条件的值
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 *      java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CODE_ADDRESS:
			return mDb.delete(AddressDBHelper.TABLE_ADDRESS, selection,
					selectionArgs);
		case CODE_SMS:
			return mDb.delete(AddressDBHelper.TABLE_SMS, selection,
					selectionArgs);
		default:
			return 0;
		}
	}

	/**
	 * 改
	 * 
	 * @param uri
	 *            遥操作的数据
	 * @param values
	 *            要插入的数据键值对(不能为空)
	 * @param selection
	 *            删除依据的条件
	 * @param selectionArgs
	 *            删除依据的条件的值
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 *      android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CODE_ADDRESS:
			return mDb.update(AddressDBHelper.TABLE_ADDRESS, values, selection,
					selectionArgs);
		case CODE_SMS:
			return mDb.update(AddressDBHelper.TABLE_SMS, values, selection,
					selectionArgs);
		default:
			return 0;
		}
	}
}
