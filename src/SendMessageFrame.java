import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
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


public class SendMessageFrame extends JFrame {
	private JTextField textFieldTo;
	private JTextField textFieldCC;
	private JTextField textFieldBCC;
	private JTextField textFieldSubject;
	private JTextArea textAreaContent;
	private JToggleButton tglbtnEncrypt;
	private JToggleButton tglBtnDigitalSignature;
	private JButton btnSend;

	private String smtpProt = "smtps";
	private String smtpHost = "smtp.gmail.com";
	private String username;
	private String password;
	private EmailSender emailSender;

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
	public SendMessageFrame(String username, String password) {
		initialize();
		this.username = username;
		this.password = password;
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
				
				mm.setContent(textAreaContent.getText());
				
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
		tglbtnEncrypt.setSelected(false);
		tglBtnDigitalSignature.setSelected(false);
		setVisible(true);
	}
	
	private void initialize() {
		setTitle("Send Message");
		setBounds(100, 100, 600, 500);
		setLocationRelativeTo(null);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblTo = new JLabel("TO :");
		
		JLabel lblCc = new JLabel("CC :");
		
		JLabel lblBcc = new JLabel("BCC :");
		
		JLabel lblSubject = new JLabel("SUBJECT :");
		
		JLabel lblContent = new JLabel("CONTENT :");
		
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
		
		tglbtnEncrypt = new JToggleButton("Encrypt");
		
		tglBtnDigitalSignature = new JToggleButton("Digital Signature");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(lblTo)
							.addGap(52)
							.addComponent(textFieldTo, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(lblCc)
							.addGap(52)
							.addComponent(textFieldCC, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(lblBcc)
							.addGap(46)
							.addComponent(textFieldBCC, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(tglbtnEncrypt, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(tglBtnDigitalSignature)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblSubject)
							.addGap(23)
							.addComponent(textFieldSubject, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
						.addComponent(lblContent))
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
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblContent)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
						.addComponent(tglBtnDigitalSignature, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
						.addComponent(tglbtnEncrypt, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		textAreaContent = new JTextArea();
		scrollPane.setViewportView(textAreaContent);
		contentPane.setLayout(gl_contentPane);
	}
}
