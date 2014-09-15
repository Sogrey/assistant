package edu.feicui.assistant.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.util.Constants;
import edu.feicui.assistant.util.FileUtil;

/**
 * ��ʾ��ƬԤ���������ɾ������
 * 
 * @author Sogrey
 * 
 */
public class ConfirmActivity extends BaseActivity implements OnClickListener {
	/** ��ʾ��Ƭ�õ�ImageView */
	protected ImageView mImgShowImage;
	/** ɾ�������水ť */
	protected Button mBtnDelete, mBtnSave;
	/** ��ת����Activity��Intent */
	protected Intent intent;
	/** ��ʱͼƬURI */
	protected Uri mUri;
	/** ����ͼƬ�ļ�·�� */
	protected static final String PATH_OF_FILENAME = Constants.PATH_SDCARD
			+ "/assistant/camera/";
	/** ����ͼƬ�ļ���׺�� */
	protected static final String SUFFIX_OF_FILENAME = ".jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_savepic_camera);
		intent = getIntent();
		mUri = intent.getData();
		initViews();
	}

	private void initViews() {
		mImgShowImage = (ImageView) findViewById(R.id.img_showpic_camera);
		mImgShowImage.setImageURI(mUri);
		mBtnDelete = (Button) findViewById(R.id.btn_showpic_delete_camera);
		mBtnDelete.setOnClickListener(this);
		mBtnSave = (Button) findViewById(R.id.btn_showpic_save_camera);
		mBtnSave.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		/*
		 * �ļ�·����File:///sdcard/xxx/xxx.jpg<br> mUri.getPath() �õ��ľ����ļ�·��
		 * /sdcard/xxx/xxx.jpg<br>
		 */
		File file = new File(mUri.getPath());
		int id = v.getId();
		switch (id) {
		case R.id.btn_showpic_save_camera:// ������Ƭ
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.FMT_FILENAME);
			String fileName = sdf.format(new Date());
			String filePath = PATH_OF_FILENAME + fileName + SUFFIX_OF_FILENAME;
			File dst = new File(filePath);
			dst.getParentFile().mkdirs();// �����ļ��У����ļ����ϼ�Ŀ¼������
			FileUtil.copyFile(file, dst);
			Toast.makeText(this, getString(R.string.saveto_camera) + filePath,
					Toast.LENGTH_LONG).show();
		case R.id.btn_showpic_delete_camera:// ɾ����Ƭ
			file.delete();// ɾ���ļ�
			Toast.makeText(this, getString(R.string.delete_temp_camera),
					Toast.LENGTH_SHORT).show();
			finish();
			break;
		default:
			break;
		}
	}
}
