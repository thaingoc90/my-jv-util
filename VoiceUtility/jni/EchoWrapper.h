#ifndef _ECHO_EFFECT_H_
#define _ECHO_EFFECT_H_

void EchoEffect_init();

void EchoEffect_initProcess(double, double);

void EchoEffect_destroy();

int EchoEffect_processBlock(short** playerBuffer, int size);

#endif
