#include "NSWrapper.h"
#include "Utils.h"
#include "Log.h"
#ifdef _FLOATING_POINT_
#define W_WebRtcNs_Create WebRtcNs_Create
#define W_WebRtcNs_Init WebRtcNs_Init
#define W_WebRtcNs_Free WebRtcNs_Free
#define W_WebRtcNs_Process WebRtcNs_Process
#define W_WebRtcNs_set_policy WebRtcNs_set_policy
#else
#define W_WebRtcNs_Create WebRtcNsx_Create
#define W_WebRtcNs_Init WebRtcNsx_Init
#define W_WebRtcNs_Free WebRtcNsx_Free
#define W_WebRtcNs_Process WebRtcNsx_Process
#define W_WebRtcNs_set_policy WebRtcNsx_set_policy
#endif
W_NsHandle* NS_inst = NULL;
unsigned NS_clock_rate = 0;

/*
 * This function creates an instance & initializes a NS instance and has to be called before any other
 * processing is made.
 *
 * Input:
 *      - NS_inst       : Instance that should be initialized
 *      - fs            : sampling frequency
 *
 * Output:
 *      - NS_inst       : Initialized instance
 *
 * Return value         :  0 - Ok
 *                        1 - Error
 */
int NS_init(int mode, unsigned clock_rate) {
	Log::info("Init NS");
	int res = W_WebRtcNs_Create(&NS_inst);
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}
	res = W_WebRtcNs_Init(NS_inst, clock_rate);
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}

	NS_clock_rate = clock_rate;

	if (mode < 0 || mode > 3) {
		mode = 3;
	}
	res = NS_set_policy(NS_inst, mode);
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}

	return STATUS_OK;
}

/*
 * This function frees the dynamic memory of a specified noise suppression
 * instance.
 *
 * Input:
 *      - NS_inst       : Pointer to NS instance that should be freed
 *
 * Return value         :  0 - Ok
 *                         1 - Error
 */
int NS_destroy() {
	Log::info("Destroy NS");
	NS_clock_rate = 0;
	if (NS_inst) {
		int res = W_WebRtcNs_Free(NS_inst);
		NS_inst = NULL;
		if (res != STATUS_OK) {
			return STATUS_FAIL;
		}
	}
	return STATUS_OK;
}

/*
 * This changes the aggressiveness of the noise suppression method.
 *
 * Input:
 *      - NS_inst       : Noise suppression instance.
 *      - mode          : 0: Mild, 1: Medium , 2: Aggressive
 *
 * Output:
 *      - NS_inst       : Updated instance.
 *
 * Return value         :  0 - Ok
 *                         1 - Error
 */
int NS_set_policy(W_NsHandle* NS_inst, int mode) {
	int res = W_WebRtcNs_set_policy(NS_inst, mode);
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}
	return STATUS_OK;
}

/*
 * This functions does Noise Suppression for the inserted speech frame. The
 * input and output signals should always be 10ms (80 or 160 samples).
 *
 * Input
 *      - NS_inst       : Noise suppression instance.
 *      - spframe       : Pointer to speech frame buffer for L band
 *      - spframe_H     : Pointer to speech frame buffer for H band (frequency >= 32K)
 *      				  (Must be not null if Fre >= 32K)
 *
 * Output:
 *      - NS_inst       : Updated NS instance
 *      - outframe      : Pointer to output frame for L band
 *      - outframe_H    : Pointer to output frame for H band
 *      				  (Must be not null if Fre >= 32K)
 *
 * Return value         :  0 - OK
 *                         1 - Error
 */
int NS_Process(short* rec_frm, unsigned samples_per_frame) {
	int blockLen10ms = 160, i, status;
	short tmp_frame[samples_per_frame], tmp_frame_H[samples_per_frame];

	if (NS_clock_rate == 8000) {
		blockLen10ms = 80;
	} else if (NS_clock_rate == 16000 || NS_clock_rate == 32000) {
		blockLen10ms = 160;
	} else {
		return STATUS_FAIL;
	}

	for (i = 0; i < samples_per_frame; i += blockLen10ms) {
		if (NS_inst) {
			if (NS_clock_rate == 32000) {
				status = W_WebRtcNs_Process(NS_inst, (short*) (&rec_frm[i]),
						(short*) (&rec_frm[i]), (short*) (&tmp_frame[i]),
						(short*) (&tmp_frame_H[i]));
			} else {
				status = W_WebRtcNs_Process(NS_inst, (short*) (&rec_frm[i]),
						NULL, (short*) (&tmp_frame[i]), NULL);
			}
			if (status != 0) {
				Log::error("Error suppressing noise");
				return STATUS_FAIL;
			}
		}
	}
	memcpy(rec_frm, tmp_frame, samples_per_frame * 2);
	return STATUS_OK;
}
