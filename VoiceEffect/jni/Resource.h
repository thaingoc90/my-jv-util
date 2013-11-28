#ifndef _RESOURCE_H_
#define _RESOURCE_H_

#include "Types.h"

class Resource {
public:
	Resource(const char* pPath);
	void load();
	const char* getPath();
	off_t getLength();
	status open();
	void close();
	uint8_t* getBuffer();
	status load();
	status unload();

private:
	const char* mPath;
	off_t mLength;
};

#endif
