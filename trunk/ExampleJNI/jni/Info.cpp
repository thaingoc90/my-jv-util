#include "Info.h"
#include <string.h>

InfoType* findEntry(JNIEnv* pEnv, InfoStore* pInfoStore, jstring name,
		jint* pError) {
	InfoType* beginInfo = pInfoStore->infoList;
	jint length = pInfoStore->length;
	InfoType* lastInfo = beginInfo + length;
	const char* nameTemp = pEnv->GetStringUTFChars(name, NULL);
	if (nameTemp == NULL) {
		if (pError != NULL) {
			*pError = 1;
		}
		return NULL;
	}

	while ((beginInfo < lastInfo) && (strcmp(nameTemp, beginInfo->mName) != 0)) {
		++beginInfo;
	}

	pEnv->ReleaseStringUTFChars(name, nameTemp);
	return beginInfo == lastInfo ? NULL : beginInfo;
}

InfoType* allocateEntry(JNIEnv* pEnv, InfoStore* pInfoStore, jstring name) {
	jint error = 0;
	InfoType* info = findEntry(pEnv, pInfoStore, name, &error);
	if (info != NULL) {
		releaseEntry(pEnv, info);
	} else if (!error) {
		if (pInfoStore->length >= INFO_MAX_CAPACITY) {
			return NULL;
		}
		info = pInfoStore->infoList + pInfoStore->length;
		const char* nameTemp = pEnv->GetStringUTFChars(name, NULL);
		if (nameTemp == NULL) {
			return NULL;
		}
		info->mName = new char[strlen(nameTemp)];
		strcpy(info->mName, nameTemp);
		pInfoStore->length++;
		pEnv->ReleaseStringUTFChars(name, nameTemp);
	}
	return info;
}

void releaseEntry(JNIEnv* pEnv, InfoType* pInfo) {
	delete[] pInfo->mAddress;
}
