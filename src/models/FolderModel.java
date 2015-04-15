package models;

import java.util.ArrayList;
import java.util.List;

public class FolderModel {

	private String name;
	private List<MessageModel> messages;
	
	public FolderModel(String name) {
		this.name = name;
		messages = new ArrayList<MessageModel>();
	}
	
	public void addMessageModel(MessageModel mm) {
		messages.add(mm);
	}
	
	public String getName() {
		return name;
	}
	
	public List<MessageModel> getMessages() {
		return messages;
	}
}
