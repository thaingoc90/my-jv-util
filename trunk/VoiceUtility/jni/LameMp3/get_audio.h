/*
 *    Get Audio routines include file
 *
 *    Copyright (c) 1999 Albert L Faber
 *                  2010 Robert Hegemann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

#ifndef LAME_GET_AUDIO_H
#define LAME_GET_AUDIO_H
#include "lame.h"
#include <stdio.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef enum sound_file_format_e {
	sf_unknown, sf_raw, sf_wave, sf_aiff, sf_mp1, /* MPEG Layer 1, aka mpg */
	sf_mp2, /* MPEG Layer 2 */
	sf_mp3, /* MPEG Layer 3 */
	sf_mp123, /* MPEG Layer 1,2 or 3; whatever .mp3, .mp2, .mp1 or .mpg contains */
	sf_ogg
} sound_file_format;

int is_mpeg_file_format(int input_format);

int init_infile(lame_t gfp, char const * inPath);
int samples_to_skip_at_start(void);
int samples_to_skip_at_end(void);
void close_infile(void);
int get_audio(lame_t gfp, int buffer[2][1152]);
int get_audio16(lame_t gfp, short buffer[2][1152]);
int get_audio_float(lame_t gfp, float buffer[2][1152]);
int get_audio_double(lame_t gfp, double buffer[2][1152]);
hip_t get_hip(void);

FILE *init_outfile(char const *outPath, int decode);
int WriteWaveHeader(FILE * const fp, int pcmbytes, int freq, int channels,
		int bits);
void put_audio16(FILE* outf, short Buffer[2][1152], int iread, int nch);
void rewindInputFile();

/*
 struct AudioReader;
 typedef struct AudioReader* AudioReader;

 AudioReader ar_open(lame_t gfp, char const* inPath);
 int     ar_samplesToSkipAtStart(AudioReader ar);
 int     ar_samplesToSkipAtEnd(AudioReader ar);
 void    ar_close(AudioReader ar);
 int     ar_readInt(AudioReader ar, lame_t gfp, int buffer[2][1152]);
 int     ar_readShort(AudioReader ar, lame_t gfp, short buffer[2][1152]);
 int     ar_readFloat(AudioReader ar, lame_t gfp, float buffer[2][1152]);

 struct AudioWriter;
 typedef struct AudioWriter* AudioWriter;

 AudioWriter aw_open(lame_t gfp, char const* outPath, int pcmbystes, int freq, int channels, int bits);
 int     aw_writeWaveHeader(AudioWriter aw);
 int     aw_write(AudioWriter aw, short buffer[2][1152], int n);
 int     aw_write(AudioWriter aw, float buffer[2][1152], int n);

 */

extern size_t sizeOfOldTag(lame_t gf);
extern unsigned char* getOldTag(lame_t gf);

typedef struct UiConfig {
	int silent; /* Verbosity */
	int brhist;
	int print_clipping_info; /* print info whether waveform clips */
	float update_interval; /* to use Frank's time status display */
} UiConfig;
typedef struct ReaderConfig {
	sound_file_format input_format;
	int swapbytes; /* force byte swapping   default=0 */
	int swap_channel; /* 0: no-op, 1: swaps input channels */
	int input_samplerate;
} ReaderConfig;

typedef struct WriterConfig {
	int flush_write;
} WriterConfig;

typedef struct DecoderConfig {
	int mp3_delay; /* to adjust the number of samples truncated during decode */
	int mp3_delay_set; /* user specified the value of the mp3 encoder delay to assume for decoding */
	int disable_wav_header;
	mp3data_struct mp3input_data;
} DecoderConfig;
static DecoderConfig global_decoder;

static UiConfig global_ui_config = { 0, 0, 0, 0 };
static ReaderConfig global_reader = { sf_unknown, 0, 0, 0 };
static WriterConfig global_writer = { 0 };

static int error_printf(const char *format, ...) {
	va_list args;
	int ret;

	va_start(args, format);
	ret = 0;
	va_end(args);

	return ret;
}

static int console_printf(const char *format, ...) {
	va_list args;
	int ret;

	va_start(args, format);
	ret = 0;
	va_end(args);

	return ret;
}

//static wchar_t *mbsToUnicode(const char *mbstr, int code_page) {
//	int n = MultiByteToWideChar(code_page, 0, mbstr, -1, NULL, 0);
//	wchar_t* wstr = malloc(n * sizeof(wstr[0]));
//	if (wstr != 0) {
//		n = MultiByteToWideChar(code_page, 0, mbstr, -1, wstr, n);
//		if (n == 0) {
//			free(wstr);
//			wstr = 0;
//		}
//	}
//	return wstr;
//}

//wchar_t *utf8ToUnicode(const char *mbstr) {
//	return mbsToUnicode(mbstr, CP_UTF8);
//}

static FILE* lame_fopen(char const* file, char const* mode) {
	FILE* fh = 0;
//	wchar_t* wfile = utf8ToUnicode(file);
//	wchar_t* wmode = utf8ToUnicode(mode);
//	if (wfile != 0 && wmode != 0) {
////		fh = _wfopen(wfile, wmode);
//	} else {
		fh = fopen(file, mode);
//	}
//	free(wfile);
//	free(wmode);
	return fh;
}

static int lame_set_stream_binary_mode(FILE * const fp) {
#if   defined __EMX__
	_fsetmode(fp, "b");
#elif defined __BORLANDC__
	setmode(_fileno(fp), O_BINARY);
#elif defined __CYGWIN__
	setmode(fileno(fp), _O_BINARY);
#elif defined _WIN32
	_setmode(_fileno(fp), _O_BINARY);
#else
	(void) fp; /* doing nothing here, silencing the compiler only. */
#endif
	return 0;
}

#ifdef __cplusplus
}
#endif

#endif /* ifndef LAME_GET_AUDIO_H */
