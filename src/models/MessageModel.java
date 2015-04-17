package models;

import java.util.Date;

import javax.mail.Address;

public class MessageModel {

	private Address[] from;
	private Address[] TOs;
	private Address[] CCs;
	private Address[] BCCs;
	private Date sentDate;
	private String subject;
	private String content;
	
	public String toString() {
		String from = "";
    	if (getFrom()!=null) {
    		for (Address s : getFrom()) {
    			from += s+" ";
    		}
    	}
    	String TOs = "";
    	if (getTOs()!=null) {
    		for (Address s : getTOs()) {
    			TOs += s+" ";
    		}
    	}
    	String CCs = "";
    	if (getCCs()!=null) {
    		for (Address s : getCCs()) {
    			CCs += s+" ";
    		}
    	}
    	String BCCs = "";
    	if (getBCCs()!=null) {
    		for (Address s : getBCCs()) {
    			BCCs += s+" ";
    		}
    	}
    	
    	return ("FROM : "+from+"\nTO : "+TOs+"\nCC : "+CCs+"\nBCC : "+BCCs+"\nSENT : "+sentDate+"\nSUBJECT : "+subject+"\nCONTENT :\n"+content);
	}
	
	public Address[] getFrom() {
		return from;
	}
	public void setFrom(Address[] from) {
		this.from = from;
	}
	public Address[] getTOs() {
		return TOs;
	}
	public void setTOs(Address[] TOs) {
		this.TOs = TOs;
	}
	public Address[] getCCs() {
		return CCs;
	}
	public void setCCs(Address[] CCs) {
		this.CCs = CCs;
	}
	public Address[] getBCCs() {
		return BCCs;
	}
	public void setBCCs(Address[] BCCs) {
		this.BCCs = BCCs;
	}
	public Date getSentDate() {
		return sentDate;
	}
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
