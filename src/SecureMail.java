import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

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
import gui.GenerateKeysFrame;
import gui.ReceiveMessageFrame;
import gui.SendMessageFrame;


public class SecureMail {

	private JFrame mainWindow;
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
	
	private JList<String> listFolders;
	private JList<String> listMessages;
	private DefaultListModel<String> listModelFolders;
	private DefaultListModel<String> listModelMessages;
	
	private JFrame signInWindow;
	private JButton btnOk;
	private JTextField textFieldUserName;
	private JPasswordField textFieldPassword;
	
	private SendMessageFrame sendMessageFrame;
	
	private String imapProt = "imaps";
	private String imapHost = "imap.gmail.com";
	private String username;
	private String password;
	
	private EmailDownloader emailDownloader = null;
	private EmailModel emailModel;
	protected ReceiveMessageFrame receiveMessageFrame;

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
		initializeSignIn();
		
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
				signInWindow.dispatchEvent(new WindowEvent(signInWindow, WindowEvent.WINDOW_CLOSING));
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
				emailModel = refresh();
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
				showSignIn();
			}
		});
		
		btnNewMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (sendMessageFrame==null) {
					sendMessageFrame = new SendMessageFrame(username, password);
				} else {
					sendMessageFrame.show(username, password);
				}
				sendMessageFrame.setVisible(true);
			}
		});
	}
	
	private EmailModel getEmails() {
		File file = new File(username + ".ser");
		if(file.exists() && !file.isDirectory()){
			EmailModel emailModel = null;
			try {
				FileInputStream fileIn = new FileInputStream(username + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);
				emailModel = (EmailModel) in.readObject();
				in.close();
				fileIn.close();
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
			
			return emailModel;
		} else {
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
						
						mm.setFrom(m.getFrom());
						
						mm.setTOs(m.getRecipients(Message.RecipientType.TO));
						
						mm.setCCs(m.getRecipients(Message.RecipientType.CC));
						
						mm.setBCCs(m.getRecipients(Message.RecipientType.BCC));
						
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
			
			try {
				FileOutputStream fileOut = new FileOutputStream(username + ".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(emailModel);
				out.close();
				fileOut.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			emailDownloader.signOut();
			return emailModel;
		}
	}
	
	private EmailModel refresh(){
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
					
					mm.setFrom(m.getFrom());
					
					mm.setTOs(m.getRecipients(Message.RecipientType.TO));
					
					mm.setCCs(m.getRecipients(Message.RecipientType.CC));
					
					mm.setBCCs(m.getRecipients(Message.RecipientType.BCC));
					
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
		
		try {
			FileOutputStream fileOut = new FileOutputStream(username + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(emailModel);
			out.close();
			fileOut.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		emailDownloader.signOut();
		return emailModel;
	}
	
	private void showSignIn() {
		textFieldUserName.setText("");
		textFieldPassword.setText("");
		signInWindow.setVisible(true);
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
		
		mainWindow = new JFrame("SecureMail");
		mainWindow.setBounds(0, 0, 1000, 650);
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.getContentPane().setLayout(new BorderLayout(0, 0));
		mainWindow.setVisible(true);
		
		toolbar = new JPanel();
		mainWindow.getContentPane().add(toolbar, BorderLayout.NORTH);
		toolbar.setPreferredSize(new Dimension(10, 45));
		
		btnNewMessage = new JButton("New Message");
		
		btnRefresh = new JButton("Refresh");
		
		btnChangeAccount = new JButton("Change Account");
		
		btnGenerateKeys = new JButton("Generate Keys");
		btnGenerateKeys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GenerateKeysFrame frame = new GenerateKeysFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			}
		});
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
		mainWindow.getContentPane().add(splitPaneFoldersEmails, BorderLayout.CENTER);
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
		
		btnDecrypt = new JButton("Decrypt & Verify");
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listMessages.getSelectedIndex() != -1) {
		        	MessageModel mm = emailModel.getFolders().get(listFolders.getSelectedIndex()).getMessages().get(listModelMessages.getSize()-listMessages.getSelectedIndex()-1);
		        	
		        	if (receiveMessageFrame==null) {
						receiveMessageFrame = new ReceiveMessageFrame();
					} 
		        	
		        	String from = "";
		        	if (mm.getFrom()!=null) {
		        		for (Address s : mm.getFrom()) {
		        			from += s+" ";
		        		}
		        	}

		        	String CCs = "";
		        	if (mm.getCCs()!=null) {
		        		for (Address s : mm.getCCs()) {
		        			CCs += s+" ";
		        		}
		        	}
		        	
		        	String BCCs = "";
		        	if (mm.getBCCs()!=null) {
		        		for (Address s : mm.getBCCs()) {
		        			BCCs += s+" ";
		        		}
		        	}
					
		        	receiveMessageFrame.setField(from, CCs, BCCs, mm.getSubject(), mm.getContent());
					receiveMessageFrame.setVisible(true);
		        }
			}
		});
		panelContentActions.add(btnDecrypt);
		
		scrollPaneFolders = new JScrollPane();
		splitPaneFoldersEmails.setLeftComponent(scrollPaneFolders);
		
		listModelFolders = new DefaultListModel<String>();
		listFolders = new JList<String>(listModelFolders);
		scrollPaneFolders.setViewportView(listFolders);
		
		listModelMessages = new DefaultListModel<String>();
		listMessages = new JList<String>(listModelMessages);
		scrollPaneEmails.setViewportView(listMessages);
	}
	
	private void initializeSignIn() {
		signInWindow = new JFrame("Sign In");
		signInWindow.setBounds(100, 100, 300, 200);
		signInWindow.setLocationRelativeTo(null);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		signInWindow.setContentPane(contentPane);
		
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
		signInWindow.setVisible(true);
	}


}
