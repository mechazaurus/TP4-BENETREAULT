#include <string.h>
#include <jni.h>
#include <math.h>
using namespace std;
extern "C" {
JNIEXPORT void JNICALL
Java_com_example_myapplicationopencv_MainActivity_ProcessFast(JNIEnv *env, jobject thiz, jint width,
                                                              jint height, jbyteArray data,
                                                              jbyteArray out) {
    jbyte *_data = env->GetByteArrayElements(data, 0);
    jbyte *_out = env->GetByteArrayElements(out, 0);


    // ===== GRADIENT =====
/*    for (int indexV = 0; indexV < width; indexV++) {
        for (int indexH = 0; indexH < width; indexH++) {
            if (indexH == 0 || indexH == width - 1) {
                _out[indexV * width + indexH] = _data[indexV * width + indexH];
            } else if (indexV == 0 || indexV == height - 1) {
                _out[indexV * width + indexH] = _data[indexV * width + indexH];
            } else {
                int valueH = (_data[indexV * width + indexH - 1] -
                              _data[indexV * width + indexH + 1]);
                int valueV = (_data[indexV * width + indexH - width] -
                              _data[indexV * width + indexH + width]);
                int value = (valueH + valueV) / 4 + 128;
                _out[indexV * width + indexH] = value;
            }
        }
    }*/
    // ====================

    // ===== SOBEL =====
    for (int indexV = 0; indexV < height; indexV++) {
        for (int indexH = 0; indexH < width; indexH++) {
            if (indexH == 0 || indexH == width-1) {
                _out[indexV*width+indexH] = _data[indexV*width+indexH];
            } else if (indexV == 0 || indexV == height-1) {
                _out[indexV*width+indexH] = _data[indexV*width+indexH];
            } else {
                float valueUpperH = _data[indexV*width+indexH-1-width] * -1.0f
                                    + _data[indexV*width+indexH-width] * -2.0f
                                    + _data[indexV*width+indexH+1-width] * -1.0f;

                float valueLowerH = _data[indexV*width+indexH-1+width] * 1.0f
                                    + _data[indexV*width+indexH+width] * 2.0f
                                    + _data[indexV*width+indexH+1+width] * 1.0f;

                float valueLeftV = _data[indexV*width+indexH-width-1] * -1.0f
                                   + _data[indexV*width+indexH-width] * -2.0f
                                   + _data[indexV*width+indexH-width+1] * -1.0f;

                float valueRightV = _data[indexV*width+indexH+width-1] * 1.0f
                                    + _data[indexV*width+indexH+width] * 2.0f
                                    + _data[indexV*width+indexH+width+1] * 1.0f;

                int valueH = (valueLowerH + valueUpperH);
                int valueV = (valueLeftV + valueRightV);
                double value = sqrt(valueH*valueH + valueV*valueV);
                int result = value;
                _out[indexV*width+indexH] = result;
            }
        }
    }
    // =================


    env->ReleaseByteArrayElements(data, _data, 0);
    env->ReleaseByteArrayElements(out, _out, 0);
    }
}