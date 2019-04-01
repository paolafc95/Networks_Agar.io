package View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class VentanaCrearCuenta extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtNickname;
	private JTextField txtPassword;
	private JTextField txtEmail;
	private Client client;
	/**
	 * Create the dialog.
	 */
	public VentanaCrearCuenta(Client client) {
		this.client=client;
		setBounds(100, 100, 407, 305);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Nickname");
			lblNewLabel.setBounds(48, 30, 46, 14);
			contentPanel.add(lblNewLabel);
		}
		
		txtNickname = new JTextField();
		txtNickname.setBounds(142, 27, 197, 20);
		contentPanel.add(txtNickname);
		txtNickname.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Contrase\u00F1a");
		lblNewLabel_1.setBounds(48, 72, 75, 14);
		contentPanel.add(lblNewLabel_1);
		
		txtPassword = new JTextField();
		txtPassword.setColumns(10);
		txtPassword.setBounds(142, 69, 197, 20);
		contentPanel.add(txtPassword);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(48, 116, 75, 14);
		contentPanel.add(lblEmail);
		
		txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(142, 113, 197, 20);
		contentPanel.add(txtEmail);
		{
			JButton btnAceptar = new JButton("ACEPTAR");
			btnAceptar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					//Lo que debe hacer cuando desea logearse
					verificarRegistro();
					
				}
			});
			btnAceptar.setBounds(114, 218, 87, 23);
			contentPanel.add(btnAceptar);
			btnAceptar.setActionCommand("OK");
			getRootPane().setDefaultButton(btnAceptar);
		}
		{
			JButton btnCancelar = new JButton("CANCELAR");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
					
				}
			});
			btnCancelar.setBounds(211, 218, 87, 23);
			contentPanel.add(btnCancelar);
			btnCancelar.setActionCommand("Cancel");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
		}
		
		
		
	}
	
	public void verificarRegistro() {

		Session verificarRegistro = new Session(txtNickname.getText(), 0, 0);
		verificarRegistro.setPass(txtPassword.getText());
		verificarRegistro.setEmail(txtEmail.getText());
		verificarRegistro.setEstado(Session.VERIFICAR_REGISTRO);
		client.userPass(verificarRegistro);
		
	}
}
