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


public class ReceiveMessageFrame extends JFrame {
	private final String BEGIN_SIGNATURE = "#### BEGIN SIGNATURE ####";
	private final String END_SIGNATURE = "#### END SIGNATURE ####";
	private JTextField textFieldFrom;
	private JTextField textFieldCC;
	private JTextField textFieldBCC;
	private JTextField textFieldSubject;
	private JTextArea textAreaContent;
	private JButton btnSend;
	
	// public key for signature verification
	Point publicKey = null;
	
	// currently used ECC params
	BigInteger _p = null;
	BigInteger _a = null;
	BigInteger _b = null;
	BigInteger _xG = null;
	BigInteger _yG = null;
	BigInteger _n = null;
	
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
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EnterKeyDialog dialog = new EnterKeyDialog(new Callback() {
					public void doAction(Object param) {
						String key = (String) param;
						decryptMessageContent(key);
					}
				});
				dialog.setTitle("Enter decryption key...");
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
		
		JButton btnNewButton = new JButton("Verify");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// get public key
				EnterKeyDialog dialog = new EnterKeyDialog(new Callback() {
					public void doAction(Object param) {
						String key = (String) param;
						if(isValidPublicKeyFormat(key)){
							setPublicKey(key);
							confirmSignature();
						} else {
							JOptionPane.showMessageDialog(null, "Format public key tidak sesuai.");
						}
					}
				});
				dialog.setTitle("Enter public key...");
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
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
	
	public boolean isValidPublicKeyFormat(String key){
		// p a b
		// xg yg
		// n
		// pubkey_x pubkey_y
		// total 8 elemen
		String keys[] = key.split("\n|\r\n| ");
		return (keys.length == 8);
	}
	
	public void setPublicKey(String key){
		String keys[] = key.split("\n|\r\n| ");
		
		_p = new BigInteger(keys[0]);
		_a = new BigInteger(keys[1]);
		_b = new BigInteger(keys[2]);
		_xG = new BigInteger(keys[3]);
		_yG = new BigInteger(keys[4]);
		_n = new BigInteger(keys[5]);
		
		publicKey = new Point(new BigInteger(keys[6]), new BigInteger(keys[7]));
	}
	
	public void confirmSignature(){
		BigInteger r = publicKey.x;
		BigInteger s = publicKey.y;
		
		String content = textAreaContent.getText();
		String realContent = content.split("\n" + BEGIN_SIGNATURE)[0];
		
		System.out.println("Content: " + "\n\"" + realContent + "\"");
		
		String[] lineContents = content.split("\n|\r\n");
		for(int i=0;i<lineContents.length;i++){
			if(lineContents[i].equals(BEGIN_SIGNATURE)){
				r = new BigInteger(lineContents[i+1], 16);
				s = new BigInteger(lineContents[i+2], 16);
			}
		}
		
		ECDSA ecdsa = new ECDSA();
		if(r != null && s != null){
			boolean valid = ecdsa.verifySignature(publicKey, realContent,  _a, _b, _p, new Point(_xG,_yG), _n , r, s);
			if(valid){
				JOptionPane.showMessageDialog(null, "Signature is valid.");
			} else {
				JOptionPane.showMessageDialog(null, "Signature is invalid!");
			}
		} else {
			JOptionPane.showMessageDialog(null, "No signature is found.");
		}
	}
	
	public void decryptMessageContent(String key){
		BlockCipher cipher = new BlockCipher();
		cipher.setIsEncryption(false);
		cipher.setInputWithByteString(textAreaContent.getText());
		cipher.setKey(key);
		cipher.CBC();
		
		textAreaContent.setText(cipher.getOutput());
	}
}
