/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class vng_wmb_activity_InfoService */

#ifndef _Included_vng_wmb_activity_InfoService
#define _Included_vng_wmb_activity_InfoService
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     vng_wmb_activity_InfoService
 * Method:    getInfo
 * Signature: (Ljava/lang/String;)Lvng/wmb/activity/InfoType;
 */
jobject Java_vng_wmb_activity_InfoService_getInfo(JNIEnv *, jobject, jstring);

/*
 * Class:     vng_wmb_activity_InfoService
 * Method:    setInfo
 * Signature: (Ljava/lang/String;Lvng/wmb/activity/InfoType;)V
 */
void Java_vng_wmb_activity_InfoService_setInfo(JNIEnv *, jobject, jstring,
		jobject);

/*
 * Class:     vng_wmb_activity_InfoService
 * Method:    deleteInfo
 * Signature: (Ljava/lang/String;)V
 */
void Java_vng_wmb_activity_InfoService_deleteInfo(JNIEnv *, jobject, jstring);

jint Java_vng_wmb_activity_InfoService_writeFile(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif