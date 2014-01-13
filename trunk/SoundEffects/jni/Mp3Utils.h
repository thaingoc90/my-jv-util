#ifndef _MP3_UTILS_H_
#define _MP3_UTILS_H_

void convertWavToMp3(char* wavInput, char* mp3Output);
void convertMp3ToWav(char* mp3Input, char* wavOutput);

/*
 * Use for processing each block.
 */
void initConvertMp3ToWav(char* mp3Input);
int convertBlockMp3ToWav(short* buffer);
void destroyConvertMp3ToWav();
void rewindMp3InputFile();

void cutFileMp3(char* inFilePath, char* outFilePath, int beginTime,
		int endTime);

#endif
