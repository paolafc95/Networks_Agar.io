package View;

//import java.awt.BorderLayout;
//import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
//import javax.swing.JPanel;
//import javax.swing.border.EmptyBorder;


import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;



import javax.swing.JLabel;
//import javax.swing.JOptionPane;

public class VentanaLoginP extends JDialog {
	
	private JTextField txtNick;
	private JTextField txtPass;
	private final JLabel lblNickname;
	private final JLabel lblPassword;
	
	private VentanaCrearCuenta ventanaCrearCuenta;
    private Client client;
	public VentanaLoginP(Client client) {
		this.client=client;
		setBounds(50, 50, 450, 300);
		
		getContentPane().setLayout(null);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				verificarUsuario();
			}
		});
		btnLogin.setBounds(172, 170, 89, 23);
		getContentPane().add(btnLogin);
		
		JButton btnSingIn = new JButton("Sign in");
		btnSingIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Debe ir a la ventana crear cuenta'
				ventanaCrearCuenta = new VentanaCrearCuenta(client);
				ventanaCrearCuenta.setVisible(true);
			}
		});
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {  
            @Override
            public void windowClosing(WindowEvent e) {  
                System.exit(0);  
            }  
        });
		btnSingIn.setBounds(172, 204, 89, 23);
		getContentPane().add(btnSingIn);
		
		txtNick = new JTextField();
		txtNick.setText("");
		txtNick.setBounds(220, 66, 127, 20);
		getContentPane().add(txtNick);
		txtNick.setColumns(10);
		
		txtPass = new JTextField();
		txtPass.setText("");
		txtPass.setBounds(220, 105, 127, 20);
		getContentPane().add(txtPass);
		txtPass.setColumns(10);
		
		lblNickname = new JLabel("Email");
		lblNickname.setBounds(80, 69, 100, 14);
		getContentPane().add(lblNickname);
		
		lblPassword = new JLabel("Password");
		lblPassword.setBounds(80, 108, 100, 14);
		getContentPane().add(lblPassword);
		
	}
	public void verificarUsuario() {
		Session verificarUsuPass = new Session();
		verificarUsuPass.setPass(txtPass.getText());
		verificarUsuPass.setEmail(txtNick.getText());
		client.userPass(verificarUsuPass);
	}
}

