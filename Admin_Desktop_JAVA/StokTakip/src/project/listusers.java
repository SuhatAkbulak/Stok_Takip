package project;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.JTable;

public class listusers {

	JFrame frame;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					listusers window = new listusers();
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
	public listusers() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 473, 535);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(27).addContainerGap(431, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addGap(37).addContainerGap(460, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
		try {
			URL url = new URL("https://proje.hegel.io/api/v1/list_yonetici");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/json");
			http.setRequestProperty("Accept", "application/json");
			//http.setRequestProperty("Authorization", "Basic "+encoded);
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
					    String column[]={"ID","KULLANICI ADI","ÞÝFRE"};         
					    
					    System.out.println(response.toString());
						/*
						 * JSONObject jo= new JSONObject(response.toString()); String
						 * message="",durum=""; String [][]veriler=new String[jo.length()][3]; for(int
						 * i=0;i<3;i++) { for(int j=0;j<jo.length();j++) { if(i==0) veriler[i][j]=
						 * jo.get("db_id").toString(); if(i==1) veriler[i][j]=
						 * jo.get("username").toString(); if(i==2)
						 * veriler[i][j]=jo.get("password").toString(); } }
						 */
						JSONArray jsonArr = new JSONArray(response.toString());
					    Vector<Vector<String>> dataList = new Vector<>();
					    for (int i = 0; i < jsonArr.length(); i++) {

					        JSONObject jsonObj = jsonArr.getJSONObject(i);
					        Vector<String> veiler = new Vector<>();

					        veiler.add(jsonObj.get("db_id").toString());
					        veiler.add(jsonObj.getString("username"));
					        veiler.add(jsonObj.getString("password"));

					        dataList.add(veiler);
					    }

					    Vector<String> columnNames = new Vector<>();
					    columnNames.add("ID");
					    columnNames.add("KULLANICI ADI");
					    columnNames.add("PASSWORD");

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
