#include "Resource.h"

#include "Log.h"
Resource::Resource(const char* pPath) :
		mPath(pPath), mLength(0) {
}

const char* Resource::getPath() {
	return mPath;
}

off_t Resource::getLength() {
	return mLength;
}

