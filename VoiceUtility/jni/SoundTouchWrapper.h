#ifndef _SOUNDTOUCHWRAPPER_H_
#define _SOUNDTOUCHWRAPPER_H_

void SoundTouchEffect_init();

void SoundTouchEffect_destroy();

void SoundTouchEffect_initProcess(double, double, double);

int SoundTouchEffect_processBlock(short** playerBuffer, int size);

#endif
