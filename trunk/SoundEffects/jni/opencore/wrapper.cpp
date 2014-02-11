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

#include "interf_enc.h"
#include "codecs_v2/audio/gsm_amr/amr_nb/enc/src/amrencode.h"
#include <stdlib.h>

struct encoder_state {
	void* encCtx;
	void* pidSyncCtx;
};

void* Encoder_Interface_init(int dtx) {
	struct encoder_state* state = (struct encoder_state*) malloc(sizeof(struct encoder_state));
	AMREncodeInit(&state->encCtx, &state->pidSyncCtx, dtx);
	return state;
}

void Encoder_Interface_exit(void* s) {
	struct encoder_state* state = (struct encoder_state*) s;
	AMREncodeExit(&state->encCtx, &state->pidSyncCtx);
	free(state);
}

int Encoder_Interface_Encode(void* s, enum ModeAMR mode, const short* speech, unsigned char* out, int forceSpeech) {
	struct encoder_state* state = (struct encoder_state*) s;
	enum Frame_Type_3GPP frame_type = (enum Frame_Type_3GPP) mode;
	int ret = AMREncode(state->encCtx, state->pidSyncCtx, (enum Mode) mode, (Word16*) speech, out, &frame_type, AMR_TX_IETF);
	out[0] |= 0x04;
	return ret;
}

