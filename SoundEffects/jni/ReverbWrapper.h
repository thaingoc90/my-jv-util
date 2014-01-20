#ifndef _REVERBEFFECT_H_
#define _REVERBEFFECT_H_

void ReverbEffect_init();

void ReverbEffect_initProcess(double, double, double, double, double, double,
		double, double);

void ReverbEffect_destroy();

int ReverbEffect_processBlock(short** playerBuffer, int size);

#endif
