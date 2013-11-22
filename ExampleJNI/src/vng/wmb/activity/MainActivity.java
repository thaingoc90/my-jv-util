package vng.wmb.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText mUIName, mUIAddress, mUISalary;
	private InfoService infoService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mUIName = (EditText) findViewById(R.id.editText1);
		mUIAddress = (EditText) findViewById(R.id.editText2);
		mUISalary = (EditText) findViewById(R.id.editText3);

		infoService = new InfoService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onSetValue(View view) {
		String name = mUIName.getText().toString();
		String address = mUIAddress.getText().toString();
		String salaryStr = mUISalary.getText().toString();
		int salary = 0;
		try {
			salary = Integer.parseInt(salaryStr);
		} catch (NumberFormatException e) {
		}
		if (name.length() <= 0) {
			return;
		}
		if (address.length() <= 0 || salary <= 0) {
			displayError("Input Value is invalid");
			return;
		}
		InfoType info = new InfoType(name, address, salary);
		try {
			infoService.setInfo(name, info);
		} catch (Exception e) {
			displayError("Error when processing");
		}
	}

	public void onGetValue(View view) {
		String name = mUIName.getText().toString();
		if (name.length() <= 0) {
			return;
		}
		InfoType info = infoService.getInfo(name);
		if (info != null) {
			mUIAddress.setText(info.getAddress());
			mUISalary.setText(String.valueOf(info.getSalary()));
		} else {
			displayError("Don't exist name.");
		}
	}

	public void onDeleteValue(View view) {
		String name = mUIName.getText().toString();
		if (name.length() <= 0) {
			return;
		}
		infoService.deleteInfo(name);
		mUIAddress.setText("");
		mUISalary.setText("");
		displayError("Remove successfully");
	}

	public void onBackup(View view) {
		int error = infoService.writeFile();
		if (error != 0) {
			displayError("Error when processing");
		} else {
			displayError("Backup successfully");
		}
	}

	private void displayError(String pError) {
		Toast.makeText(getApplicationContext(), pError, Toast.LENGTH_LONG)
				.show();
	}
}
