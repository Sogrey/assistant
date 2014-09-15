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
	/** ������� �������� */
	protected View mLytContainer;
	/** ���Ԥ�� ������ͼ */
	protected SurfaceView mSfcPreview;
	/** ��ť ���� */
	protected Button mBtnBack;
	/** ��ť ����� */
	protected Button mBtnFlash;
	/** ��ť ���� */
	protected Button mBtnShutter;
	/** ������ ���� */
	protected SeekBar mSkbZoom;
	/** �������� */
	protected Camera mCamera;
	/** �������� */
	protected Parameters mParameters;
	/** �����״̬�� */
	protected List<String> mFlashModes;
	/** ֧��ͼƬ��С */
	private List<Size> mPictureSizes;
	/** �����ģʽList�±� */
	protected int mFlashIndex = 0;
	/** ѡ��ͼƬ��С ��ť */
	protected Button mBtnPicSize;
	/** ͼƬ��СList�±� */
	protected int mPicSizeIndex = 0;
	/** ������ʱͼƬ�ļ�ǰ׺ */
	protected static final String PREFIX_OF_FILENAME = "DCIM_";
	/** ������ʱͼƬ�ļ���׺�� */
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
		// ���ý��������ֵ
		mSkbZoom.setMax(mParameters.getMaxZoom());
		// ���ý�������ǰֵ
		mSkbZoom.setProgress(mParameters.getZoom());
		refreshFlashMode();
		refreshPictrueSize();
	}

	@Override
	protected void onStop() {
		if (mCamera != null) {
			/* �ͷ����ռ�� */
			mCamera.release();
			/* Ϊ����Դ�ͷŸ����� */
			mCamera = null;
		}
		super.onStop();
	}

	/** ��ʼ���齨����View�⣩ */
	private void initComps() {
		/* �ж���������ͷ�豸 */
		if (Camera.getNumberOfCameras() > 0) {
			if (mCamera != null) {
				mCamera.release();
			}
			mCamera = Camera.open();
			mParameters = mCamera.getParameters();
			/* API17 �Ժ������·���������վ��� */
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

	/** ��ʼ��View�齨 */
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
	 * ȫ��
	 * */
	public void setFullScreen() {
		// ȥ����
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȥ��Ϣ��
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/** ˢ�������״̬ */
	private void refreshFlashMode() {
		if (mFlashModes.size() > 1) {// �ж�������ƹ���
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
			mParameters.setFlashMode(flashString);// �����������Ʋ���
			mCamera.setParameters(mParameters);// �����������Ʋ��������
		}// End if (mFlahModes != null)
		else {
			Toast.makeText(this, R.string.tint_noflash_camera,
					Toast.LENGTH_LONG).show();
			mBtnFlash.setBackgroundResource(R.drawable.btn_flash_close_camera);
		}
	}

	/** ˢ��ͼƬ��С��ť */
	private void refreshPictrueSize() {
		if (mPictureSizes.size() > 1) {// �жϿ�֧�ֶ���Ƭ��С���
			int pictureWidth = mPictureSizes.get(mPicSizeIndex).width;
			int pictureHeight = mPictureSizes.get(mPicSizeIndex).height;
			mBtnPicSize.setText(pictureHeight
					+ getString(R.string.txt_size_camera) + pictureWidth);
			mParameters.setPictureSize(pictureWidth, pictureHeight);// ���������Ƭ��С����
			mCamera.setParameters(mParameters);// ���������Ƭ��С���������
		} else {
			Toast.makeText(this, R.string.tint_nopicsize_camera,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_back_camera:// ����
			finish();
			break;
		case R.id.btn_shutter_camera:// ����
			mCamera.takePicture(this, null, this);
			break;
		case R.id.btn_flash_camera:// �����
			mFlashIndex = (mFlashIndex + 1) % mFlashModes.size();
			refreshFlashMode();
			break;
		case R.id.btn_picturesize_camera:// ͼƬ��С
			mPicSizeIndex = (mPicSizeIndex + 1) % mPictureSizes.size();
			refreshPictrueSize();
			break;
		case R.id.lyt_camera:// �Խ�
			/* ������һ���ص������Խ��ɹ�����ʧ�ܣ�����Ϊnull */
			mCamera.autoFocus(this);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA:// ���ռ�
		case KeyEvent.KEYCODE_ENTER:// �س���
		case KeyEvent.KEYCODE_DPAD_CENTER:// �����м�
		case KeyEvent.KEYCODE_VOLUME_MUTE:// ������
			mCamera.takePicture(this, null, this);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:// ������
		case KeyEvent.KEYCODE_VOLUME_DOWN:// ������
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
		case KeyEvent.KEYCODE_VOLUME_UP:// ������
		case KeyEvent.KEYCODE_VOLUME_DOWN:// ������
			return true;
		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);// Ԥ����ʾ
			mCamera.startPreview();// ����Ԥ��
			// mCamera.startFaceDetection();// �����������
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
			mCamera.stopPreview();// �ر�Ԥ��
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			mParameters.setZoom(progress);// ���ò���
			mCamera.setParameters(mParameters);// ���ò��������
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	/**
	 * �Զ��Խ�<br>
	 * 
	 * @param success
	 *            �Խ������true���Խ��ɹ���false���Խ�ʧ��
	 * @param camera
	 *            ���ʵ��
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

	/** ��Ƭ���� */
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		FileOutputStream fos = null;
		File file = null;
		try {
			// ������ʱ�ļ�������ҪдCD��Ȩ��
			file = File.createTempFile(PREFIX_OF_FILENAME, SUFFIX_OF_FILENAME);
			fos = new FileOutputStream(file);
			/*
			 * bitmap.compress()�������Խ�ͼƬ���浽ָ������ <br> ����һ��ͼƬ��ʽ <br> ��������ͼƬ�������<br>
			 * ��������Ҫ�������������ļ������������ͼƬ���浽�ļ�<br>
			 */
			bitmap.compress(CompressFormat.JPEG, 100, fos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();// �ͷ���
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Intent intent = new Intent(this, ConfirmActivity.class);
		// ���·���������ʹ��
		// intent.putExtra("PATH", file.getAbsolutePath());
		intent.setData(Uri.fromFile(file));// ��Intent ����Data����Uri
		startActivity(intent);
	}

	/** ���Ű����¼� */
	@Override
	public void onShutter() {

		Toast.makeText(this, getString(R.string.shutter_camera),
				Toast.LENGTH_SHORT).show();
	}
}
