#ifndef _MP3_UTILS_H_
#define _MP3_UTILS_H_

int convertWavToMp3(char* wavInput, char* mp3Output);
int convertMp3ToWav(char* mp3Input, char* wavOutput);

/*
 * Use for processing each block.
 */
int initMp3Decoder(char* mp3Input);
int convertBlockMp3ToWav(short* buffer);
void destroyMp3Decoder();
void rewindMp3InputFile();

int cutMp3File(char* inFilePath, char* outFilePath, int beginTime,
		int endTime);

#endif
