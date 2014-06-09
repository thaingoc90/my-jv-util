#ifndef _BOOST_AUDIO_WRAPPER_H_
#define _BOOST_AUDIO_WRAPPER_H_

int BoostAudioWrapper_processBlockByGain(short* buffer, int size,
		double gain);

int BoostAudioWrapper_processBlockByDb(short* buffer, int size,
		double decibel);

#endif
