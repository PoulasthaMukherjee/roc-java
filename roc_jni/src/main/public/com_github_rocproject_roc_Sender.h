/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_github_rocproject_roc_Sender */

#ifndef _Included_com_github_rocproject_roc_Sender
#define _Included_com_github_rocproject_roc_Sender
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_github_rocproject_roc_Sender
 * Method:    open
 * Signature: (JLcom/github/rocproject/roc/SenderConfig;)V
 */
JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Sender_open
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     com_github_rocproject_roc_Sender
 * Method:    bind
 * Signature: (JLcom/github/rocproject/roc/Address;)V
 */
JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Sender_bind
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     com_github_rocproject_roc_Sender
 * Method:    connect
 * Signature: (JIILcom/github/rocproject/roc/Address;)V
 */
JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Sender_connect
  (JNIEnv *, jobject, jlong, jint, jint, jobject);

/*
 * Class:     com_github_rocproject_roc_Sender
 * Method:    writeFloats
 * Signature: (J[F)V
 */
JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Sender_writeFloats
  (JNIEnv *, jobject, jlong, jfloatArray);

/*
 * Class:     com_github_rocproject_roc_Sender
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_github_rocproject_roc_Sender_close
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif