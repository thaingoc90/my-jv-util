#ifndef _NS_WRAPPER_H_
#define _NS_WRAPPER_H_

//#define _FLOATING_POINT_ 1

#ifdef _FLOATING_POINT_

#include "ns/noise_suppression.h"
#define W_NsHandle NsHandle

#else

#include "ns/noise_suppression_x.h"
#define W_NsHandle NsxHandle

#endif

int NS_init(W_NsHandle**, int);
int NS_destroy(W_NsHandle*);
int NS_set_policy(W_NsHandle*, int);
int NS_Process(W_NsHandle* NS_inst, short* spframe, short* spframe_H,
		short* outframe, short* outframe_H);

#endif
