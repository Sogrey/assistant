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
 * 显示相片预览并保存或删除操作
 * 
 * @author Sogrey
 * 
 */
public class ConfirmActivity extends BaseActivity implements OnClickListener {
	/** 显示相片用的ImageView */
	protected ImageView mImgShowImage;
	/** 删除、保存按钮 */
	protected Button mBtnDelete, mBtnSave;
	/** 跳转到本Activity的Intent */
	protected Intent intent;
	/** 临时图片URI */
	protected Uri mUri;
	/** 储存图片文件路径 */
	protected static final String PATH_OF_FILENAME = Constants.PATH_SDCARD
			+ "/assistant/camera/";
	/** 储存图片文件后缀名 */
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
		 * 文件路径：File:///sdcard/xxx/xxx.jpg<br> mUri.getPath() 得到的就是文件路径
		 * /sdcard/xxx/xxx.jpg<br>
		 */
		File file = new File(mUri.getPath());
		int id = v.getId();
		switch (id) {
		case R.id.btn_showpic_save_camera:// 储存相片
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.FMT_FILENAME);
			String fileName = sdf.format(new Date());
			String filePath = PATH_OF_FILENAME + fileName + SUFFIX_OF_FILENAME;
			File dst = new File(filePath);
			dst.getParentFile().mkdirs();// 创建文件夹（用文件的上级目录创建）
			FileUtil.copyFile(file, dst);
			Toast.makeText(this, getString(R.string.saveto_camera) + filePath,
					Toast.LENGTH_LONG).show();
		case R.id.btn_showpic_delete_camera:// 删除相片
			file.delete();// 删除文件
			Toast.makeText(this, getString(R.string.delete_temp_camera),
					Toast.LENGTH_SHORT).show();
			finish();
			break;
		default:
			break;
		}
	}
}
