package emailprocessing;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

import models.MessageModel;

public class EmailSender {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EmailSender es = new EmailSender();
		
		MessageModel mm = new MessageModel();
		try {
			mm.setFrom(InternetAddress.parse("tubes.kripto.a@gmail.com", false));
			mm.setTOs(InternetAddress.parse("tubes.kripto.b@gmail.com", false));
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mm.setSentDate(new Date());
		mm.setSubject("Ini Subject");
		mm.setContent("Ini Content");
		
		es.sendEmail("smtps", "smtp.gmail.com", "tubes.kripto.a", "tubeskript0", mm);
	}
	
	public void sendEmail(String prot, String host, String username, String password, MessageModel mm) {
		Properties props = System.getProperties();
		props.put("mail." + prot + ".host", host);
		props.put("mail." + prot + ".auth", "true");
		Session session = Session.getInstance(props, null);
		session.setDebug(true);
		
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom();
			msg.setRecipients(Message.RecipientType.TO, mm.getTOs());
			if (mm.getCCs()!=null) {
				msg.setRecipients(Message.RecipientType.CC, mm.getCCs());
			}
			if (mm.getBCCs()!=null) {
				msg.setRecipients(Message.RecipientType.BCC, mm.getBCCs());
			}
			msg.setSentDate(mm.getSentDate());
			msg.setSubject(mm.getSubject());
			msg.setText(mm.getContent());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			SMTPTransport t = (SMTPTransport)session.getTransport(prot);
			t.connect(host, username, password);
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
