package moe.bsod.ccattack;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;


public class MainActivity extends AppCompatActivity {

	public static Handler uiUpdate;
	public Button btn_start;
	public EditText et_url;
	public EditText et_thread;
	public EditText et_looptime;
	public TextView round;
	public TextView req;
	public TextView fail;

	private ClientMan cm;

	private long round_count = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_start = (Button) findViewById(R.id.btn_start);
		et_url = (EditText) findViewById(R.id.et_url);
		et_thread = (EditText) findViewById(R.id.et_thread);
		et_looptime = (EditText) findViewById(R.id.et_looptime);
		round = (TextView) findViewById(R.id.round_count);
		req = (TextView) findViewById(R.id.request_count);
		fail = (TextView) findViewById(R.id.failure_count);

		uiUpdate = new Handler() {
			@Override
			public void handleMessage(Message message) {

				switch (message.what) {
					case ClientMan.CLIENTMAN_START:
						btn_start.setText(R.string.btn_onStart);
						btn_start.setEnabled(true);
						break;

					case ClientMan.CLIENTMAN_DIED:
						round_count = 0;
						round.setText("0");
						req.setText("0");
						fail.setText("0");
						btn_start.setText(R.string.btn_onStop);
						btn_start.setEnabled(true);
						break;

					case ClientMan.CLIENTMAN_UPDATE_DATA:
						Pair<Long, Long> pair = (Pair)message.obj;
						// TODO: proceed the data and update the ui.
						round.setText(String.valueOf(++round_count));
						req.setText(String.valueOf(pair.first));
						fail.setText(String.valueOf(pair.second));

						break;
				}
			}
		};

		btn_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (btn_start.getText().equals(getResources().getString(R.string.btn_onStart))) {
					btn_start.setEnabled(false);
					btn_start.setText(R.string.btn_onwait);

					cm.terminate();
				} else {

					btn_start.setEnabled(false);
					btn_start.setText(R.string.btn_onwait);

					String url = et_url.getText().toString();
					String thread = et_thread.getText().toString();
					String loop_count = et_looptime.getText().toString();

					try {

						cm = new ClientMan(uiUpdate, url,
								"".equals(thread) ? 1 : Integer.valueOf(thread),
								"".equals(loop_count) ? 0 : Integer.valueOf(loop_count));

						cm.start();

					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
