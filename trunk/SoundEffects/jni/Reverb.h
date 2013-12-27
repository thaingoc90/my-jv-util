/**********************************************************************

 Audacity: A Digital Audio Editor
 Audacity(R) is copyright (c) 1999-2013 Audacity Team.
 License: GPL v2.  See License.txt.

 Reverb.h
 Rob Sykes, Vaughan Johnson

 **********************************************************************/

#ifndef __AUDACITY_EFFECT_REVERB__
#define __AUDACITY_EFFECT_REVERB__

class wxSpinCtrl;
class WaveTrack;

struct Reverb_priv_t;

class EffectReverb {
public:
	EffectReverb();
	virtual ~EffectReverb() {
	}
	;

protected:

	// Processing:
	void Create(double rate, bool isStereo);
	bool ProcessOneBlock(long len, float * const * chans);
	void Delete();
	double mCurT0, mCurT1;
	Reverb_priv_t * mP;

	struct Params {
		double mRoomSize;
		double mDelay;
		double mReverberance;
		double mHfDamping;
		double mToneLow;
		double mToneHigh;
		double mWetGain;
		double mDryGain;
		double mStereoWidth;
		bool mWetOnly;
	};
	Params mParams;
};

#endif
