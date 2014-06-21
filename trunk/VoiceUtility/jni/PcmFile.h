#ifndef PCMFILE_H
#define PCMFILE_H

#include <stdio.h>

class PcmFileBase {
protected:
	PcmFileBase();
	virtual ~PcmFileBase();
};

class PcmInFile: protected PcmFileBase {
private:
	FILE *fptr;
	long dataRead;
public:
	PcmInFile(const char *filename);
	PcmInFile(FILE *file);
	~PcmInFile();

	void rewind();
	int read(char *buffer, int maxElems);
	int read(short *buffer, int maxElems);
	int eof() const;
};

class PcmOutFile: protected PcmFileBase {
private:
	FILE *fptr;
	int bytesWritten;
public:
	PcmOutFile(const char *filename);
	PcmOutFile(FILE *file);
	~PcmOutFile();

	void write(char *buffer, int maxElems);
	void write(short *buffer, int maxElems);
};
#endif
