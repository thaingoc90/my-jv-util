#ifndef _AUDIOSERVICE_H_
#define _AUDIOSERVICE_H_

int AudioService_init();

int AudioService_initRecorder();

void AudioService_destroyRecorder();

int AudioService_startRecord(char*);

int AudioService_stopRecord();

int AudioService_initPlayer();

void AudioService_destroyPlayer();

int AudioService_playEffect();

int AudioService_startPlayer();

int AudioService_stopPlayer();

void AudioService_destroy();

#endif
