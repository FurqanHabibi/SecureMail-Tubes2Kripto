import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.mail.Folder;
import javax.mail.Message;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import emailprocessing.EmailDownloader;


public class SecureMail {

	private JFrame frame;
	private JPanel toolbar;
	private JSplitPane splitPaneFoldersEmails;
	private JSplitPane splitPaneEmailsContent;
	private JScrollPane scrollPaneEmails;
	private JPanel panelContent;
	private JScrollPane scrollPaneContent;
	private JTextArea textAreaContent;
	private JPanel panelContentActions;
	private JScrollPane scrollPaneFolders;
	private JButton btnNewMessage;
	private JButton btnRefresh;
	private JButton btnChangeAccount;
	private JButton btnGenerateKeys;
	private JButton btnDecrypt;
	private JButton btnVerifySignature;
	
	private JList<String> listFolders;
	private JList<String> listEmails;
	private DefaultListModel<String> listModelFolders;
	private DefaultListModel<String> listModelEmails;
	
	private String imapProt = "imaps";
	private String imapHost = "imap.gmail.com";
	private String smtpProt = "smtps";
	private String smtpHost = "smtp.gmail.com";
	private String username;
	private String password;
	
	private EmailDownloader emailDownloader;
	private Folder currentFolder = null;
	private Message[] currentMessages = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SecureMail window = new SecureMail();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SecureMail() {
		initialize();
		changeAccount();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		frame = new JFrame("SecureMail");
		frame.setBounds(0, 0, 1000, 650);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setVisible(true);
		
		toolbar = new JPanel();
		frame.getContentPane().add(toolbar, BorderLayout.NORTH);
		toolbar.setPreferredSize(new Dimension(10, 45));
		
		btnNewMessage = new JButton("New Message");
		
		btnRefresh = new JButton("Refresh");
		
		btnChangeAccount = new JButton("Change Account");
		
		btnGenerateKeys = new JButton("Generate Keys");
		GroupLayout gl_toolbar = new GroupLayout(toolbar);
		gl_toolbar.setHorizontalGroup(
			gl_toolbar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_toolbar.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewMessage)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRefresh)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnChangeAccount)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnGenerateKeys)
					.addContainerGap(572, Short.MAX_VALUE))
		);
		gl_toolbar.setVerticalGroup(
			gl_toolbar.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_toolbar.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_toolbar.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewMessage)
						.addComponent(btnRefresh)
						.addComponent(btnChangeAccount)
						.addComponent(btnGenerateKeys))
					.addContainerGap(16, Short.MAX_VALUE))
		);
		toolbar.setLayout(gl_toolbar);
		
		splitPaneFoldersEmails = new JSplitPane();
		frame.getContentPane().add(splitPaneFoldersEmails, BorderLayout.CENTER);
		splitPaneFoldersEmails.setResizeWeight(0.2);
		
		splitPaneEmailsContent = new JSplitPane();
		splitPaneFoldersEmails.setRightComponent(splitPaneEmailsContent);
		splitPaneEmailsContent.setResizeWeight(0.3);
		
		scrollPaneEmails = new JScrollPane();
		splitPaneEmailsContent.setLeftComponent(scrollPaneEmails);
		
		panelContent = new JPanel();
		splitPaneEmailsContent.setRightComponent(panelContent);
		panelContent.setLayout(new BorderLayout(0, 0));
		
		scrollPaneContent = new JScrollPane();
		panelContent.add(scrollPaneContent, BorderLayout.CENTER);
		
		textAreaContent = new JTextArea();
		textAreaContent.setEditable(false);
		scrollPaneContent.setViewportView(textAreaContent);
		
		panelContentActions = new JPanel();
		panelContent.add(panelContentActions, BorderLayout.SOUTH);
		
		btnDecrypt = new JButton("Decrypt");
		panelContentActions.add(btnDecrypt);
		
		btnVerifySignature = new JButton("Verify Signature");
		panelContentActions.add(btnVerifySignature);
		
		scrollPaneFolders = new JScrollPane();
		splitPaneFoldersEmails.setLeftComponent(scrollPaneFolders);
		
		listModelFolders = new DefaultListModel<String>();
		listFolders = new JList<String>(listModelFolders);
		scrollPaneFolders.setViewportView(listFolders);
		
		listModelEmails = new DefaultListModel<String>();
		listEmails = new JList<String>(listModelEmails);
		scrollPaneEmails.setViewportView(listEmails);
	}
	
	private void changeAccount() {
		JFrame frame1 = new JFrame("Sign In");
		frame1.setBounds(100, 100, 300, 200);
		frame1.setLocationRelativeTo(null);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame1.setContentPane(contentPane);
		
		JLabel lblUsername = new JLabel("Username :");
		JTextField textFieldUserName = new JTextField();
		textFieldUserName.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password :");
		JPasswordField textFieldPassword = new JPasswordField();
		textFieldPassword.setColumns(10);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				username = textFieldUserName.getText();
				password = new String(textFieldPassword.getPassword());
				getEmails();
				frame1.dispatchEvent(new WindowEvent(frame1, WindowEvent.WINDOW_CLOSING));
			}
		});
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(29)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblUsername)
						.addComponent(lblPassword))
					.addGap(28)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnOk)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
							.addComponent(textFieldPassword)
							.addComponent(textFieldUserName, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(29, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(34, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(textFieldUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword)
						.addComponent(textFieldPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnOk)
					.addGap(25))
		);
		contentPane.setLayout(gl_contentPane);
		frame1.setVisible(true);
	}

	private void getEmails() {
		//System.out.println(imapProt+ imapHost+ username+ password);
		emailDownloader = new EmailDownloader(imapProt, imapHost, username, password);
		emailDownloader.signIn();
		
		Folder[] folders = emailDownloader.getFolders();
		listModelFolders.clear();
		for (Folder f : folders) {
			listModelFolders.addElement(f.getFullName());
		}
	}
}
