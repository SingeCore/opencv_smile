#include <DetectionBasedTracker_jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>
#include <string>
#include <vector>
#include <android/log.h>

#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/calib3d/calib3d.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/objdetect/objdetect.hpp>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
using namespace std;
using namespace cv;

inline void vector_Rect_to_Mat(vector<Rect>& v_rect, Mat& mat) {
	mat = Mat(v_rect, true);
}

JNIEXPORT jlong JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject(
		JNIEnv * jenv, jclass, jstring jFileName, jint faceSize) {
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject enter");
	const char* jnamestr = jenv->GetStringUTFChars(jFileName, NULL);
	string stdFileName(jnamestr);
	jlong result = 0;

	try {
		DetectionBasedTracker::Parameters DetectorParams;
		if (faceSize > 0)
			DetectorParams.minObjectSize = faceSize;
		result = (jlong) new DetectionBasedTracker(stdFileName, DetectorParams);
	} catch (cv::Exception& e) {
		LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
		jclass je = jenv->FindClass("org/opencv/core/CvException");
		if (!je)
			je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je, e.what());
	} catch (...) {
		LOGD("nativeCreateObject caught unknown exception");
		jclass je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je,
				"Unknown exception in JNI code of DetectionBasedTracker.nativeCreateObject()");
		return 0;
	}

	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject exit");
	return result;
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject(
		JNIEnv * jenv, jclass, jlong thiz) {
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject enter");
	try {
		if (thiz != 0) {
			((DetectionBasedTracker*) thiz)->stop();
			delete (DetectionBasedTracker*) thiz;
		}
	} catch (cv::Exception& e) {
		LOGD("nativeestroyObject caught cv::Exception: %s", e.what());
		jclass je = jenv->FindClass("org/opencv/core/CvException");
		if (!je)
			je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je, e.what());
	} catch (...) {
		LOGD("nativeDestroyObject caught unknown exception");
		jclass je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je,
				"Unknown exception in JNI code of DetectionBasedTracker.nativeDestroyObject()");
	}
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart(
		JNIEnv * jenv, jclass, jlong thiz) {
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart enter");
	try {
		((DetectionBasedTracker*) thiz)->run();
	} catch (cv::Exception& e) {
		LOGD("nativeStart caught cv::Exception: %s", e.what());
		jclass je = jenv->FindClass("org/opencv/core/CvException");
		if (!je)
			je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je, e.what());
	} catch (...) {
		LOGD("nativeStart caught unknown exception");
		jclass je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je,
				"Unknown exception in JNI code of DetectionBasedTracker.nativeStart()");
	}
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop(
		JNIEnv * jenv, jclass, jlong thiz) {
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop enter");
	try {
		((DetectionBasedTracker*) thiz)->stop();
	} catch (cv::Exception& e) {
		LOGD("nativeStop caught cv::Exception: %s", e.what());
		jclass je = jenv->FindClass("org/opencv/core/CvException");
		if (!je)
			je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je, e.what());
	} catch (...) {
		LOGD("nativeStop caught unknown exception");
		jclass je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je,
				"Unknown exception in JNI code of DetectionBasedTracker.nativeStop()");
	}
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize(
		JNIEnv * jenv, jclass, jlong thiz, jint faceSize) {
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize enter");
	try {
		if (faceSize > 0) {
			DetectionBasedTracker::Parameters DetectorParams =
					((DetectionBasedTracker*) thiz)->getParameters();
			DetectorParams.minObjectSize = faceSize;
			((DetectionBasedTracker*) thiz)->setParameters(DetectorParams);
		}
	} catch (cv::Exception& e) {
		LOGD("nativeStop caught cv::Exception: %s", e.what());
		jclass je = jenv->FindClass("org/opencv/core/CvException");
		if (!je)
			je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je, e.what());
	} catch (...) {
		LOGD("nativeSetFaceSize caught unknown exception");
		jclass je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je,
				"Unknown exception in JNI code of DetectionBasedTracker.nativeSetFaceSize()");
	}
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect(
		JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong faces) {
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect enter");
	try {
		vector<Rect> RectFaces;
		((DetectionBasedTracker*) thiz)->process(*((Mat*) imageGray));
		((DetectionBasedTracker*) thiz)->getObjects(RectFaces);
		vector_Rect_to_Mat(RectFaces, *((Mat*) faces));
	} catch (cv::Exception& e) {

		LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
		jclass je = jenv->FindClass("org/opencv/core/CvException");
		if (!je)
			je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je, e.what());
	} catch (...) {
		LOGD("nativeDetect caught unknown exception");
		jclass je = jenv->FindClass("java/lang/Exception");
		jenv->ThrowNew(je,
				"Unknown exception in JNI code DetectionBasedTracker.nativeDetect()");
	}
	LOGD(
			"Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect exit");
}

//smile 处理
JNIEXPORT jfloat JNICALL Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativateSmileData(
		JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong facess) {

	Mat img;
	Mat gray, smallImg;
	double scale = 1;
	bool tryflip = 0;
	float smile_max = 0;
	vector<Rect> faces, faces2;
	img = *((Mat*) imageGray);
	CascadeClassifier nestedCascade, cascade;
	if (!cascade.load(
			"/data/data/org.opencv.samples.facedetect/app_cascade/haarcascade_frontalface_alt2.xml")) { //分类器存放的位置
		return 11;
	}
	if (!nestedCascade.load(
			"/data/data/org.opencv.samples.facedetect/app_cascade/haarcascade_smile.xml")) { //分类器存放的位置
		return 11;
	}
	const static Scalar colors[] = { Scalar(255, 0, 0), Scalar(255, 128, 0),
			Scalar(255, 255, 0), Scalar(0, 255, 0), Scalar(0, 128, 255), Scalar(
					0, 255, 255), Scalar(0, 0, 255), Scalar(255, 0, 255) };
	cvtColor(img, gray, COLOR_BGR2GRAY);

	double fx = 1 / scale;
	resize(gray, smallImg, Size(), fx, fx, INTER_LINEAR);
	equalizeHist(smallImg, smallImg);

	cascade.detectMultiScale(smallImg, faces, 1.1, 2, 0
			| CASCADE_SCALE_IMAGE, Size(30, 30));
	if (tryflip)
		{
			flip(smallImg, smallImg, 1);
			cascade.detectMultiScale(smallImg, faces2,
				1.1, 2, 0
				| CASCADE_SCALE_IMAGE,
				Size(30, 30));
			for (vector<Rect>::const_iterator r = faces2.begin(); r != faces2.end(); ++r)
			{
				faces.push_back(Rect(smallImg.cols - r->x - r->width, r->y, r->width, r->height));
			}
		}
	for (size_t i = 0; i < faces.size(); i++) {
		Rect r = faces[i];
		Mat smallImgROI;
		vector<Rect> nestedObjects; //微笑处理
		Point center;
		Scalar color = colors[i % 8];
		int radius;

		double aspect_ratio = (double) r.width / r.height;
		if (0.75 < aspect_ratio && aspect_ratio < 1.3) {
			center.x = cvRound((r.x + r.width * 0.5) * scale);
			center.y = cvRound((r.y + r.height * 0.5) * scale);
			radius = cvRound((r.width + r.height) * 0.25 * scale);
			circle(img, center, radius, color, 3, 8, 0);
		} else
			rectangle(img, cvPoint(cvRound(r.x * scale), cvRound(r.y * scale)),
					cvPoint(cvRound((r.x + r.width - 1) * scale),
							cvRound((r.y + r.height - 1) * scale)), color, 3, 8,
					0);

		const int half_height = cvRound((float) r.height / 2);
		r.y = r.y + half_height;
		r.height = half_height - 1;
		smallImgROI = smallImg(r);

		//微笑处理&识别度
		nestedCascade.detectMultiScale(smallImgROI, nestedObjects, 1.1, 0, 0
				| CASCADE_SCALE_IMAGE, Size(30, 30));
		// The number of detected neighbors depends on image size (and also illumination, etc.). The
		// following steps use a floating minimum and maximum of neighbors. Intensity thus estimated will be
		//accurate only after a first smile has been displayed by the user.
		const int smile_neighbors = (int) nestedObjects.size();
		static int max_neighbors = -1;
		static int min_neighbors = -1;
		if (min_neighbors == -1)
			min_neighbors = smile_neighbors;
		max_neighbors = MAX(max_neighbors, smile_neighbors);

		// Draw rectangle on the left side of the image reflecting smile intensity
		float intensityZeroOne = ((float) smile_neighbors - min_neighbors)
				/ (max_neighbors - min_neighbors + 1);
		smile_max = intensityZeroOne;
	}
	LOGE("jni_smile_ok_->3");
	return (jfloat) smile_max;
}
