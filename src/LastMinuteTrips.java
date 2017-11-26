

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LastMinuteTrips
 */
public class LastMinuteTrips extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HealthMonitor healthMonitor;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LastMinuteTrips() {
		super();
		healthMonitor = new HealthMonitor(new String[] {
				"http://147.32.233.18:8888/MI-MDW-LastMinute1/list",
				"http://147.32.233.18:8888/MI-MDW-LastMinute2/list",
				"http://147.32.233.18:8888/MI-MDW-LastMinute3/list"
		});
		healthMonitor.start();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// url
		String url = null;
		do {
			url = healthMonitor.getAvailableUrl();
		} while (url == null);
		System.out.println("Connecting to " + url);
		HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
		// HTTP method
		connection.setRequestMethod("GET");
		// copy headers
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			connection.setRequestProperty(header, request.getHeader(header));
		}
		// copy body
		BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));
		String inputLine;
		ServletOutputStream sout = response.getOutputStream();
		while ((inputLine = inputStream.readLine()) != null) {
			sout.write(inputLine.getBytes());
		}
		// close
		inputStream.close();
		sout.flush();
	}
}
