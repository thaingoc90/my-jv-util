#ifndef _INFO_H_
#define _INFO_H_

#include "jni.h"
#include <stdint.h>

#define INFO_MAX_CAPACITY 4

typedef struct {
	char* mName;
	char* mAddress;
	jint mSalary;
} InfoType;

typedef struct {
	InfoType infoList[INFO_MAX_CAPACITY];
	jint length;
} InfoStore;

InfoType* findEntry(JNIEnv* pEnv, InfoStore* pInfoStore, jstring name,
		jint* pError);

InfoType* allocateEntry(JNIEnv* pEnv, InfoStore* pInfoStore, jstring name);

void releaseEntry(JNIEnv* pEnv, InfoType* pInfo);

#endif
