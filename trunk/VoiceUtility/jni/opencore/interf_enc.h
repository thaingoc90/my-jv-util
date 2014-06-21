/* ------------------------------------------------------------------
 * Copyright (C) 2009 Martin Storsjo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 * -------------------------------------------------------------------
 */

#ifndef OPENCORE_AMRNB_INTERF_ENC_H
#define OPENCORE_AMRNB_INTERF_ENC_H

#ifdef __cplusplus
extern "C" {
#endif

enum ModeAMR {
	AMRNB475 = 0,/* 4.75 kbps */
	AMRNB515,    /* 5.15 kbps */
	AMRNB59,     /* 5.90 kbps */
	AMRNB67,     /* 6.70 kbps */
	AMRNB74,     /* 7.40 kbps */
	AMRNB795,    /* 7.95 kbps */
	AMRNB102,    /* 10.2 kbps */
	AMRNB122,    /* 12.2 kbps */
	AMRNBDTX,    /* DTX       */
};

void* Encoder_Interface_init(int dtx);
void Encoder_Interface_exit(void* state);
int Encoder_Interface_Encode(void* state, enum ModeAMR mode, const short* speech, unsigned char* out, int forceSpeech);

#ifdef __cplusplus
}
#endif

#endif
