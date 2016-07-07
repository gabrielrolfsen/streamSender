package views;

import com.example.streamsender.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {

	final static String IP_ADDR = "IpAddr";
	final static String PORT = "Port";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button btnConnect = (Button) findViewById(R.id.btnConnect);
		final EditText edtIpAddr = (EditText) findViewById(R.id.edtIpAddress);
		// final EditText edtPort = (EditText) findViewById(R.id.edtPort);

		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
				intent.putExtra(IP_ADDR, edtIpAddr.getText().toString());
				// intent.putExtra(PORT, edtPort.getText().toString());
				intent.putExtra(PORT, "1234");
				startActivity(intent);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
