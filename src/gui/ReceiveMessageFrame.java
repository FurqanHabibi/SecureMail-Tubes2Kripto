package gui;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import models.MessageModel;
import emailprocessing.EmailSender;

import javax.swing.JCheckBox;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import algorithm.blockcipher.BlockCipher;
import algorithm.dsa.ECDSA;
import algorithm.dsa.Point;


public class ReceiveMessageFrame extends JFrame {
	private final String BEGIN_SIGNATURE = "#### BEGIN SIGNATURE ####";
	private final String END_SIGNATURE = "#### END SIGNATURE ####";
	private JTextField textFieldFrom;
	private JTextField textFieldCC;
	private JTextField textFieldBCC;
	private JTextField textFieldSubject;
	private JTextArea textAreaContent;
	private JButton btnSend;
	
	/**
	 * Launch the application.
	 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReceiveMessageFrame frame = new ReceiveMessageFrame("tubes.kripto.a", "tubeskript0");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	*/
	
	public void setField(String _textFieldFrom, String _textFieldCC, String _textFieldBCC, String _textFieldSubject, String _textAreaContent){
		textFieldFrom.setText(_textFieldFrom);
		textFieldCC.setText(_textFieldCC);
		textFieldBCC.setText(_textFieldBCC);
		textFieldSubject.setText(_textFieldSubject);
		textAreaContent.setText(_textAreaContent);
	}

	/**
	 * Create the frame.
	 */
	public ReceiveMessageFrame() {
		initialize();
	}
	
	private void initialize() {
		setTitle("Decrypt & Verify Message");
		setBounds(100, 100, 600, 505);
		setLocationRelativeTo(null);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblTo = new JLabel("FROM :");
		
		JLabel lblCc = new JLabel("CC :");
		
		JLabel lblBcc = new JLabel("BCC :");
		
		JLabel lblSubject = new JLabel("Subject:");
		
		JLabel lblContent = new JLabel("Content:");
		
		textFieldFrom = new JTextField();
		textFieldFrom.setColumns(10);
		
		textFieldCC = new JTextField();
		textFieldCC.setColumns(10);
		
		textFieldBCC = new JTextField();
		textFieldBCC.setColumns(10);
		
		textFieldSubject = new JTextField();
		textFieldSubject.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		
		btnSend = new JButton("Decrypt");
		
		JButton btnNewButton = new JButton("Verify");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSubject)
								.addComponent(lblBcc)
								.addComponent(lblCc)
								.addComponent(lblTo))
							.addGap(23)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(textFieldFrom, GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
								.addComponent(textFieldCC, GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
								.addComponent(textFieldBCC, GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
								.addComponent(textFieldSubject, GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)))
						.addComponent(lblContent)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTo)
						.addComponent(textFieldFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCc)
						.addComponent(textFieldCC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBcc)
						.addComponent(textFieldBCC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSubject)
						.addComponent(textFieldSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(11)
					.addComponent(lblContent)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
					.addGap(7))
		);
		
		textAreaContent = new JTextArea();
		scrollPane.setViewportView(textAreaContent);
		contentPane.setLayout(gl_contentPane);
		
		textFieldFrom.setEditable(false);
		textFieldCC.setEditable(false);
		textFieldBCC.setEditable(false);
		textFieldSubject.setEditable(false);
		textAreaContent.setEditable(false);
	}
}
