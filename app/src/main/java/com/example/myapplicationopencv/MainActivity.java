package com.example.myapplicationopencv;



import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity implements CvCameraViewListener2 {
    private static final String    TAG = "OCVSample::Activity";

    private byte[] outarray;
    private byte[] tmparray;

    private int w;

    private int h;


    private CameraBridgeViewBase   mOpenCvCameraView;

    static {
        System.loadLibrary("native-lib");
    }

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial2_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableFpsMeter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        return true;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        //mRgba = new Mat(height, width, CvType.CV_8UC4);
        outarray = new byte[width*height];
        tmparray = new byte[width*height];
        w=width;
        h=height;
    }

    public void onCameraViewStopped() {
        //mRgba.release();

    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat gray =inputFrame.gray();
        MatToArray(gray);

        //Sutf to do here by the students....

        // ===== JAVA =====
        // Gradient
        //computeGradient();

        // Sobel
        //computeSobel();
        // ================

        // ===== CPP =====
        ProcessFast(w, h, outarray, tmparray);
        // ===============

        Mat out=ArrayToMat(gray,tmparray);
        return out;
    }

    private Mat ArrayToMat(Mat gray, byte[] grayarray) {
        // TODO Auto-generated method stub
        Mat out = gray.clone();
        out.put(0,0,grayarray);
        return out;
    }

    private void MatToArray(Mat gray) {
        // TODO Auto-generated method stub
        gray.get(0, 0, outarray);

    }

    /**
     * Method to compute the gradient version of an image
     */
    private void computeGradient() {
        for (int indexV = 0; indexV < h; indexV++) {
            for (int indexH = 0; indexH < w; indexH++) {
                if (indexH == 0 || indexH == w-1) {
                    tmparray[indexV*w+indexH] = outarray[indexV*w+indexH];
                } else if (indexV == 0 || indexV == h-1) {
                    tmparray[indexV*w+indexH] = outarray[indexV*w+indexH];
                } else {
                    Integer valueH = (outarray[indexV*w+indexH-1] - outarray[indexV*w+indexH+1]);
                    Integer valueV = (outarray[indexV*w+indexH-w] - outarray[indexV*w+indexH+w]);
                    Integer value = (valueH + valueV) / 4 + 128;
                    tmparray[indexV*w+indexH] = value.byteValue();
                }
            }
        }
    }

    /**
     * Method to compute the Sobel filtered image
     */
    private void computeSobel() {

        for (int indexV = 0; indexV < h; indexV++) {
            for (int indexH = 0; indexH < w; indexH++) {
                if (indexH == 0 || indexH == w-1) {
                    tmparray[indexV*w+indexH] = outarray[indexV*w+indexH];
                } else if (indexV == 0 || indexV == h-1) {
                    tmparray[indexV*w+indexH] = outarray[indexV*w+indexH];
                } else {
                    Float valueUpperH = outarray[indexV*w+indexH-1-w] * -1.0f
                            + outarray[indexV*w+indexH-w] * -2.0f
                            + outarray[indexV*w+indexH+1-w] * -1.0f;

                    Float valueLowerH = outarray[indexV*w+indexH-1+w] * 1.0f
                            + outarray[indexV*w+indexH+w] * 2.0f
                            + outarray[indexV*w+indexH+1+w] * 1.0f;

                    Float valueLeftV = outarray[indexV*w+indexH-w-1] * -1.0f
                            + outarray[indexV*w+indexH-w] * -2.0f
                            + outarray[indexV*w+indexH-w+1] * -1.0f;

                    Float valueRightV = outarray[indexV*w+indexH+w-1] * 1.0f
                            + outarray[indexV*w+indexH+w] * 2.0f
                            + outarray[indexV*w+indexH+w+1] * 1.0f;

                    Integer valueH = (valueLowerH.intValue() + valueUpperH.intValue());
                    Integer valueV = (valueLeftV.intValue() + valueRightV.intValue());
                    Double value = Math.sqrt(valueH*valueH + valueV*valueV);
                    Integer result = value.intValue();
                    tmparray[indexV*w+indexH] = result.byteValue();
                }
            }
        }
    }

    public native void ProcessFast(int width, int height, byte input[], byte output[]);
}