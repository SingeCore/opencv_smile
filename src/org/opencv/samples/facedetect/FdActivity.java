package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class FdActivity extends Activity implements CvCameraViewListener2 {

	private static final String TAG = "OCVSample::Activity";
	private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
	public static final int JAVA_DETECTOR = 0;
	public static final int NATIVE_DETECTOR = 1;

	private MenuItem mItemFace50;
	private MenuItem mItemFace40;
	private MenuItem mItemFace30;
	private MenuItem mItemFace20;
	private MenuItem mItemType;

	private Mat mRgba;
	private Mat mGray;
	private File mCascadeFile;
	private CascadeClassifier mJavaDetector;
	private DetectionBasedTracker mNativeDetector;

	private int mDetectorType = JAVA_DETECTOR;
	private String[] mDetectorName;

	private float mRelativeFaceSize = 0.2f;
	private int mAbsoluteFaceSize = 0;

	private CameraBridgeViewBase mOpenCvCameraView;
	// 在 mOpenCvCameraView 的回调接口onCameraFrame函数里面处理每一帧从相机获取到的图片。
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");

				// Load native library after(!) OpenCV initialization
				System.loadLibrary("detection_based_tracker");

				try {
					// load cascade file from application resources
					// 这里去加载人脸识别分类文件（lbpcascade_frontalface.XML
					/*
					 * 是XML文件，这都是利用Opencv给我们提供好的XML人脸识别分类文件，在opencv/source/data/
					 * 目录下,这里把那个文件拉到了Raw资源文件里面，方便Android调用，
					 * 如果要自己实现一个XML人脸识别分类文件的话，需要用到opencv_haartraining，来训练大量数据，
					 * 最终生成XML人脸识别分类文件）
					 */
					InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
					File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
					mCascadeFile = new File(cascadeDir, "haarcascade_smile.xml");
					FileOutputStream os = new FileOutputStream(mCascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();

					mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
					Log.e("分类器路径--》", mCascadeFile.getAbsolutePath());
					if (mJavaDetector.empty()) {
						Log.e(TAG, "Failed to load cascade classifier");
						mJavaDetector = null;
					} else
						Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

					mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

					cascadeDir.delete();

				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
				}

				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public FdActivity() {
		mDetectorName = new String[2];
		mDetectorName[JAVA_DETECTOR] = "Java";
		mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	private Button bu;
	private ImageView vi;
	private TextView tvi;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.face_detect_surface_view);

		init();
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	// 图片捕捉
	private Handler  handler ;
	private int id = 0;
	private String filePath;
	private int pi = 0;
	private Button bu1;
	private int hd = 3;
	@SuppressLint("HandlerLeak")
	private void init() {
		tvi = (TextView) findViewById(R.id.tv);
		vi = (ImageView) findViewById(R.id.imageView);
		bu = (Button) findViewById(R.id.button1);
		bu1 = (Button) findViewById(R.id.button2);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// 自动拍照并对比
				hd = 3;
			}
		};
		OnClickListener btnOnClick = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
				case R.id.button1:
				//	id = 1;
					break;
				case R.id.button2:

					break;
				}
			}
		};
		bu.setOnClickListener(btnOnClick);
		bu1.setOnClickListener(btnOnClick);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat();
	}

	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
	}

	// 正方形
	private int log;
	private Mat mat;
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// 这里获取相机拍摄到的原图，彩色图
		mRgba = inputFrame.rgba();
		// 这里获取相机拍摄到的灰度图，用来给下面检测人脸使用。
		mGray = inputFrame.gray();

		if (mAbsoluteFaceSize == 0) {
			int height = mGray.rows();
			if (Math.round(height * mRelativeFaceSize) > 0) {
				mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
			}
			mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
		}

		MatOfRect faces = new MatOfRect();

		if (mDetectorType == JAVA_DETECTOR) {
			if (mJavaDetector != null)
				// 调用opencv的detectMultiScale（）检测函数，参数意义如下

				/*
				 *
				 * mGray表示的是要检测的输入图像，faces表示检测到的目标序列,存储检测结果（坐标位置，长，宽），1.1表示
				 * 每次图像尺寸减小的比例为1.1，2表示每一个目标至少要被检测到3次才算是真的目标(因为周围的像素和不同的窗口大
				 * 小都可以检测到目标),2（其实是一个常量：CV_HAAR_SCALE_IMAGE）表示不是缩放分类器来检测，而是缩放图像，
				 * 最后两个size()为检测目标的 最小最大尺寸
				 *
				 */
				mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO:
						// objdetect.CV_HAAR_SCALE_IMAGE
						new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
		} else if (mDetectorType == NATIVE_DETECTOR) {
			if (mNativeDetector != null)
				mNativeDetector.detect(mGray, faces);
		} else {
			Log.e(TAG, "Detection method is not selected!");
		}

		final Rect[] facesArray = faces.toArray();
		log = facesArray.length;
		// 在原图mRgba上为每个检测到的人脸画一个绿色矩形
		for (int i = 0; i < facesArray.length; i++)
			Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
		//Button 测试
		if ((id != 0) && (log != 0)) {
			// 重置标志
			id = 0;
			// 要把Mat对象转换成Bitmap对象，需要创建一个宽高相同的Bitmap对象昨晚参数
			// 记录要展示图片的ImageView
			final ImageView image = vi;
			final TextView qd = tvi;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// 获取roi区域转为bitmap格式
					mat = new Mat(mRgba, facesArray[0]);
					Mat dd = mat.clone();
					Log.e("准备开始微笑处理", "------>");
					//微笑处理度
					Mat mrgb = mRgba;
					MatOfRect faces2 = new MatOfRect();
					float sm =  mNativeDetector.data(mrgb, faces2);
					Log.e("微笑强度-->", ""+sm);
					qd.setText("笑容强度:"+sm);
					//格式转换
					Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Config.RGB_565);
					Utils.matToBitmap(dd, bmp);
					//存储
					filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/a" + pi + "png";				
					image.setImageBitmap(bmp);
				}
			});
		}
		//0.5秒检测一次 微笑处理
		if((hd == 3)&&(log!=0)){
			log = 0;
			hd = 0;
			handler.sendEmptyMessageDelayed(1, 500);
			runOnUiThread(new Runnable() {
				public void run() {
					TextView tvs = tvi;
					MatOfRect faces2 = new MatOfRect();
					float sm =  mNativeDetector.data(mRgba, faces2);
					Log.e("微笑强度-->", ""+sm);
					tvs.setText("笑容强度:"+sm);
					//截取人脸
					mat = new Mat(mRgba, facesArray[0]);
					Bitmap bmpz = Bitmap.createBitmap(mat.cols(), mat.rows(), Config.RGB_565);
					Utils.matToBitmap(mat, bmpz);
					vi.setImageBitmap(bmpz);
				}
			});
		}
		
		return mRgba;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemFace50 = menu.add("Face size 50%");
		mItemFace40 = menu.add("Face size 40%");
		mItemFace30 = menu.add("Face size 30%");
		mItemFace20 = menu.add("Face size 20%");
		mItemType = menu.add(mDetectorName[mDetectorType]);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
		if (item == mItemFace50)
			setMinFaceSize(0.5f);
		else if (item == mItemFace40)
			setMinFaceSize(0.4f);
		else if (item == mItemFace30)
			setMinFaceSize(0.3f);
		else if (item == mItemFace20)
			setMinFaceSize(0.2f);
		else if (item == mItemType) {
			int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
			item.setTitle(mDetectorName[tmpDetectorType]);
			setDetectorType(tmpDetectorType);
		}
		return true;
	}

	private void setMinFaceSize(float faceSize) {
		mRelativeFaceSize = faceSize;
		mAbsoluteFaceSize = 0;
	}

	private void setDetectorType(int type) {
		if (mDetectorType != type) {
			mDetectorType = type;

			if (type == NATIVE_DETECTOR) {
				Log.i(TAG, "Detection Based Tracker enabled");
				mNativeDetector.start();
			} else {
				Log.i(TAG, "Cascade detector enabled");
				mNativeDetector.stop();
			}
		}
	}

}
