#include "vng_wmb_activity_InfoService.h"
#include "Info.h"
#include "string.h"
#include <stdint.h>

static InfoStore infoStore = { { }, 0 };

jobject Java_vng_wmb_activity_InfoService_getInfo(JNIEnv* pEnv, jobject pThis,
		jstring name) {
	InfoType* info = findEntry(pEnv, &infoStore, name, NULL);
	if (info == NULL) {
		return NULL;
	}
	jclass infoClass = pEnv->FindClass("vng/wmb/activity/InfoType");
	jmethodID constructor = pEnv->GetMethodID(infoClass, "<init>",
			"(Ljava/lang/String;Ljava/lang/String;I)V");
	jstring address = pEnv->NewStringUTF(info->mAddress);
	jobject infoObj = pEnv->NewObject(infoClass, constructor, name, address,
			info->mSalary);
	return infoObj;
}

void Java_vng_wmb_activity_InfoService_setInfo(JNIEnv* pEnv, jobject pThis,
		jstring name, jobject info) {
	InfoType* infoType = allocateEntry(pEnv, &infoStore, name);
	if (infoType != NULL) {
		jclass infoClass = pEnv->FindClass("vng/wmb/activity/InfoType");
		jfieldID field = pEnv->GetFieldID(infoClass, "address",
				"Ljava/lang/String;");
		jstring address = (jstring) pEnv->GetObjectField(info, field);
		const char* addressTemp = pEnv->GetStringUTFChars(address, NULL);
		if (addressTemp != NULL) {
			infoType->mAddress = new char[strlen(addressTemp)];
			strcpy(infoType->mAddress, addressTemp);
			pEnv->ReleaseStringUTFChars(address, addressTemp);
		}

		field = pEnv->GetFieldID(infoClass, "salary", "I");
		jint salary = pEnv->GetIntField(info, field);
		infoType->mSalary = salary;
	}
}

void Java_vng_wmb_activity_InfoService_deleteInfo(JNIEnv* pEnv, jobject pThis,
		jstring name) {

}
