package vng.wmb.activity;

import vng.wmb.service.AudioService;
import android.media.AudioRecord;
import android.util.Log;

public class SoundSampler {
	private static short[] buffer;
	private AudioRecord ar;
	private int audioEncoding = 2;
	private int buffersizebytes;
	private int channelConfiguration = 16;
	private int mSamplesRead;
	private boolean m_bDead = false;
	private boolean m_bDead2 = true;
	private boolean m_bRun;
	private boolean m_bSleep = false;
	private StartActivity m_ma;
	private Thread recordingThread;
	private AudioService audioService;

	private static final String LOG_TAG = "SoundSampler";

	public SoundSampler(StartActivity paramMainActivity) {
		m_ma = paramMainActivity;
		m_bRun = false;
		audioService = StartActivity.audioServices;
	}

	public boolean GetDead2() {
		return m_bDead2;
	}

	public boolean GetSleep() {
		return m_bSleep;
	}

	/**
	 * Prepares to collect audiodata.
	 * 
	 * @throws Exception
	 */
	public void Init() throws Exception {
		try {
			if (!m_bRun) {
				ar = new AudioRecord(1, 44100, channelConfiguration,
						audioEncoding, AudioRecord.getMinBufferSize(44100,
								channelConfiguration, audioEncoding));
				if (ar.getState() != 1)
					return;
				Log.i(LOG_TAG, "State initialized");
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
			throw new Exception();
		}
		while (true) {
			buffersizebytes = AudioRecord.getMinBufferSize(44100,
					channelConfiguration, audioEncoding);
			Log.i(LOG_TAG, "BufferSizeBytes" + buffersizebytes);
			buffer = new short[buffersizebytes / 2];
			m_bRun = true;
			Log.i(LOG_TAG, "State unitialized!!!");
			return;
		}
	}

	/**
	 * Restarts the thread
	 */
	public void Restart() {
		while (true) {
			if (m_bDead2) {
				m_bDead2 = false;
				if (m_bDead) {
					m_bDead = false;
					Log.d(LOG_TAG, "Stop & release AudioRecord");
					if (ar != null) {
						ar.stop();
						ar.release();
					}
					try {
						Init();
					} catch (Exception e) {
						return;
					}
					StartRecording();
					StartSampling();
				}
				return;
			}
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException localInterruptedException) {
				localInterruptedException.printStackTrace();
			}
		}
	}

	public void Stop() {
		while (true) {
			if (m_bDead2) {
				m_bDead2 = true;
				if (m_bDead) {
					m_bDead = true;
					Log.d(LOG_TAG, "Stop & release AudioRecord");
					ar.stop();
					ar.release();
					ar = null;
				}
				return;
			}
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException localInterruptedException) {
				localInterruptedException.printStackTrace();
			}
		}
	}

	/**
	 * Reads the data-bufferts
	 */
	public void Sample() {
		mSamplesRead = ar.read(buffer, 0, buffersizebytes / 2);
	}

	public void setRun(Boolean paramBoolean) {
		m_bRun = paramBoolean;
		if (m_bRun)
			StartRecording();
		while (true) {

			StopRecording();
			return;
		}
	}

	public void SetSleeping(boolean paramBoolean) {
		m_bSleep = paramBoolean;
	}

	public void StartRecording() {
		if (ar == null) {
			try {
				Init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			StartRecording();
		} else {
			ar.startRecording();
		}
	}

	/**
	 * Collects audiodata and sends it back to the main activity
	 */
	public void StartSampling() {
		recordingThread = new Thread() {
			public void run() {
				while (true) {
					if (!m_bRun) {
						m_bDead = true;
						m_bDead2 = true;
						return;
					}
					Sample();
					m_ma.setBuffer(SoundSampler.buffer);
				}
			}
		};
		recordingThread.setName("RecordingThread");
		recordingThread.start();
	}

	public void StopRecording() {
		ar.stop();
	}

	public short[] getBuffer() {
		return buffer;
	}

}
