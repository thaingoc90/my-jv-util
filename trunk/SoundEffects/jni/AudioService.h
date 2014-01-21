#ifndef _AUDIOSERVICE_H_
#define _AUDIOSERVICE_H_

int AudioService_init();

int AudioService_initRecorder();

void AudioService_destroyRecorder();

void AudioService_startRecord();

void AudioService_stopRecord();

int AudioService_initPlayer();

void AudioService_destroyPlayer();

void AudioService_playEffect();

void AudioService_startPlayer();

void AudioService_stopPlayer();

void AudioService_destroy();

#endif
