package vng.wmb.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EffectActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.effect, menu);
		return true;
	}

}
