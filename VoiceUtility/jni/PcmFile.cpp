#include <stdio.h>
#include <string>
#include <sstream>
#include <cstring>
#include <assert.h>
#include <limits.h>
#include "Log.h"

#include "PcmFile.h"

using namespace std;

PcmFileBase::PcmFileBase() {
}

PcmFileBase::~PcmFileBase() {
}

/*PcmInFile*/

PcmInFile::PcmInFile(const char *fileName) {
	// Try to open the file for reading
	fptr = fopen(fileName, "rb");
	if (fptr == NULL) {
		// didn't succeed
		string msg = "Error : Unable to open file \"";
		msg += fileName;
		msg += "\" for reading.";
		Log::error(msg.c_str());
	}
	dataRead = 0;
}

PcmInFile::PcmInFile(FILE *file) {
	fptr = file;
	if (!file) {
		string msg = "Error : Unable to access input stream for reading";
		Log::error(msg.c_str());
	}
	dataRead = 0;
}

PcmInFile::~PcmInFile() {
	if (fptr) {
		fclose(fptr);
	}
	fptr = NULL;
}

void PcmInFile::rewind() {
	fseek(fptr, 0, SEEK_SET);
	dataRead = 0;
}

int PcmInFile::read(char *buffer, int maxElems) {
	int numBytes = (int) fread(buffer, 1, numBytes, fptr);
	dataRead += numBytes;
	return numBytes;
}

int PcmInFile::read(short *buffer, int maxElems) {
	int numBytes = (int) fread(buffer, sizeof(short), numBytes, fptr);
	dataRead += numBytes * sizeof(short);
	return numBytes;
}

int PcmInFile::eof() const {
	return feof(fptr);
}

/* PcmOutFile*/

PcmOutFile::PcmOutFile(const char *fileName) {
	// Try to open the file for reading
	fptr = fopen(fileName, "wb");
	if (fptr == NULL) {
		string msg = "Error : Unable to open file \"";
		msg += fileName;
		msg += "\" for writing.";
		Log::error(msg.c_str());
	}
	bytesWritten = 0;
}

PcmOutFile::PcmOutFile(FILE *file) {
	bytesWritten = 0;
	fptr = file;
	if (fptr == NULL) {
		string msg = "Error : Unable to access output file stream.";
		Log::error(msg.c_str());
	}
}

PcmOutFile::~PcmOutFile() {
	if (fptr) {
		fclose(fptr);
	}
	fptr = NULL;
}

void PcmOutFile::write(char *buffer, int numElems) {
	int res = (int) fwrite(buffer, 1, numElems, fptr);
	if (res != numElems) {
		Log::error("Error while writing to a file.");
	}
	bytesWritten += numElems;
}

void PcmOutFile::write(short *buffer, int numElems) {
	int res = (int) fwrite(buffer, sizeof(short), numElems, fptr);
	if (res != numElems) {
		Log::error("Error while writing to a file.");
	}
	bytesWritten += numElems * 2;
}

