package project;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class mainpage {

	JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainpage window = new mainpage();
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
	public mainpage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 400, 402);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton adduser = new JButton("KULLANICI EKLE");
		adduser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adduser window1 = new adduser(); 
				window1.frame.setVisible(true);
			}
		});
		
		JButton listUsers = new JButton("KULLANICILARI L\u0130STELE");
		listUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listusers window1 = new listusers(); 
				window1.frame.setVisible(true);
			}
		});
		
		JButton logList = new JButton("LOG L\u0130STE");
		logList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loglist window1 = new loglist(); 
				window1.frame.setVisible(true);
			}
		});
		
		JButton listProductAdd = new JButton("\u00DCR\u00DCN L\u0130STELE / EKLE");
		listProductAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				productListAdd window1 = new productListAdd(); 
				window1.frame.setVisible(true);
			}
		});
		
		JLabel dashboardinfo = new JLabel("DashBoardInfo");
		
		JLabel doviz = new JLabel("D\u00F6viz");
		
		JLabel dashboardinfo_1 = new JLabel("S\u0130STEMDEK\u0130 KULLANICI SAYISI : null\n DEPODAK\u0130 TOPLAM \u00DCR\u00DCN : null\n TOPLAM \u00DCR\u00DCN : null");
		
		JLabel dashboardinfo_1_1 = new JLabel("S\u0130STEMDEK\u0130 KULLANICI SAYISI : null\n DEPODAK\u0130 TOPLAM \u00DCR\u00DCN : null\n TOPLAM \u00DCR\u00DCN : null");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(adduser, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(listUsers, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(logList, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(listProductAdd, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE))))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(dashboardinfo, GroupLayout.PREFERRED_SIZE, 354, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(dashboardinfo_1, GroupLayout.PREFERRED_SIZE, 354, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(dashboardinfo_1_1, GroupLayout.PREFERRED_SIZE, 354, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(11, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(11, Short.MAX_VALUE)
					.addComponent(doviz, GroupLayout.PREFERRED_SIZE, 365, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(dashboardinfo, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addComponent(dashboardinfo_1, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(dashboardinfo_1_1, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addGap(1)
					.addComponent(doviz, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(listUsers, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addComponent(adduser, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(logList, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addComponent(listProductAdd, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
					.addGap(30))
		);
		frame.getContentPane().setLayout(groupLayout);
		try {
			URL url = new URL("https://proje.hegel.io/api/v1/guncel_dolar_kur");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/json");
			http.setRequestProperty("Accept", "application/json");
			http.setDoOutput(true);

			String data = "{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0aW1lIjoxNjQwNDYwODQyLjA3NjYxNCwiZXhwIjoxNjQwNTQ3MjQyLjA3NjYxNCwiaWQiOjEsInlldGtpIjoxfQ.uoNwSocgltYR-5m6Hj-EZQCzpHjgTKjJo7Jx3b5G60E\"}";

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
					    JSONObject jo= new JSONObject(response.toString());
					    JSONObject dataDoviz=jo.getJSONObject("data");
						JSONObject dolar=dataDoviz.getJSONObject("USDTRY");
						JSONObject euro=dataDoviz.getJSONObject("EURTRY");
						doviz.setText("USD :"+dolar.get("satis").toString()+"  EURO : "+euro.get("satis").toString());
					}
			url = new URL("https://proje.hegel.io/api/v1/dashbord_info");
			http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/json");
			http.setRequestProperty("Accept", "application/json");
			http.setDoOutput(true);

			String data1 = "{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0aW1lIjoxNjQwNDYwODQyLjA3NjYxNCwiZXhwIjoxNjQwNTQ3MjQyLjA3NjYxNCwiaWQiOjEsInlldGtpIjoxfQ.uoNwSocgltYR-5m6Hj-EZQCzpHjgTKjJo7Jx3b5G60E\"}";

			try(OutputStream os = http.getOutputStream()) {
			    byte[] input = data1.getBytes(StandardCharsets.UTF_8);
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
					    JSONObject jo= new JSONObject(response.toString());
						dashboardinfo.setText("SÝSTEMDEKÝ KULLANICI SAYISI : "+jo.get("sistemdeki_toplam_kullanici"));
								dashboardinfo_1.setText(" TOPLAM ÜRÜN : "+jo.get("depodaki_toplam_urun"));
								dashboardinfo_1_1.setText(" ÜRÜN : "+jo.get("toplam_urun"));
					}
			  http.disconnect();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
