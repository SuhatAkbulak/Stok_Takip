package project;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.json.JSONArray;
import org.json.JSONObject;

public class loglist {

	JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					loglist window = new loglist();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public loglist() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			URL url = new URL("https://proje.hegel.io/api/v1/log_list");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/json");
			http.setRequestProperty("Accept", "application/json");
			//http.setRequestProperty("Authorization", "Basic "+encoded);
			http.setDoOutput(true);

			String data = "{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0aW1lIjoxNjQwNTExOTcxLjYxMTcwNSwiZXhwIjoxNjQwNTk4MzcxLjYxMTcwNSwiaWQiOjIsInlldGtpIjoxfQ.6y15EiyKIewpGs8cPvv0dYBkBbNBMY9gRzh6SGWdZFY\"}";

			try(OutputStream os = http.getOutputStream()) {
			    byte[] input = data.getBytes(StandardCharsets.UTF_8);
			    os.write(input, 0, input.length);			
			}
			try(BufferedReader br = new BufferedReader(
					  new InputStreamReader(http.getInputStream()))) {
					    StringBuilder response = new StringBuilder();
					    String responseLine = null;
					    while ((responseLine = br.readLine()) != null) {
					        response.append(responseLine.trim());
					    }        
					    
					    System.out.println(response.toString());

						JSONArray jsonArr = new JSONArray(response.toString());
					    Vector<Vector<String>> dataList = new Vector<>();
					    for (int i = 0; i < jsonArr.length(); i++) {

					        JSONObject jsonObj = jsonArr.getJSONObject(i);
					        Vector<String> veiler = new Vector<>();

					        veiler.add(jsonObj.get("tarih").toString());
					        veiler.add(jsonObj.getString("islem_yapilan_urun"));
					        veiler.add(jsonObj.getString("events"));

					        dataList.add(veiler);
					    }

					    Vector<String> columnNames = new Vector<>();
					    columnNames.add("TARÝH");
					    columnNames.add("ÜRÜN");
					    columnNames.add("AÇIKLAMA");

					    JTable table = new JTable(dataList, columnNames);
						JScrollPane scrollPane = new JScrollPane(table);
						scrollPane.setVisible(true);
						scrollPane.setBounds(30,50, 300, 300);
						
						
						frame.add(scrollPane);
						
						
						frame.setSize(400, 500);
						frame.setLayout(null);
						
						frame.setVisible(true);
						
					}
			  http.disconnect();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
