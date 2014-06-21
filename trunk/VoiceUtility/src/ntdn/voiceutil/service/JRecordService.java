package ntdn.voiceutil.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ntdn.voiceutil.utils.Constants;
import ntdn.voiceutil.utils.Utils;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

public class JRecordService {

	private AudioRecord mRecorder;
	private int sampleRate;
	private int channel;
	private int sampleSize;
	private int bufferSize;
	private boolean isRecording = false;
	private String fileName;
	private String fileTemp;
	private Thread threadRecord = null;
	private byte[] buffer;
	private int channelAlias, bpsAlias;

	public JRecordService(int sampleRate, int channel, int sampleSize,
			String fileName) {
		this.sampleRate = sampleRate;
		this.channel = channel;
		this.sampleSize = sampleSize;
		this.fileName = fileName;
		isRecording = false;
		fileTemp = Environment.getExternalStorageDirectory().getAbsolutePath();
		fileTemp = fileTemp + "/TempAudio.pcm";

		if (channel == 2) {
			channelAlias = AudioFormat.CHANNEL_IN_STEREO;
		} else {
			channelAlias = AudioFormat.CHANNEL_IN_MONO;
		}

		if (sampleSize == 8) {
			bpsAlias = AudioFormat.ENCODING_PCM_8BIT;
		} else {
			bpsAlias = AudioFormat.ENCODING_PCM_16BIT;
		}

		bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelAlias,
				bpsAlias);
		if (bufferSize >= 0) {
			buffer = new byte[bufferSize];
		}
	}

	public int startRecord() {
		try {
			if (!isRecording) {
				return Constants.STATUS_OK;
			}
			isRecording = true;
			mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
					sampleRate, channelAlias, bpsAlias, bufferSize);
			mRecorder.startRecording();
			threadRecord = new Thread(new Runnable() {
				@Override
				public void run() {
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(fileTemp);
					} catch (FileNotFoundException e) {
						return;
					}

					if (fos != null) {
						int read = 0;
						while (isRecording) {
							read = mRecorder.read(buffer, 0, bufferSize);
							if (read != AudioRecord.ERROR_INVALID_OPERATION) {
								try {
									fos.write(buffer);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
					}
				}

			});
			threadRecord.start();
			return Constants.STATUS_OK;
		} catch (Exception e) {
			isRecording = false;
			return Constants.STATUS_FAIL;
		}
	}

	public int stopRecord() {
		try {
			if (isRecording) {
				isRecording = false;
				if (mRecorder != null) {
					mRecorder.stop();
					threadRecord = null;
					mRecorder.release();
					mRecorder = null;
				}
				copyWaveFile(fileTemp, fileName);
				Utils.deleteFile(fileTemp);
			}
			return Constants.STATUS_OK;
		} catch (Exception e) {
			return Constants.STATUS_FAIL;
		}
	}

	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long byteRate = sampleRate * sampleSize * channel / 8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			writeWaveFileHeader(out, totalAudioLen, totalDataLen, sampleRate,
					channel, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = (byte) sampleSize; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}
}
