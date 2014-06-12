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

int NS_init(int mode, unsigned clock_rate);
int NS_destroy();
int NS_set_policy(W_NsHandle* NS_inst, int mode);
int NS_Process(short* rec_frm, unsigned samples_per_frame);

#endif
