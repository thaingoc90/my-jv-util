package ntdn.voiceutil.service;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class JPlayerService {

	private boolean isPlaying = false;
	private String fileName;
	private MediaPlayer mPlayer = null;

	public JPlayerService(String fileName) {
		this.fileName = fileName;
		isPlaying = false;
	}

	public void startPlaying() {
		if (isPlaying) {
			return;
		}
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				return;
			}
			isPlaying = true;
			mPlayer = new MediaPlayer();
			mPlayer.setDataSource(fileName);
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					stopPlaying();
				}
			});
			mPlayer.prepare();
			mPlayer.start();

		} catch (Exception e) {
			stopPlaying();
		}
	}

	public void stopPlaying() {
		if (!isPlaying) {
			return;
		}
		isPlaying = false;
		try {
			if (mPlayer != null) {
				mPlayer.stop();
				mPlayer.release();
				mPlayer = null;
			}
		} catch (Exception e) {
		}
	}
}
