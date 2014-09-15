package edu.feicui.assistant.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import edu.feicui.assistant.R;
import edu.feicui.assistant.base.BaseActivity;
import edu.feicui.assistant.util.DialogUtil;

/**
 * @author Sogrey
 * 
 */
public class CameraActivity extends BaseActivity implements OnClickListener,
		Callback, OnSeekBarChangeListener, AutoFocusCallback, PictureCallback,
		ShutterCallback {
	/** 相机界面 根本布局 */
	protected View mLytContainer;
	/** 相机预览 表面视图 */
	protected SurfaceView mSfcPreview;
	/** 按钮 返回 */
	protected Button mBtnBack;
	/** 按钮 闪光灯 */
	protected Button mBtnFlash;
	/** 按钮 照相 */
	protected Button mBtnShutter;
	/** 进度条 调焦 */
	protected SeekBar mSkbZoom;
	/** 相机类变量 */
	protected Camera mCamera;
	/** 相机类参数 */
	protected Parameters mParameters;
	/** 闪光灯状态数 */
	protected List<String> mFlashModes;
	/** 支持图片大小 */
	private List<Size> mPictureSizes;
	/** 闪光灯模式List下标 */
	protected int mFlashIndex = 0;
	/** 选择图片大小 按钮 */
	protected Button mBtnPicSize;
	/** 图片大小List下标 */
	protected int mPicSizeIndex = 0;
	/** 储存临时图片文件前缀 */
	protected static final String PREFIX_OF_FILENAME = "DCIM_";
	/** 储存临时图片文件后缀名 */
	protected static final String SUFFIX_OF_FILENAME = ".jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullScreen();
		setContentView(R.layout.activity_camera);
		initViews();
	}

	@Override
	protected void onStart() {
		super.onStart();
		initComps();
		// 设置进度条最大值
		mSkbZoom.setMax(mParameters.getMaxZoom());
		// 设置进度条当前值
		mSkbZoom.setProgress(mParameters.getZoom());
		refreshFlashMode();
		refreshPictrueSize();
	}

	@Override
	protected void onStop() {
		if (mCamera != null) {
			/* 释放相机占用 */
			mCamera.release();
			/* 为了资源释放更彻底 */
			mCamera = null;
		}
		super.onStop();
	}

	/** 初始化组建（除View外） */
	private void initComps() {
		/* 判断有无摄像头设备 */
		if (Camera.getNumberOfCameras() > 0) {
			if (mCamera != null) {
				mCamera.release();
			}
			mCamera = Camera.open();
			mParameters = mCamera.getParameters();
			/* API17 以后用以下方法相机拍照静音 */
			// mCamera.enableShutterSound(false);
			mFlashModes = mParameters.getSupportedFlashModes();
			mPictureSizes = mParameters.getSupportedPictureSizes();
		} else {
			Intent intent = new Intent(CameraActivity.this, DialogUtil.class);
			intent.putExtra(DialogUtil.DIALOG_NAME_STRING,
					DialogUtil.DIALOG_ID_NOCAMERA_CAMERA);
			startActivity(intent);
			finish();
		}
	}

	/** 初始化View组建 */
	private void initViews() {
		mLytContainer = findViewById(R.id.lyt_camera);
		mLytContainer.setOnClickListener(this);
		mSfcPreview = (SurfaceView) findViewById(R.id.sfc_camera);
		mSfcPreview.getHolder().addCallback(this);
		mBtnBack = (Button) findViewById(R.id.btn_back_camera);
		mBtnBack.setOnClickListener(this);
		mBtnShutter = (Button) findViewById(R.id.btn_shutter_camera);
		mBtnShutter.setOnClickListener(this);
		mBtnFlash = (Button) findViewById(R.id.btn_flash_camera);
		mBtnFlash.setOnClickListener(this);
		mBtnPicSize = (Button) findViewById(R.id.btn_picturesize_camera);
		mBtnPicSize.setOnClickListener(this);
		mSkbZoom = (SeekBar) findViewById(R.id.skb_camera);
		mSkbZoom.setOnSeekBarChangeListener(this);
	}

	/**
	 * 全屏
	 * */
	public void setFullScreen() {
		// 去标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去信息栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/** 刷新闪光灯状态 */
	private void refreshFlashMode() {
		if (mFlashModes.size() > 1) {// 判断有闪光灯功能
			String flashString = mFlashModes.get(mFlashIndex);
			if (Parameters.FLASH_MODE_AUTO.equals(flashString)) {
				mBtnFlash
						.setBackgroundResource(R.drawable.btn_flash_auto_camera);
			} else if (Parameters.FLASH_MODE_OFF.equals(flashString)) {
				mBtnFlash
						.setBackgroundResource(R.drawable.btn_flash_close_camera);
			} else if (Parameters.FLASH_MODE_ON.equals(flashString)) {
				mBtnFlash
						.setBackgroundResource(R.drawable.btn_flash_open_camera);
			} else if (Parameters.FLASH_MODE_RED_EYE.equals(flashString)) {

			} else if (Parameters.FLASH_MODE_TORCH.equals(flashString)) {

			}
			mParameters.setFlashMode(flashString);// 设置相机闪光灯参数
			mCamera.setParameters(mParameters);// 设置相机闪光灯参数到相机
		}// End if (mFlahModes != null)
		else {
			Toast.makeText(this, R.string.tint_noflash_camera,
					Toast.LENGTH_LONG).show();
			mBtnFlash.setBackgroundResource(R.drawable.btn_flash_close_camera);
		}
	}

	/** 刷新图片大小按钮 */
	private void refreshPictrueSize() {
		if (mPictureSizes.size() > 1) {// 判断可支持多相片大小规格
			int pictureWidth = mPictureSizes.get(mPicSizeIndex).width;
			int pictureHeight = mPictureSizes.get(mPicSizeIndex).height;
			mBtnPicSize.setText(pictureHeight
					+ getString(R.string.txt_size_camera) + pictureWidth);
			mParameters.setPictureSize(pictureWidth, pictureHeight);// 设置相机相片大小参数
			mCamera.setParameters(mParameters);// 设置相机相片大小参数到相机
		} else {
			Toast.makeText(this, R.string.tint_nopicsize_camera,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_back_camera:// 返回
			finish();
			break;
		case R.id.btn_shutter_camera:// 拍照
			mCamera.takePicture(this, null, this);
			break;
		case R.id.btn_flash_camera:// 闪光灯
			mFlashIndex = (mFlashIndex + 1) % mFlashModes.size();
			refreshFlashMode();
			break;
		case R.id.btn_picturesize_camera:// 图片大小
			mPicSizeIndex = (mPicSizeIndex + 1) % mPictureSizes.size();
			refreshPictrueSize();
			break;
		case R.id.lyt_camera:// 对焦
			/* 参数是一个回调表明对焦成功还是失败，可以为null */
			mCamera.autoFocus(this);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA:// 拍照键
		case KeyEvent.KEYCODE_ENTER:// 回车键
		case KeyEvent.KEYCODE_DPAD_CENTER:// 方向中键
		case KeyEvent.KEYCODE_VOLUME_MUTE:// 静音键
			mCamera.takePicture(this, null, this);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:// 声音加
		case KeyEvent.KEYCODE_VOLUME_DOWN:// 声音减
			int zoom = mParameters.getZoom();
			if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
				zoom++;
			else
				zoom--;
			zoom = Math.max(0, zoom);
			zoom = Math.min(mParameters.getMaxZoom(), zoom);
			mSkbZoom.setProgress(zoom);
			mParameters.setZoom(zoom);
			mCamera.setParameters(mParameters);
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:// 声音加
		case KeyEvent.KEYCODE_VOLUME_DOWN:// 声音减
			return true;
		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);// 预览显示
			mCamera.startPreview();// 开启预览
			// mCamera.startFaceDetection();// 开启人脸检测
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();// 关闭预览
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			mParameters.setZoom(progress);// 设置参数
			mCamera.setParameters(mParameters);// 设置参数到相机
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	/**
	 * 自动对焦<br>
	 * 
	 * @param success
	 *            对焦结果：true，对焦成功；false，对焦失败
	 * @param camera
	 *            相机实例
	 * */
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (success) {
			Toast.makeText(this, getString(R.string.focus_success_camera),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, getString(R.string.focus_fail_camera),
					Toast.LENGTH_SHORT).show();
		}
	}

	/** 相片处理 */
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		FileOutputStream fos = null;
		File file = null;
		try {
			// 创建临时文件，不需要写CD卡权限
			file = File.createTempFile(PREFIX_OF_FILENAME, SUFFIX_OF_FILENAME);
			fos = new FileOutputStream(file);
			/*
			 * bitmap.compress()方法可以将图片保存到指定流中 <br> 参数一：图片格式 <br> 参数二：图片输出质量<br>
			 * 参数三：要输出的流，如果文件是输出流，则将图片保存到文件<br>
			 */
			bitmap.compress(CompressFormat.JPEG, 100, fos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();// 释放流
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Intent intent = new Intent(this, ConfirmActivity.class);
		// 以下方法不建议使用
		// intent.putExtra("PATH", file.getAbsolutePath());
		intent.setData(Uri.fromFile(file));// 给Intent 设置Data参数Uri
		startActivity(intent);
	}

	/** 快门按下事件 */
	@Override
	public void onShutter() {

		Toast.makeText(this, getString(R.string.shutter_camera),
				Toast.LENGTH_SHORT).show();
	}
}
