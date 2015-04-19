package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class GenerateKeysFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GenerateKeysFrame frame = new GenerateKeysFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GenerateKeysFrame() {
		setTitle("Generate Keys");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 428, 188);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(103, 11, 299, 20);
		contentPane.add(comboBox);
		
		JLabel lblEccCurve = new JLabel("ECC Curve:");
		lblEccCurve.setBounds(10, 14, 83, 14);
		contentPane.add(lblEccCurve);
		
		JLabel lblNewLabel = new JLabel("EC Private Key:");
		lblNewLabel.setBounds(10, 39, 96, 14);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(103, 36, 299, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblEcPublicKey = new JLabel("EC Public Key:");
		lblEcPublicKey.setBounds(10, 64, 96, 14);
		contentPane.add(lblEcPublicKey);
		
		textField_1 = new JTextField();
		textField_1.setBounds(103, 61, 299, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnGenerateEcKey = new JButton("Generate EC Key Pair");
		btnGenerateEcKey.setBounds(232, 92, 170, 49);
		contentPane.add(btnGenerateEcKey);
	}
}
