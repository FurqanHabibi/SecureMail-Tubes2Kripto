package emailprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class EmailDownloader {
	
	private String imapProt;
	private String imapHost;
	private String username;
	private String password;
	private Store store = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EmailDownloader ed = new EmailDownloader("imaps", "imap.gmail.com", "tubes.kripto.a", "tubeskript0");
		int mode=2;
		if (mode==1) {
			ed.signIn();
		}
		else if (mode==2) {
			ed.signIn();
			Folder[] folders = ed.getFolders();
			for (Folder f : folders) {
				System.out.println(f.getFullName());
			}
		}
		else if (mode==3) {
			ed.signIn();
			Folder folder = ed.openFolder("INBOX");
			Message[] messages = ed.getMessagesInFolder(folder);
			for (Message m : messages) {
				try {
					System.out.println(m.getSubject());
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ed.closeFolder(folder);
		}
	}
	
	public EmailDownloader(String imapProt, String imapHost, String username, String password) {
		this.imapProt = imapProt;
		this.imapHost = imapHost;
		this.username = username;
		this.password = password;
	}
	
	public void signIn() {
		Properties props = System.getProperties();
		Session session = Session.getInstance(props, null);
		//session.setDebug(true);
		try {
			store = session.getStore(imapProt);
			store.connect(imapHost, -1, username, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Folder[] getFolders() {
		try {
			return getFoldersRecursive(store.getDefaultFolder());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private Folder[] getFoldersRecursive(Folder root) {
		List<Folder> folderList = new ArrayList<Folder>();
		try {
			Folder[] folders = root.list();
			for (Folder f : folders) {
				if ((f.getType() & Folder.HOLDS_MESSAGES) != 0) {
					folderList.add(f);
				}
				else {
					folderList.addAll(Arrays.asList(getFoldersRecursive(f)));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return folderList.toArray(new Folder[folderList.size()]);
	}
	
	public Folder openFolder(String folderName) {
		try {
			Folder folder = store.getFolder(folderName);
			folder.open(Folder.READ_ONLY);
			return folder;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Message[] getMessagesInFolder(Folder folder) {
		try {
			Message[] m = folder.getMessages();
			return m;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void closeFolder(Folder folder) {
		try {
			folder.close(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
