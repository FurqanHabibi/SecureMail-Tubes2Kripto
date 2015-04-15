import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import models.EmailModel;
import models.FolderModel;
import models.MessageModel;
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
	private JList<String> listMessages;
	private DefaultListModel<String> listModelFolders;
	private DefaultListModel<String> listModelMessages;
	
	private JFrame frame1;
	private JButton btnOk;
	private JTextField textFieldUserName;
	private JPasswordField textFieldPassword;
	
	private String imapProt = "imaps";
	private String imapHost = "imap.gmail.com";
	private String smtpProt = "smtps";
	private String smtpHost = "smtp.gmail.com";
	private String username;
	private String password;
	
	private EmailDownloader emailDownloader = null;
//	private Folder currentFolder = null;
//	private Message[] currentMessages = null;
	private EmailModel emailModel;
//	private int currentFolder;
//	private int currentMessage;

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
		
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				username = textFieldUserName.getText();
				password = new String(textFieldPassword.getPassword());
				emailModel = getEmails();
				listModelFolders.clear();
				for (FolderModel fm : emailModel.getFolders()) {
					listModelFolders.addElement(fm.getName());
				}
				listModelMessages.clear();
	        	textAreaContent.setText("");
				frame1.dispatchEvent(new WindowEvent(frame1, WindowEvent.WINDOW_CLOSING));
			}
		});
		
		listFolders.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
			        if (listFolders.getSelectedIndex() != -1) {
			        	listModelMessages.clear();
			        	for (MessageModel mm : emailModel.getFolders().get(listFolders.getSelectedIndex()).getMessages()) {
			        		listModelMessages.add(0, mm.getSubject());
			        	}
			        	textAreaContent.setText("");
			        }
			    }
			}
		});
		
		listMessages.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
			        if (listMessages.getSelectedIndex() != -1) {
			        	MessageModel mm = emailModel.getFolders().get(listFolders.getSelectedIndex()).getMessages().get(listModelMessages.getSize()-listMessages.getSelectedIndex()-1);
			        	textAreaContent.setText(mm.toString());
			        }
			    }
			}
		});

		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				emailModel = getEmails();
				listModelFolders.clear();
				for (FolderModel fm : emailModel.getFolders()) {
					listModelFolders.addElement(fm.getName());
				}
				listModelMessages.clear();
	        	textAreaContent.setText("");
			}
		});
		
		btnChangeAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame1.setVisible(true);
			}
		});
	}
	
	private EmailModel getEmails() {
		emailDownloader = new EmailDownloader();
		emailDownloader.setParams(imapProt, imapHost, username, password);
		emailDownloader.signIn();
		
		EmailModel emailModel = new EmailModel();
		Folder[] folders = emailDownloader.getFolders();
		for (Folder f : folders) {
			FolderModel fm = new FolderModel(f.getName());
			emailModel.addFolderModel(fm);
			emailDownloader.openFolder(f);
			Message[] messages = emailDownloader.getMessagesInFolder(f);
			for (Message m : messages) {
				MessageModel mm = new MessageModel();
				fm.addMessageModel(mm);
				try {
					
					Address[] adressFrom = m.getFrom();
					if (adressFrom!=null) {
						String[] from = new String[adressFrom.length];
						for (int i=0; i<adressFrom.length; i++) {
							from[i] = adressFrom[i].toString();
						}
						mm.setFrom(from);
					}
					
					Address[] adressTOs = m.getRecipients(Message.RecipientType.TO);
					if (adressTOs!=null) {
						String[] TOs = new String[adressTOs.length];
						for (int i=0; i<adressTOs.length; i++) {
							TOs[i] = adressTOs[i].toString();
						}
						mm.setTOs(TOs);
					}
					
					Address[] adressCCs = m.getRecipients(Message.RecipientType.CC);
					if (adressCCs!=null) {
						String[] CCs = new String[adressCCs.length];
						for (int i=0; i<adressCCs.length; i++) {
							CCs[i] = adressCCs[i].toString();
						}
						mm.setCCs(CCs);
					}
					
					Address[] adressBCCs = m.getRecipients(Message.RecipientType.BCC);
					if (adressBCCs!=null) {
						String[] BCCs = new String[adressBCCs.length];
						for (int i=0; i<adressBCCs.length; i++) {
							BCCs[i] = adressBCCs[i].toString();
						}
						mm.setBCCs(BCCs);
					}
					
					mm.setSentDate(m.getSentDate());
					
					mm.setSubject(m.getSubject());
					
					mm.setContent(emailDownloader.getMessageContent(m));
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			emailDownloader.closeFolder(f);
		}
		
		emailDownloader.signOut();
		return emailModel;
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
		
		listModelMessages = new DefaultListModel<String>();
		listMessages = new JList<String>(listModelMessages);
		scrollPaneEmails.setViewportView(listMessages);
	}
	
	private void changeAccount() {
		frame1 = new JFrame("Sign In");
		frame1.setBounds(100, 100, 300, 200);
		frame1.setLocationRelativeTo(null);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame1.setContentPane(contentPane);
		
		JLabel lblUsername = new JLabel("Username :");
		textFieldUserName = new JTextField();
		textFieldUserName.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password :");
		textFieldPassword = new JPasswordField();
		textFieldPassword.setColumns(10);
		
		btnOk = new JButton("OK");
		
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
}
