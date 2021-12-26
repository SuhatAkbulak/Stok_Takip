package project;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.json.JSONObject;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.awt.event.ActionEvent;

public class adduser {

	JFrame frame;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					adduser window = new adduser();
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
	public adduser() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 403, 360);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("Username");
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		
		JButton btnSgnUp = new JButton("SIGN UP");
		btnSgnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					URL url = new URL("https://proje.hegel.io/api/v1/admin_creator");
					HttpURLConnection http = (HttpURLConnection)url.openConnection();
					http.setRequestMethod("POST");
					http.setRequestProperty("Content-Type", "application/json");
					http.setRequestProperty("Accept", "application/json");
					//http.setRequestProperty("Authorization", "Basic "+encoded);
					http.setDoOutput(true);

					String data = "{\"username\":\""+textField.getText()+"\",\"password\":\""+textField_1.getText()+"\",\"yetki\":\"1\",\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0aW1lIjoxNjQwNDYwODQyLjA3NjYxNCwiZXhwIjoxNjQwNTQ3MjQyLjA3NjYxNCwiaWQiOjEsInlldGtpIjoxfQ.uoNwSocgltYR-5m6Hj-EZQCzpHjgTKjJo7Jx3b5G60E\"}";

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
								String message="",durum="";
								message=jo.get("message").toString();
								durum=jo.get("status").toString();
								if(durum.equals("ok"))
								{
									JOptionPane.showMessageDialog(btnSgnUp, message);
									frame.setVisible(false);
								}
								else
									JOptionPane.showMessageDialog(btnSgnUp, message);
							}
					  http.disconnect();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(73)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSgnUp, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE))
					.addGap(49))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(62)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(lblPassword)
					.addGap(6)
					.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnSgnUp)
					.addContainerGap(140, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
