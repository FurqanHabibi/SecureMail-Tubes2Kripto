package models;

import java.util.Date;

public class MessageModel {

	private String[] from;
	private String[] TOs;
	private String[] CCs;
	private String[] BCCs;
	private Date sentDate;
	private String subject;
	private String content;
	
	public String toString() {
		String from = "";
    	if (getFrom()!=null) {
    		for (String s : getFrom()) {
    			from += s+" ";
    		}
    	}
    	String TOs = "";
    	if (getTOs()!=null) {
    		for (String s : getTOs()) {
    			TOs += s+" ";
    		}
    	}
    	String CCs = "";
    	if (getCCs()!=null) {
    		for (String s : getCCs()) {
    			CCs += s+" ";
    		}
    	}
    	String BCCs = "";
    	if (getBCCs()!=null) {
    		for (String s : getBCCs()) {
    			BCCs += s+" ";
    		}
    	}
    	
    	return ("FROM : "+from+"\nTO : "+TOs+"\nCC : "+CCs+"\nBCC : "+BCCs+"\nSENT : "+sentDate+"\nSUBJECT : "+subject+"\nCONTENT :\n"+content);
	}
	
	public String[] getFrom() {
		return from;
	}
	public void setFrom(String[] from) {
		this.from = from;
	}
	public String[] getTOs() {
		return TOs;
	}
	public void setTOs(String[] TOs) {
		this.TOs = TOs;
	}
	public String[] getCCs() {
		return CCs;
	}
	public void setCCs(String[] CCs) {
		this.CCs = CCs;
	}
	public String[] getBCCs() {
		return BCCs;
	}
	public void setBCCs(String[] BCCs) {
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
