#include "Config.h"
#include "Mp3Utils.h"
#include "Log.h"
#include "Utils.h"
#include <stdio.h>

#ifdef _USE_LAME_MP3_

#include "LameMp3/get_audio.h"
#include "LameMp3/lame.h"

int convertWavToMp3(char* wavInput, char* mp3Output) {

	FILE *pcm = fopen(wavInput, "rb");
	FILE *mp3 = fopen(mp3Output, "wb");

	const int PCM_SIZE = 2046;
	const int MP3_SIZE = 2046;

	short int pcmBuffer[PCM_SIZE * 2];
	unsigned char mpBuffer[MP3_SIZE];

	short read, write;

	lame_t lame = lame_init();
	lame_set_num_channels(lame, 1);
	lame_set_in_samplerate(lame, SAMPLE_RATE);
	lame_set_out_samplerate(lame, SAMPLE_RATE);
	lame_set_brate(lame, 128);
	lame_set_VBR(lame, vbr_default);

	if ((lame_init_params(lame)) < 0) {
		Log::info("Unable to initialize MP3 parameters");
		fclose(mp3);
		fclose(pcm);
		return STATUS_FAIL;
	}

	do {
		read = fread(pcmBuffer, sizeof(short int), 2 * PCM_SIZE, pcm);
		if (read == 0) {
			write = lame_encode_flush(lame, mpBuffer, MP3_SIZE);
		} else {
			write = lame_encode_buffer(lame, pcmBuffer, pcmBuffer, read,
					mpBuffer, MP3_SIZE);
		}
		fwrite(mpBuffer, write, 1, mp3);
	} while (read != 0);

	lame_close(lame);
	fclose(mp3);
	fclose(pcm);
	return STATUS_OK;
}

static int lame_decoder(lame_t gfp, FILE * outf, char *outPath) {
	short int Buffer[2][1152];
	int i, iread;
	double wavsize;
	int tmp_num_channels = lame_get_num_channels(gfp);

	if (!(tmp_num_channels >= 1 && tmp_num_channels <= 2)) {
		Log::info("Internal error.  Aborting.");
		return -1;
	}

	if (0 == global_decoder.disable_wav_header)
		WriteWaveHeader(outf, 0x7FFFFFFF, lame_get_in_samplerate(gfp),
				tmp_num_channels, 16);
	/* unknown size, so write maximum 32 bit signed value */

	wavsize = 0;
	do {
		iread = get_audio16(gfp, Buffer); /* read in 'iread' samples */
		if (iread >= 0) {
			wavsize += iread;
			put_audio16(outf, Buffer, iread, tmp_num_channels);
		}
	} while (iread > 0);

	i = (16 / 8) * tmp_num_channels;
	if (wavsize <= 0) {
		if (global_ui_config.silent < 10)
			Log::info("WAVE file contains 0 PCM samples\n");
		wavsize = 0;
	} else if (wavsize > 0xFFFFFFD0 / i) {
		if (global_ui_config.silent < 10)
			Log::info("Very huge WAVE file, can't set filesize accordingly\n");
		wavsize = 0xFFFFFFD0;
	} else {
		wavsize *= i;
	}
	/* if outf is seekable, rewind and adjust length */
	if (!global_decoder.disable_wav_header && strcmp("-", outPath)
			&& !fseek(outf, 0l, SEEK_SET))
		WriteWaveHeader(outf, (int) wavsize, lame_get_in_samplerate(gfp),
				tmp_num_channels, 16);
	fclose(outf);
	close_infile();

	return 0;
}

static FILE * init_files(lame_global_flags * gf, char const *inPath,
		char const *outPath) {
	FILE *outf;
	/* Mostly it is not useful to use the same input and output name.
	 This test is very easy and buggy and don't recognize different names
	 assigning the same file
	 */
	if (0 != strcmp("-", outPath) && 0 == strcmp(inPath, outPath)) {
		Log::info("Input file and Output file are the same. Abort.\n");
		return NULL;
	}

	/* open the wav/aiff/raw pcm or mp3 input file.  This call will
	 * open the file, try to parse the headers and
	 * set gf.samplerate, gf.num_channels, gf.num_samples.
	 * if you want to do your own file input, skip this call and set
	 * samplerate, num_channels and num_samples yourself.
	 */
	if (init_infile(gf, inPath) < 0) {
		Log::info("Can't init infile '%s'\n", inPath);
		return NULL;
	}
	if ((outf = init_outfile(outPath, lame_get_decode_only(gf))) == NULL) {
		Log::info("Can't init outfile '%s'\n", outPath);
		return NULL;
	}
	return outf;
}

int convertMp3ToWav(char* mp3Input, char* wavOutput) {
	lame_t lame = lame_init();
	lame_set_num_channels(lame, 1);
	lame_set_in_samplerate(lame, SAMPLE_RATE);
	lame_set_out_samplerate(lame, SAMPLE_RATE);
	lame_set_brate(lame, 128);
	lame_set_mode(lame, MONO);
	lame_set_decode_only(lame, 1);
	FILE * file = init_files(lame, mp3Input, wavOutput);
	if (file == NULL) {
		return STATUS_FAIL;
	}
	lame_decoder(lame, file, wavOutput);
	lame_close(lame);
	return STATUS_OK;
}

// Methods for processing block of file.
// Now, it hasn't used yet.

static int init_inputFile(lame_global_flags * gf, char const *inPath) {
	if (init_infile(gf, inPath) < 0) {
		Log::info("Can't init infile '%s'\n", inPath);
		return STATUS_FAIL;
	}
	return STATUS_OK;
}

static int lame_decoder_block(lame_t gfp, short* outBuffer) {
	short int Buffer[2][1152];
	int iread;
	int num_channels = lame_get_num_channels(gfp);

	if (!(num_channels >= 1 && num_channels <= 2)) {
		Log::info("Internal error.  Aborting.");
		return -1;
	}

	iread = get_audio16(gfp, Buffer); /* read in 'iread' samples */
	for (int i = 0; i < iread; i++) {
		if (num_channels == 1) {
			outBuffer[i] = Buffer[0][i];
		} else {
			outBuffer[i] = (Buffer[0][i] + Buffer[1][i]) / 2;
		}
	}

	return iread;
}

lame_t bgLame = NULL;

int initMp3Decoder(char* mp3Input) {
	bgLame = lame_init();
	lame_set_num_channels(bgLame, 1);
	lame_set_in_samplerate(bgLame, SAMPLE_RATE);
	lame_set_out_samplerate(bgLame, SAMPLE_RATE);
	lame_set_brate(bgLame, 128);
	lame_set_mode(bgLame, MONO);
	lame_set_decode_only(bgLame, 1);
	return init_inputFile(bgLame, mp3Input);
}

int convertBlockMp3ToWav(short* buffer) {
	return lame_decoder_block(bgLame, buffer);
}

void rewindMp3InputFile() {
	rewindInputFile();
}

void destroyMp3Decoder() {
	if (bgLame != NULL) {
		close_infile();
		lame_close(bgLame);
		bgLame = NULL;
	}
}

const int sizePerSecond = 16000; // with birate is 128Kb

/**
 * Cut file mp3 from startTime to endTime with constant bitrate.
 * Unit of startTime & endtime is second
 */
int cutMp3File(char* inFilePath, char* outFilePath, int beginTime,
		int endTime) {
	FILE* inFile = fopen(inFilePath, "rb");
	if (inFile == NULL) {
		return STATUS_FAIL;
	}

	FILE* outFile = fopen(outFilePath, "wb");

	int beginBytePos = sizePerSecond * beginTime;
	int endBytePos = sizePerSecond * endTime;

	if (beginBytePos > endBytePos) {
		fclose(inFile);
		fclose(outFile);
		return STATUS_FAIL;
	}

	fseek(inFile, beginBytePos, SEEK_SET);
	char buffer[sizePerSecond];
	size_t numRead = 0;

	while (beginBytePos < endBytePos) {
		numRead =
				(beginBytePos + sizePerSecond > endBytePos) ?
						endBytePos - beginBytePos : sizePerSecond;
		numRead = fread(buffer, sizeof(char), numRead, inFile);
		fwrite(buffer, sizeof(char), numRead, outFile);
		beginBytePos += numRead;
		if (numRead < sizePerSecond) {
			break;
		}
	}

	fclose(inFile);
	fclose(outFile);
	return STATUS_OK;

}

#else

int convertWavToMp3(char* wavInput, char* mp3Output) {
	return STATUS_NOT_SUPPORT;
}
int convertMp3ToWav(char* mp3Input, char* wavOutput) {
	return STATUS_NOT_SUPPORT;
}

#endif
