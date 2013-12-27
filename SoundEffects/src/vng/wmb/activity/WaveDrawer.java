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

	private boolean isCreated = false;
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

	public boolean GetDead2() {
		return mDrawThread.GetDead2();
	}

	/**
	 * restarts the thread
	 * 
	 * @param Is
	 *            the thread dead?
	 */
	public void Restart(boolean paramBoolean) {
		if (isCreated) {
			if (mDrawThread.getRun() && mDrawThread.GetDead2()) {
				mDrawThread.SetDead2(false);
				mDrawThread.setRun(false);
				if ((!paramBoolean) || (!mDrawThread.GetDead()))
					mHolder = getHolder();
				mHolder.addCallback(this);
			}
			Log.i(LOG_TAG, "Restart drawthread");
			mDrawThread = new DrawThread(mHolder, mContext, new Handler() {
				public void handleMessage(Message paramMessage) {
				}
			});
			mDrawThread.setName("DrawThread Restart 1 - "
					+ System.currentTimeMillis());
			mDrawThread.start();
			return;
		}
	}

	public void setRun(boolean paramBoolean) {
		mDrawThread.setRun(paramBoolean);
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
			Log.i(LOG_TAG, "Start thread");
			isCreated = true;
			mDrawThread.start();
			return;
		}
		while (true) {
			Log.i(LOG_TAG, "new Thread");
			Restart(false);
			Log.i(LOG_TAG, "new Thread2");
			return;
		}
	}

	/**
	 * Surface destroyd
	 */
	public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
		// int i = 1;
		while (true) {
			try {
				Log.i(LOG_TAG, "surfaceDestroyed");
				mDrawThread.join();
				return;
			} catch (InterruptedException localInterruptedException) {
			}
			// if (i == 0)
			// return;
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
		private boolean m_bDead = false;
		private boolean m_bDead2 = true;
		private boolean m_bRun = true;
		private boolean m_bSleep = false;
		private int m_iScaler = 8;

		// private int counter = 0;

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

		public boolean GetDead() {
			return m_bDead;
		}

		public boolean GetDead2() {
			return m_bDead2;
		}

		public boolean GetSleep() {
			return m_bSleep;
		}

		public void SetDead2(boolean paramBoolean) {
			m_bDead2 = paramBoolean;
		}

		public void SetSleeping(boolean paramBoolean) {
			m_bSleep = paramBoolean;
		}

		public boolean getRun() {
			return m_bRun;
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
					m_bDead = true;
					m_bDead2 = true;
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

		public void setRun(boolean paramBoolean) {
			m_bRun = paramBoolean;
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
