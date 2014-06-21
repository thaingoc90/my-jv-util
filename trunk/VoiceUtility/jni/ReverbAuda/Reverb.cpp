/**********************************************************************

 Audacity: A Digital Audio Editor
 Audacity(R) is copyright (c) 1999-2013 Audacity Team.
 License: GPL v2.  See License.txt.

 Reverb.cpp
 Rob Sykes, Vaughan Johnson

 ******************************************************************//**

 \class EffectReverb
 \brief A reverberation effect

 *//****************************************************************//**

 \class ReverbDialogue
 \brief A configuration class used by effect, EffectReverb.

 *//*******************************************************************/

#include "Reverb.h"
#include "Reverb_libSoX.h"

#define BLOCK 0x5000u
//
// EffectReverb
//

struct Reverb_priv_t {
	size_t ichannels, ochannels;
	struct {
		reverb_t reverb;
		float * dry, *wet[2];
	} chan[2];
};

EffectReverb::EffectReverb() {
	mP = (Reverb_priv_t *) calloc(sizeof *mP, 1);
	setParam(0, 0, 0, 0, 0, 0, 0, 0);
}

void EffectReverb::setParam(double mRoomSize, double mDelay,
		double mReverberance, double mHfDamping, double mToneLow,
		double mToneHigh, double mWetGain, double mDryGain) {
	mParams.mRoomSize = mRoomSize;
	mParams.mDelay = mDelay;
	mParams.mReverberance = mReverberance;
	mParams.mHfDamping = mHfDamping;
	mParams.mToneLow = mToneLow;
	mParams.mToneHigh = mToneHigh;
	mParams.mWetGain = mWetGain;
	mParams.mDryGain = mDryGain;
	mParams.mWetOnly = false;
	mParams.mStereoWidth = 0;
}

void EffectReverb::InitProcess(double rate, bool isStereo) {
	size_t i;
	mP->ichannels = mP->ochannels = 1 + isStereo;
	for (i = 0; i < mP->ichannels; ++i)
		reverb_create(&mP->chan[i].reverb, rate, mParams.mWetGain,
				mParams.mRoomSize, mParams.mReverberance, mParams.mHfDamping,
				mParams.mDelay, mParams.mStereoWidth * isStereo,
				mParams.mToneLow, mParams.mToneHigh, BLOCK, mP->chan[i].wet);
}

bool EffectReverb::ProcessOneBlock(long len0, float * const * chans0) {
	float * chans[2];
	chans[0] = chans0[0], chans[1] = chans0[1];
	size_t c, i, w, len = len0;
	float const dryMult(mParams.mWetOnly ? 0 : dB_to_linear(mParams.mDryGain));
	while (len) {
		size_t len1 = min(len, (size_t) BLOCK);
		for (c = 0; c < mP->ichannels; ++c) {
			mP->chan[c].dry = (float *) fifo_write(
					&mP->chan[c].reverb.input_fifo, len1, chans[c]);
			reverb_process(&mP->chan[c].reverb, len1);
		}
		if (mP->ichannels == 2)
			for (i = 0; i < len1; ++i)
				for (w = 0; w < 2; ++w)
					chans[w][i] = dryMult * mP->chan[w].dry[i]
							+ .5
									* (mP->chan[0].wet[w][i]
											+ mP->chan[1].wet[w][i]);
		else
			for (i = 0; i < len1; ++i)
				for (w = 0; w < mP->ochannels; ++w)
					chans[0][i] = dryMult * mP->chan[0].dry[i]
							+ mP->chan[0].wet[w][i];
		len -= len1;
		for (c = 0; c < mP->ichannels; chans[c++] += len1)
			;
	}
	return true;
}

void EffectReverb::Delete() {
	for (size_t i = 0; i < mP->ichannels; reverb_delete(&mP->chan[i++].reverb))
		;
	free(mP);
}

// Most of what follows should really be provided by Audacity framework classes:
//vvv Not sure what you mean by that, Rob. A la EffectSimpleMono, e.g.?
