package View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class Login extends JDialog {
	private JTextField txtNick;
	private JTextField txtPass;
	private final JLabel lblNickname;
	private final JLabel lblPassword;
    
	public Login() {
		getContentPane().setLayout(null);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//client userPass(String nick, String pass)
			}
		});
		btnLogin.setBounds(172, 170, 89, 23);
		getContentPane().add(btnLogin);
		
		JButton btnSingIn = new JButton("Sign in");
		btnSingIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnSingIn.setBounds(172, 204, 89, 23);
		getContentPane().add(btnSingIn);
		
		txtNick = new JTextField();
		txtNick.setText("Nick");
		txtNick.setBounds(220, 66, 127, 20);
		getContentPane().add(txtNick);
		txtNick.setColumns(10);
		
		txtPass = new JTextField();
		txtPass.setText("Pass");
		txtPass.setBounds(220, 105, 127, 20);
		getContentPane().add(txtPass);
		txtPass.setColumns(10);
		
		lblNickname = new JLabel("Nickname");
		lblNickname.setBounds(80, 69, 46, 14);
		getContentPane().add(lblNickname);
		
		lblPassword = new JLabel("Password");
		lblPassword.setBounds(80, 108, 46, 14);
		getContentPane().add(lblPassword);
		
	}
}
