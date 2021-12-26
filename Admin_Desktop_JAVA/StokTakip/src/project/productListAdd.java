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

public class productListAdd {

	JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					productListAdd window = new productListAdd();
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
	public productListAdd() {
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
			URL url = new URL("https://proje.hegel.io/api/v1/list_stock");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			//http.setRequestProperty("Content-Type", "application/json");
			//http.setRequestProperty("Accept", "application/json");
			int responseCode = http.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						http.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				System.out.println(response.toString());
				JSONArray jsonArr = new JSONArray(response.toString());
			    Vector<Vector<String>> dataList = new Vector<>();
			    for (int i = 0; i < jsonArr.length(); i++) {

			        JSONObject jsonObj = jsonArr.getJSONObject(i);
			        Vector<String> veiler = new Vector<>();

			        veiler.add(jsonObj.get("urun_id").toString());
			        veiler.add(jsonObj.getString("urun_barkod"));
			        veiler.add(jsonObj.getString("urun_isim"));
			        veiler.add(jsonObj.getString("stok_sayi"));

			        dataList.add(veiler);
			    }

			    Vector<String> columnNames = new Vector<>();
			    columnNames.add("ID");
			    columnNames.add("BARKOD");
			    columnNames.add("ÜRÜN");
			    columnNames.add("ADET");

			    JTable table = new JTable(dataList, columnNames);
				JScrollPane scrollPane = new JScrollPane(table);
				scrollPane.setVisible(true);
				scrollPane.setBounds(30,50, 600, 300);
				
				
				frame.add(scrollPane);
				
				
				frame.setSize(700, 400);
				frame.setLayout(null);
				
				frame.setVisible(true);
			} else {
				System.out.println("GET request not worked");
			}
			  http.disconnect();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
