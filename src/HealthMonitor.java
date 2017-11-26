import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HealthMonitor {
	List<String> allUrls;
	Set<String> availableUrls;
	
	public HealthMonitor(String[] urls) {
		allUrls = Arrays.asList(urls);
		availableUrls = Collections.synchronizedSet(new HashSet<String>());
	}
	
	public void start() {
		Thread monitorThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				while (true) {
					try {
						Date date = new Date();
						System.out.println(String.format("Performing URL refresh at %s.", dateFormat.format(date)));
						refresh();
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		monitorThread.start();
	}
	
	public String getAvailableUrl() {
		int count = availableUrls.size();
		if (count > 0) {
			List<String> urls = new ArrayList<>(availableUrls);
			return urls.get((int)Math.floor(Math.random() * count));
		}
		return null;
	}
	
	private void refresh() {
		List<Thread> threadPool = new ArrayList<>();
		for (final String url : allUrls) {
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
						connection.setRequestMethod("GET");
						int code = connection.getResponseCode();
						updateUrl(url, code == HttpURLConnection.HTTP_OK);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
			threadPool.add(t);
		}
		for (Thread t : threadPool) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateUrl(String url, Boolean state) {
		if (state) {
			availableUrls.add(url);
		} else {
			availableUrls.remove(url);
		}
	}
}
