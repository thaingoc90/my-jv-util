package ntdn.voiceutil.service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ntdn.voiceutil.utils.Constants;
import ntdn.voiceutil.utils.Utils;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class JRecordService {

	private AudioRecord mRecorder;
	private int sampleRate;
	private int channel;
	private int sampleSize;
	private int bufferSize;
	private boolean isRecording = false;
	private String fileName;
	private Thread threadRecord = null;
	private byte[] buffer;
	private int channelAlias, bpsAlias;
	private long fileSize = 0;

	public JRecordService(int sampleRate, int channel, int sampleSize,
			String fileName) {
		this.sampleRate = sampleRate;
		this.channel = channel;
		this.sampleSize = sampleSize;
		this.fileName = fileName;
		isRecording = false;

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
					RandomAccessFile raf = null;
					try {
						raf = new RandomAccessFile(fileName, "rw");
						fileSize = 0;
						byte[] header = createWaveFileHeader(fileSize,
								sampleRate, channel, sampleSize);
						raf.write(header);
					} catch (Exception e) {
						try {
							raf.close();
						} catch (Exception ex) {
						} finally {
							raf = null;
						}
						return;
					}

					int read = 0;
					while (isRecording) {
						read = mRecorder.read(buffer, 0, bufferSize);
						if (read != AudioRecord.ERROR_INVALID_OPERATION
								&& read != AudioRecord.ERROR_BAD_VALUE) {
							ByteBuffer bb = ByteBuffer.allocate(read * 2);
							bb.order(ByteOrder.LITTLE_ENDIAN);
							for (int i = 0; i < read; i++) {
								bb.putShort(buffer[i]);
							}
							try {
								raf.write(bb.array());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						byte[] header = createWaveFileHeader(fileSize,
								sampleRate, channel, sampleSize);
						raf.seek(0);
						raf.write(header);
						raf.close();
					} catch (IOException e) {
					} finally {
						raf = null;
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
		int status = Constants.STATUS_OK;
		if (!isRecording || mRecorder == null) {
			isRecording = false;
			return status;
		}

		isRecording = false;
		try {
			mRecorder.stop();
		} catch (Exception e) {
			status = Constants.STATUS_FAIL;
			Utils.deleteFile(fileName);
		} finally {
			try {
				threadRecord = null;
				mRecorder.release();
			} catch (Exception e) {
			}
			mRecorder = null;
		}
		return status;
	}

	private byte[] createWaveFileHeader(long totalLenInByte, long sampleRate,
			int channels, int sampleSize) {

		long totalDataLen = totalLenInByte + 36;
		long byteRate = sampleRate * channels * sampleSize / 8;
		int blockAlign = channels * sampleSize / 8;

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
		header[24] = (byte) (sampleRate & 0xff);
		header[25] = (byte) ((sampleRate >> 8) & 0xff);
		header[26] = (byte) ((sampleRate >> 16) & 0xff);
		header[27] = (byte) ((sampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) blockAlign; // block align
		header[33] = 0;
		header[34] = (byte) sampleSize; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalLenInByte & 0xff);
		header[41] = (byte) ((totalLenInByte >> 8) & 0xff);
		header[42] = (byte) ((totalLenInByte >> 16) & 0xff);
		header[43] = (byte) ((totalLenInByte >> 24) & 0xff);

		return header;
	}
}
