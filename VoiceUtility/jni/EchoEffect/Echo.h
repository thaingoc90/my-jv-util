/**********************************************************************

 Audacity: A Digital Audio Editor

 Echo.h

 Dominic Mazzoni
 Vaughan Johnson (dialog)

 **********************************************************************/

#ifndef __AUDACITY_EFFECT_ECHO__
#define __AUDACITY_EFFECT_ECHO__

class EffectEcho {

public:

	EffectEcho(int sampleRate);
	void init(float delay, float decay);
	int ProcessOneBlock(float* data, int count);

private:

	float delay;
	float decay;
	int sample_rate;

};

#endif // __AUDACITY_EFFECT_ECHO__
