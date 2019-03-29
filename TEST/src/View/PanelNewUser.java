package View;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelNewUser extends JPanel {

	private Client client;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	/**
	 * Create the panel.
	 */
	public PanelNewUser(Client clie) {
		setLayout(null);
		client=clie;
		textField = new JTextField();
		textField.setText("");
		textField.setBounds(249, 62, 86, 20);
		add(textField);
		textField.setColumns(10);
		
		JLabel lblUsuario = new JLabel("Usuario");
		lblUsuario.setBounds(129, 65, 46, 14);
		add(lblUsuario);
		
		textField_1 = new JTextField();
		textField_1.setBounds(249, 108, 86, 20);
		add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(249, 157, 86, 20);
		add(textField_2);
		textField_2.setColumns(10);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.userPass(textField.getText(), textField_1.getText(),"create");
			}
		});
		btnNewButton.setBounds(183, 223, 89, 23);
		add(btnNewButton);

		
	}

}
