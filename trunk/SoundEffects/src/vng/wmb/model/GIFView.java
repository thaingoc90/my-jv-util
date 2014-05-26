package vng.wmb.model;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

public class GIFView extends View {

	private Movie movie;
	private long movieStart;
	private int gifId;

	public GIFView(Context context) {
		super(context);
	}

	public GIFView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int src_resource = attrs.getAttributeResourceValue(
				"http://schemas.android.com/apk/res/android", "src", 0);
		setGIFResource(src_resource);
	}

	public GIFView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		int src_resource = attrs.getAttributeResourceValue(
				"http://schemas.android.com/apk/res/android", "src", 0);
		setGIFResource(src_resource);
	}

	public void setGIFResource(int resId) {
		this.gifId = resId;
		initializeView();
	}

	public int getGIFResource() {
		return this.gifId;
	}

	private void initializeView() {
		if (gifId != 0) {
			InputStream is = getContext().getResources().openRawResource(gifId);
			movie = Movie.decodeStream(is);
			movieStart = 0;
			this.invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		super.onDraw(canvas);
		long now = android.os.SystemClock.uptimeMillis();
		if (movieStart == 0) {
			movieStart = now;
		}
		if (movie != null) {
			int relTime = (int) ((now - movieStart) % movie.duration());
			movie.setTime(relTime);
			movie.draw(canvas, getWidth() - movie.width(),
					getHeight() - movie.height());
			this.invalidate();
		}
	}

}