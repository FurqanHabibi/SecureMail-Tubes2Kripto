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
import javax.swing.JOptionPane;
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


public class SendMessageFrame extends JFrame {
	private final String BEGIN_SIGNATURE = "#### BEGIN SIGNATURE ####";
	private final String END_SIGNATURE = "#### END SIGNATURE ####";
	private JTextField textFieldTo;
	private JTextField textFieldCC;
	private JTextField textFieldBCC;
	private JTextField textFieldSubject;
	private JTextArea textAreaContent;
	private JButton btnSend;
	
	private String smtpProt = "smtps";
	private String smtpHost = "smtp.gmail.com";
	private String username;
	private String password;
	private EmailSender emailSender;
	
	
	private boolean useEncryption = false;
	private boolean useSignature = false;
	
	// key used to encrypt email content
	private String messageKey = null;
	
	// private key used to sign message
	private BigInteger privateKey = null;
	private JCheckBox useEncryptionCheckBox;
	private JCheckBox useSignatureCheckBox;
	
	// currently used ECC params
	BigInteger _p = null;
	BigInteger _a = null;
	BigInteger _b = null;
	BigInteger _xG = null;
	BigInteger _yG = null;
	BigInteger _n = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SendMessageFrame frame = new SendMessageFrame("tubes.kripto.a", "tubeskript0");
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
	public SendMessageFrame(String _username, String _password) {
		initialize();
		this.username = _username;
		this.password = _password;
		emailSender = new EmailSender();

		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MessageModel mm = new MessageModel();
				
				try {
					mm.setFrom(InternetAddress.parse(username+"@gmail.com", false));
					
					if (!textFieldTo.getText().equals("")) {
						mm.setTOs(InternetAddress.parse(textFieldTo.getText(), false));
					}
					
					if (!textFieldCC.getText().equals("")) {
						mm.setCCs(InternetAddress.parse(textFieldCC.getText(), false));
					}
					
					if (!textFieldBCC.getText().equals("")) {
						mm.setBCCs(InternetAddress.parse(textFieldBCC.getText(), false));
					}
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mm.setSentDate(new Date());
				
				mm.setSubject(textFieldSubject.getText());
				
				String finalContent = textAreaContent.getText();
				
				if(useSignature){
					ECDSA ecdsa = new ECDSA();
					ecdsa.signatureGeneration(privateKey, finalContent, _a, _b, _p, new Point(_xG,_yG), _n);
					
					finalContent += "\n" + BEGIN_SIGNATURE + "\n" + ecdsa.getR().toString(16) + "\n" + ecdsa.getS().toString(16) + "\n" + END_SIGNATURE + "\n";
				}
				
				if(useEncryption){
					BlockCipher cipher = new BlockCipher();
					cipher.setIsEncryption(true);
					cipher.setInput(finalContent);
					cipher.setKey(messageKey);
					cipher.CBC();
					
					finalContent = cipher.getByteString();
				} else {
					finalContent = textAreaContent.getText();
				} 
				
				System.out.println(finalContent);
				
				mm.setContent(finalContent);
				
				emailSender.sendEmail(smtpProt, smtpHost, username, password, mm);
			}
		});
	}

	public void show(String username, String password) {
		showSendMessage();
		this.username = username;
		this.password = password;
		
	}
	
	private void showSendMessage() {
		textFieldTo.setText("");
		textFieldCC.setText("");
		textFieldBCC.setText("");
		textFieldSubject.setText("");
		textAreaContent.setText("");
		setVisible(true);
	}
	
	private void initialize() {
		setTitle("Send Message");
		setBounds(100, 100, 600, 565);
		setLocationRelativeTo(null);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblTo = new JLabel("TO :");
		
		JLabel lblCc = new JLabel("CC :");
		
		JLabel lblBcc = new JLabel("BCC :");
		
		JLabel lblSubject = new JLabel("Subject:");
		
		JLabel lblContent = new JLabel("Content:");
		
		textFieldTo = new JTextField();
		textFieldTo.setColumns(10);
		
		textFieldCC = new JTextField();
		textFieldCC.setColumns(10);
		
		textFieldBCC = new JTextField();
		textFieldBCC.setColumns(10);
		
		textFieldSubject = new JTextField();
		textFieldSubject.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		
		btnSend = new JButton("Send");
		
		useEncryptionCheckBox = new JCheckBox("Use Encryption");
		useEncryptionCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox encryptCheck = (JCheckBox) e.getSource();
				if(encryptCheck.isSelected()){
					enableEncryption();
				} else {
					disableEncryption();
				}
				
				// biar enableEncryption/disableEncryption yang menentukan state checked/tidak
				encryptCheck.setSelected(false);
			}
		});
		
		useSignatureCheckBox = new JCheckBox("Use Signature");
		useSignatureCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox signatureCheck = (JCheckBox) e.getSource();
				if(signatureCheck.isSelected()){
					enableSignature();
				} else {
					disableSignature();
				}
				
				signatureCheck.setSelected(false);
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(useSignatureCheckBox)
								.addComponent(useEncryptionCheckBox))
							.addGap(18)
							.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblContent)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSubject)
								.addComponent(lblBcc)
								.addComponent(lblCc)
								.addComponent(lblTo))
							.addGap(23)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(textFieldTo, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(textFieldCC, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(textFieldBCC, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(textFieldSubject, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTo)
						.addComponent(textFieldTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(useEncryptionCheckBox)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(useSignatureCheckBox))
						.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		textAreaContent = new JTextArea();
		scrollPane.setViewportView(textAreaContent);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void disableSignature() {
		useSignature = false;
		privateKey = null;
	}

	public void enableSignature() {
		EnterKeyDialog dialog = new EnterKeyDialog(new Callback() {
			public void doAction(Object param) {
				String key = (String) param;
				if(isValidPrivateKeyFormat(key)){
					setPrivateKeyValue(key);
					useSignature = true;
					useSignatureCheckBox.setSelected(true);
				} else {
					JOptionPane.showMessageDialog(null, "Format private key tidak sesuai.");
				}
			}
		});
		dialog.setTitle("Enter private key...");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		
	}

	public void disableEncryption(){
		useEncryption = false;
		messageKey = null;
	}
	
	// pop dialog box for key, set encryption key
	public void enableEncryption(){
		EnterKeyDialog dialog = new EnterKeyDialog(new Callback() {
			public void doAction(Object param) {
				String key = (String) param;
				setMessageKeyValue(key);
				useEncryption = true;
				useEncryptionCheckBox.setSelected(true);
			}
		});
		dialog.setTitle("Enter encryption key...");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		
	}
	
	public void setMessageKeyValue(String key){
		this.messageKey = key;
	}
	
	public boolean isValidPrivateKeyFormat(String key){
		// p a b
		// xg yg
		// n
		// private_key
		// total 7 elemen
		String keys[] = key.split("\n|\r\n| ");
		return (keys.length == 7);
	}
	
	public void setPrivateKeyValue(String key){
		String keys[] = key.split("\n|\r\n| ");
		
		_p = new BigInteger(keys[0]);
		_a = new BigInteger(keys[1]);
		_b = new BigInteger(keys[2]);
		_xG = new BigInteger(keys[3]);
		_yG = new BigInteger(keys[4]);
		_n = new BigInteger(keys[5]);
		privateKey = new BigInteger(keys[6]);
		
		System.out.println("_p: " + _p);
		System.out.println("_a: " + _a);
		System.out.println("_b: " + _b);
		System.out.println("_xG: " + _xG);
		System.out.println("_yG: " + _yG);
		System.out.println("_n: " + _n);
		System.out.println("privateKey: " + privateKey);
	}
}
