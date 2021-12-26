package project;

import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.awt.event.ActionEvent;

public class login {

	private JFrame frame;
	private JTextField userName;
	private JTextField password;
	private JLabel durum;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login window = new login();
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
	public login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 471, 520);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		userName = new JTextField();
		userName.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Username");
		
		password = new JTextField();
		password.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		
		JButton login = new JButton("LOGIN");
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String encoded = Base64.getEncoder().encodeToString((userName.getText() + ":" + password.getText()).getBytes(StandardCharsets.UTF_8));
				
					URL url = new URL("https://proje.hegel.io/api/v1/login");
					HttpURLConnection http = (HttpURLConnection)url.openConnection();
					http.setRequestMethod("POST");
					http.setRequestProperty("Content-Type", "application/json");
					http.setRequestProperty("Accept", "application/json");
					//http.setRequestProperty("Authorization", "Basic "+encoded);
					http.setDoOutput(true);

					String data = "{\"username\":\""+userName.getText()+"\",\"password\":\""+password.getText()+"\"}";

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
									login.setVisible(false); 
									mainpage window1 = new mainpage(); 
									window1.frame.setVisible(true);
								}
								else
									JOptionPane.showMessageDialog(login, message);
							}
					  http.disconnect();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		durum = new JLabel("");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(90)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(durum, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
							.addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
							.addComponent(password, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
							.addComponent(userName, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
							.addComponent(login, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addGap(100))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(186)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(userName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPassword)
					.addGap(6)
					.addComponent(password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(login)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(durum)
					.addContainerGap(161, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
	 public static String slurp(InputStream is){
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();
		    String line;
		    try {
				while ((line = br.readLine()) != null) {
				    sb.append(line+"\n");
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    return sb.toString();
		 }
}
