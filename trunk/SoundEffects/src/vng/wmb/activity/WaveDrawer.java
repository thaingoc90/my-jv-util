package vng.wmb.activity;

/**
 *  This is the drawer for the visualizer
 **/

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WaveDrawer extends SurfaceView implements SurfaceHolder.Callback {

	private Context mContext;
	private DrawThread mDrawThread;
	private SurfaceHolder mHolder;

	private static final String LOG_TAG = "WaveDrawer";

	public WaveDrawer(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		Log.i(LOG_TAG, "CDrawer()");
		mHolder = getHolder();
		mContext = paramContext;
		mHolder.addCallback(this);
		mDrawThread = new DrawThread(mHolder, paramContext, new Handler() {
			public void handleMessage(Message paramMessage) {
			}
		});
		mDrawThread.setName("DrawThread Constructor -"
				+ System.currentTimeMillis());
		setFocusable(true);
	}

	/**
	 * restarts the thread
	 * 
	 * @param Is
	 *            the thread dead?
	 */
	public void Restart(boolean paramBoolean) {
		Log.i(LOG_TAG, "Restart drawthread");
		if (mDrawThread.getRun()) {
			mDrawThread.setRun(false);
			if (!paramBoolean) {
				mHolder = getHolder();
			}
			mHolder.addCallback(this);
		}

		mDrawThread = new DrawThread(mHolder, mContext, new Handler() {
			public void handleMessage(Message paramMessage) {
			}
		});
		mDrawThread.setName("DrawThread Restart - "
				+ System.currentTimeMillis());
		mDrawThread.start();
	}

	public DrawThread getThread() {
		return mDrawThread;
	}

	/**
	 * Called when there's a change in the surface
	 */
	public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1,
			int paramInt2, int paramInt3) {
		mDrawThread.setSurfaceSize(paramInt2, paramInt3);
	}

	/**
	 * Creates the surface
	 */
	public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
		Log.i(LOG_TAG, "surfaceCreated");
		if (mDrawThread.getRun()) {
			mDrawThread.start();
			return;
		}
		while (true) {
			Restart(false);
			return;
		}
	}

	/**
	 * Surface destroyd
	 */
	public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
		while (true) {
			try {
				Log.i(LOG_TAG, "surfaceDestroyed");
				mDrawThread.setRun(false);
				mDrawThread.join();
				return;
			} catch (InterruptedException localInterruptedException) {
			}
		}
	}

	/**
	 * The Drawer Thread, subclass to cDrawer class We want to keep most of this
	 * process in a background thread, so the UI don't hang
	 * 
	 */
	class DrawThread extends Thread {
		private Paint mBackPaint;
		private Bitmap mBackgroundImage;
		private short[] mBuffer;
		private int mCanvasHeight = 1;
		@SuppressWarnings("unused")
		private int mCanvasWidth = 1;
		private Paint mLinePaint;
		// private int mPaintCounter = 0;
		private SurfaceHolder mSurfaceHolder;
		private boolean m_bRun = true;
		private int m_iScaler = 8;

		/**
		 * Instance the Thread All the parameters i handled by the cDrawer class
		 * 
		 * @param paramContext
		 * @param paramHandler
		 * @param arg4
		 */
		public DrawThread(SurfaceHolder paramContext, Context paramHandler,
				Handler arg4) {
			mSurfaceHolder = paramContext;
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(255, 255, 0, 0);
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(255, 255, 0, 255);
			mBackPaint = new Paint();
			mBackPaint.setAntiAlias(true);
			mBackPaint.setARGB(255, 0, 0, 0);
			mBuffer = new short[2048];
			mBackgroundImage = Bitmap.createBitmap(1, 1,
					Bitmap.Config.ARGB_8888);

		}

		/**
		 * Allow you to change the size of the waveform displayed on the screen
		 * Or scale of you so will
		 * 
		 * @return returns a new scale value
		 */
		public int ChangeSensitivity() {
			m_iScaler = (2 + m_iScaler);
			if (m_iScaler > 20)
				m_iScaler = 1;
			return m_iScaler;
		}

		public boolean getRun() {
			return m_bRun;
		}

		public void setRun(boolean paramBoolean) {
			m_bRun = paramBoolean;
		}

		/**
		 * Calculate and draws the line
		 * 
		 * @param Canvas
		 *            to draw on, handled by cDrawer class
		 */
		public void doDraw(Canvas paramCanvas) {
			if (mCanvasHeight == 1)
				mCanvasHeight = paramCanvas.getHeight();
			paramCanvas.drawPaint(mBackPaint);
			/**
			 * Set some base values as a starting point This could be considerd
			 * as a part of the calculation process
			 */
			int height = paramCanvas.getHeight();
			// Divide by 2 since we only have audio data in the first half
			// (mBuffer.length/2) of
			// audio buffer (initialized AudioRecord with buffer half of audio
			// buffer)
			// int BuffIndex = (mBuffer.length / 2 - paramCanvas.getWidth()) /
			// 2;
			// Fix for allocate actual buffer
			int BuffIndex = mBuffer.length / 2 - paramCanvas.getWidth() / 2;
			int width = paramCanvas.getWidth();
			int mBuffIndex = BuffIndex;
			int scale = height / m_iScaler;
			int StratX = 0;
			if (StratX >= width) {
				paramCanvas.save();
				return;
			}
			/**
			 * Here is where the real calculations is taken in to action In this
			 * while loop, we calculate the start and stop points for both X and
			 * Y The line is then drawer to the canvas with drawLine method
			 */
			while (StratX < width - 1) {

				if (mBuffIndex >= mBuffer.length || mBuffIndex <= 0) {
					Log.i("WaveDrawer", " BuffLength " + mBuffer.length
							+ " BuffIndex " + mBuffIndex);
					break;
				}
				int StartBaseY = mBuffer[(mBuffIndex - 1)] / scale;

				int StopBaseY = mBuffer[mBuffIndex] / scale;
				if (StartBaseY > height / 2) {
					StartBaseY = 2 + height / 2;
					int checkSize = height / 2;
					if (StopBaseY <= checkSize)
						return;
					StopBaseY = 2 + height / 2;
				}

				int StartY = StartBaseY + height / 2;
				int StopY = StopBaseY + height / 2;
				paramCanvas.drawLine(StratX, StartY, StratX + 1, StopY,
						mLinePaint);
				mBuffIndex++;
				StratX++;
				int checkSize_again = -1 * (height / 2);
				if (StopBaseY >= checkSize_again)
					continue;
				StopBaseY = -2 + -1 * (height / 2);
			}
		}

		/**
		 * Updated the Surface and redraws the new audio-data
		 */
		public void run() {
			while (true) {
				if (!m_bRun) {
					Log.i(LOG_TAG, "Goodbye Drawthread");
					return;
				}
				Canvas localCanvas = null;
				try {
					localCanvas = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						if (localCanvas != null)
							doDraw(localCanvas);

					}
				} finally {
					if (localCanvas != null)
						mSurfaceHolder.unlockCanvasAndPost(localCanvas);
				}
			}
		}

		public void setBuffer(short[] paramArrayOfShort) {
			synchronized (mBuffer) {
				mBuffer = paramArrayOfShort;
				return;
			}
		}

		public void setSurfaceSize(int paramInt1, int paramInt2) {
			synchronized (mSurfaceHolder) {
				mCanvasWidth = paramInt1;
				mCanvasHeight = paramInt2;
				mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
						paramInt1, paramInt2, true);
				return;
			}
		}
	}
}
